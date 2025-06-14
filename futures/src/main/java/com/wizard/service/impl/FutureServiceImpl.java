package com.wizard.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.binance.connector.futures.client.exceptions.BinanceClientException;
import com.binance.connector.futures.client.exceptions.BinanceConnectorException;
import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import com.wizard.common.enums.ContractTypeEnum;
import com.wizard.common.enums.ExchangeEnum;
import com.wizard.common.enums.IntervalEnum;
import com.wizard.common.enums.PushEnum;
import com.wizard.common.model.MarketQuotation;
import com.wizard.common.model.Supertrend;
import com.wizard.common.utils.DataTransformationUtil;
import com.wizard.common.utils.IndicatorCalculateUtil;
import com.wizard.common.utils.SupertrendUtil;
//import com.wizard.common.utils.TradingViewSuperTrendUtil;
import com.wizard.component.CheckComponent;
import com.wizard.component.GlobalListComponent;
import com.wizard.config.PrivateConfig;
import com.wizard.model.dto.SymbolLineDTO;
import com.wizard.model.vo.SymbolBollVO;
import com.wizard.model.vo.SymbolFundingRateVO;
import com.wizard.push.serivce.PushService;
import com.wizard.service.FutureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import xlc.quant.data.indicator.calculator.BOLL;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author 巫师
 * @date 2024-05-07
 * @desc
 */
@Slf4j
@Service
public class FutureServiceImpl implements FutureService {

	@Resource
	PushService pushService;

	@Resource
	GlobalListComponent globalListComponent;

	@Value("${BINANCE.API_KEY}")
	private String API_KEY;

	@Value("${BINANCE.SECRET_KEY}")
	private String SECRET_KEY;

	@Value("${PROXY.URL}")
	private String PROXY_URL;

	/**
	 * 检测是否存在新增标的
	 *
	 * @param logId 日志ID
	 */
	@Override
	public void checkNewSymbol(Long logId) {
		// 获取全部交易标的
		List<String> symbolList = getExchangeInfo(logId);
		List<String> alreadyList = globalListComponent.getGlobalList();
		boolean isEqual = symbolList.stream().allMatch(alreadyList::contains);
		log.info("日志ID:{},检测是否存在新增标的-当前已存在标的:{}",logId,JSONObject.toJSONString(alreadyList));
		if(!isEqual){
			// 最新可交易列表中存在的标的
			List<String> newSymbolList = symbolList.stream()
					.filter(element -> !alreadyList.contains(element))
					.collect(Collectors.toList());

			// 最新可交易列表中存在的标的
			List<String> deleteSymbolList = alreadyList.stream()
					.filter(element -> !symbolList.contains(element))
					.collect(Collectors.toList());
			// 存在新增标的
			if(!newSymbolList.isEmpty()){
				for (String symbol : newSymbolList) {
					Boolean pushFlag = pushService.pushFeiShu(logId,symbol, DateUtil.now(),"", ExchangeEnum.EXCHANGE_BINANCE, PushEnum.FUTURES_SYMBOL_ADD);
					if(pushFlag){
						log.info("日志ID:{},标的:{},推送消息成功",logId,symbol);
					}
				}
			}
			// 存在去除的标的
			if(!deleteSymbolList.isEmpty()){
				for (String symbol : newSymbolList) {
					Boolean pushFlag = pushService.pushFeiShu(logId,symbol, DateUtil.now(),"", ExchangeEnum.EXCHANGE_BINANCE, PushEnum.FUTURES_SYMBOL_DELETE);
					if(pushFlag){
						log.info("日志ID:{},标的:{},推送消息成功",logId,symbol);
					}
				}
			}
			// 清空列表
			globalListComponent.removeAll(logId);

			// 重新添加数据
			globalListComponent.addToGlobalList(logId,symbolList);
		}
	}

	@Resource
	CheckComponent checkComponent;

	/**
	 * 异步方法计算币安合约持仓量
	 */
	@Async
	@Override
	public void openInterestStatistics(Long logId) {
		log.info("日志ID:{},计算合约持仓量-进入合约持仓量计算方法",logId);
		// 以下方法需要抽取为公共方法,实现传入不同标的以及对应计算规则,动态计算是否符合通知条件
		// 获取全部交易标的
		List<String> symbolList = globalListComponent.getGlobalList();
		if(symbolList.isEmpty()){
			log.info("日志ID:{},计算合约持仓量-标的列表为空,流程结束",logId);
			return;
		}
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2);
		symbolList.stream().forEach(symbol ->{
			executor.submit(()->{
				checkComponent.checkInterestStatistics(logId,symbol);
			});
		});
		executor.shutdown();
		try {
			boolean terminated = executor.awaitTermination(30, TimeUnit.SECONDS);
			if (!terminated) {
				log.error("日志ID:{},计算合约持仓量-查询动态节点线程池在指定时间内未能成功关闭","");
			}
		} catch (InterruptedException e) {
			log.error("日志ID:{},计算合约持仓量--查询动态节点 Error waiting for threads to finish:{}","",e.getMessage());
		}
	}

	@Override
	public List<String> getExchangeInfo(Long logId) {
		List<String> resultList = new ArrayList<>();
		UMFuturesClientImpl client = new UMFuturesClientImpl(API_KEY,SECRET_KEY,PROXY_URL);

		try {
			String result = client.market().exchangeInfo();
			JSONObject jsonObject = JSONObject.parseObject(result);
			JSONArray jsonArray = jsonObject.getJSONArray("symbols");
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject1 = jsonArray.getJSONObject(i);
				// 获取合约类型
				String contractType = jsonObject1.getString("contractType");
				// 获取交易对名称
				String symbol = jsonObject1.getString("symbol");
				if("PERPETUAL".equals(contractType)&& !symbol.contains("USDC") && symbol.contains("USDT")){
					resultList.add(symbol);
				}
			}
			log.info("日志ID:{},当前标的数量:{}",logId,resultList.size());
		} catch (BinanceConnectorException e) {
			log.error("fullErrMessage: {}", e.getMessage(), e);
		} catch (BinanceClientException e) {
			log.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}",
					e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
		}

		return resultList;
	}

	/**
	 * 检测资金费率
	 *
	 * @param logId		日志ID
	 */
	@Override
	public void getRate(Long logId) {
		LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
		UMFuturesClientImpl client = new UMFuturesClientImpl(API_KEY,SECRET_KEY,PROXY_URL);

		try {
			String result = client.market().fundingRate(parameters);
			List<SymbolFundingRateVO> symbolFundingRateVOList = JSONArray.parseArray(result,SymbolFundingRateVO.class);
			// 过滤出负费率标的
			BigDecimal bigDecimal = new BigDecimal("0");
			List<SymbolFundingRateVO> negativeRateList = symbolFundingRateVOList.stream()
					.filter(item -> item.getFundingRate().compareTo(bigDecimal)<0)
					.sorted(Comparator.comparing(SymbolFundingRateVO::getFundingRate))
					.collect(Collectors.toList());
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("警报类型:资金费率").append("\n");
			if(!negativeRateList.isEmpty()){
				if(negativeRateList.size() > 5){
					negativeRateList = negativeRateList.subList(0,5);
				}
				stringBuffer.append("负费率TOP5标的").append("\n");
				for (SymbolFundingRateVO symbolFundingRateVO:negativeRateList) {
					BigDecimal rateResult = symbolFundingRateVO.getFundingRate().multiply(new BigDecimal("100"));
					stringBuffer.append("标的:").append(symbolFundingRateVO.getSymbol()).append(",资金费率:").append(String.format("%.4f%%",rateResult)).append("\n");
				}
			}
			// 过滤出正费率
			List<SymbolFundingRateVO> positiveRateList  = symbolFundingRateVOList.stream()
					.filter(item -> item.getFundingRate().compareTo(bigDecimal)>0)
					.sorted(Comparator.comparing(SymbolFundingRateVO::getFundingRate))
					.collect(Collectors.toList());
			if(!positiveRateList.isEmpty()){
				if(positiveRateList.size() > 5){
					positiveRateList = positiveRateList.subList(0,5);
				}
				stringBuffer.append("\n").append("正费率TOP5标的").append("\n");
				for (SymbolFundingRateVO symbolFundingRateVO:positiveRateList) {
					BigDecimal rateResult = symbolFundingRateVO.getFundingRate().multiply(new BigDecimal("100"));
					stringBuffer.append("标的:").append(symbolFundingRateVO.getSymbol()).append(",资金费率:").append(String.format("%.4f%%",rateResult)).append("\n");
				}
			}
			stringBuffer.append("时间:").append(DateUtil.now());
			pushService.pushManySymbol(logId,stringBuffer.toString());
		} catch (BinanceConnectorException e) {
			log.error("fullErrMessage: {}", e.getMessage(), e);
		} catch (BinanceClientException e) {
			log.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}",
					e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
		}
	}

	/**
	 * 获取连续合约数据
	 *
	 * @param symbolLineDTO 标的
	 * @return
	 */
	@Override
	public List<MarketQuotation> getContinuousKLines(SymbolLineDTO symbolLineDTO) {
		String url = "/fapi/v1/continuousKlines";
	    String targetUrl = PrivateConfig.UM_BASE_URL + url;
		StringBuffer params = new StringBuffer();
		params.append("?pair=").append(symbolLineDTO.getSymbol());
		if(StrUtil.isBlank(symbolLineDTO.getContractType())){
			symbolLineDTO.setContractType(ContractTypeEnum.PERPETUAL.getCode());
		}
		if(StrUtil.isBlank(symbolLineDTO.getInterval())){
			symbolLineDTO.setInterval(IntervalEnum.FOUR_HOUR.getCode());
		}
		if(ObjectUtil.isNull(symbolLineDTO.getLimit())){
			symbolLineDTO.setLimit(1000);
		}
		// 合约类型
		params.append("&contractType=").append(symbolLineDTO.getContractType());
		// 时间级别
		params.append("&interval=").append(symbolLineDTO.getInterval());
		// 限制数量
		params.append("&limit=").append(symbolLineDTO.getLimit());
		String resultUrl = targetUrl+params;
		log.info("开始请求,目标地址:{}",resultUrl);

		String result = HttpRequest.get(resultUrl).execute().body();
		//log.info("原始结果:{}",result);
		List<MarketQuotation> marketQuotationList = DataTransformationUtil.transform(symbolLineDTO.getSymbol(), result);
		// 数据按照收盘时间排序
		//marketQuotationList = marketQuotationList.stream().sorted(Comparator.comparing(MarketQuotation::getCloseTime)).collect(Collectors.toList());
		log.info("计算全部指标，开始");
		// 计算指标
		IndicatorCalculateUtil.multipleIndicatorCalculate(marketQuotationList,2);
		log.info("计算全部指标，结束");
		return marketQuotationList;
	}

	/**
	 * 波动预警
	 *
	 * @param logId 日志ID
	 */
	@Override
	public void fluctuate(Long logId) {
		List<String> symbolList = globalListComponent.getGlobalList();
		symbolList.stream().forEach(symbol->{
			// 计算成交量
			try {
				SymbolLineDTO symbolLineDTO = SymbolLineDTO.builder()
						.symbol(symbol)
						.interval(IntervalEnum.FIVE_MINUTE.getCode())
						.contractType(ContractTypeEnum.PERPETUAL.getCode())
						.limit(4)
						.build();
				// 获取交易数据
				List<MarketQuotation> marketQuotationList = getContinuousKLines(symbolLineDTO);
				// 按照收盘时间倒序排序
				marketQuotationList = marketQuotationList.stream().sorted(Comparator.comparing(MarketQuotation::getCloseTime).reversed()).collect(Collectors.toList());
			} catch (Exception e) {
				log.error("日志ID:{},计算交易量错误",logId);
			}
		});
	}

	/**
	 * 成交量监控
	 * @param logId 日志ID
	 */
	@Override
	public void monitorVolume(Long logId) {
	   List<String> symbolList = globalListComponent.getGlobalList();
	   StringBuffer stringBuffer = new StringBuffer();
	   stringBuffer.append("警报类型: 交易量异常【庄神指标】").append("\n").append("\n");
	   stringBuffer.append("说明：使用最近三根已收盘的K线数据,收盘价需高于前一根K线的收盘价").append("\n");
	   stringBuffer.append("强势标的:成交量连续是前一根K线的3倍及以上").append("\n");
	   stringBuffer.append("一般标的:最近一根收盘K线的成交量是前一根K线的3倍及以上").append("\n").append("\n");
	   StringBuffer storge = new StringBuffer();
	   storge.append("强势标的:").append("\n");
	   StringBuffer other = new StringBuffer();
	   other.append("一般标的:").append("\n");
	   AtomicReference<Boolean> pushFlag = new AtomicReference<>(false);

	   AtomicReference<Boolean> storgeFlag = new AtomicReference<>(false);
	   AtomicReference<Boolean> otherFlag = new AtomicReference<>(false);
	   symbolList.stream().forEach(symbol->{
		   // 计算成交量
		   try {
		   		SymbolLineDTO symbolLineDTO = SymbolLineDTO.builder()
				   .symbol(symbol)
				   .interval(IntervalEnum.ONE_HOUR.getCode())
				   .contractType(ContractTypeEnum.PERPETUAL.getCode())
				   .limit(4)
				   .build();
			   // 获取交易数据
			   List<MarketQuotation> marketQuotationList = getContinuousKLines(symbolLineDTO);
			   // 按照收盘时间倒序排序
			   marketQuotationList = marketQuotationList.stream().sorted(Comparator.comparing(MarketQuotation::getCloseTime).reversed()).collect(Collectors.toList());
			   BigDecimal volumeOne = marketQuotationList.get(1).getBigDecimalVolume();
			   BigDecimal volumeTwo = marketQuotationList.get(2).getBigDecimalVolume();
			   BigDecimal volumeThree = marketQuotationList.get(3).getBigDecimalVolume();
			   BigDecimal one_two = volumeOne.divide(volumeTwo,2);
			   BigDecimal two_three = volumeTwo.divide(volumeThree,2);
			   // 获取涨跌数据
			   BigDecimal changeOne = getRateBigDecimal(marketQuotationList.get(1).getBigDecimalClose(),marketQuotationList.get(1).getBigDecimalOpen());
			   Boolean flagOne = false;
			   if(changeOne.compareTo(BigDecimal.ZERO) > 0){
				   flagOne = true;
			   }
			   BigDecimal changeTwo = getRateBigDecimal(marketQuotationList.get(2).getBigDecimalClose(),marketQuotationList.get(2).getBigDecimalOpen());
			   Boolean flagTwo = false;
			   if(changeTwo.compareTo(BigDecimal.ZERO) > 0){
				   flagTwo = true;
			   }
			   BigDecimal changeThree = getRateBigDecimal(marketQuotationList.get(3).getBigDecimalClose(),marketQuotationList.get(3).getBigDecimalOpen());
			   Boolean flagThree = false;
			   if(changeThree.compareTo(BigDecimal.ZERO) > 0){
				   flagThree = true;
			   }
			   // 前一根收盘交易量/前二收盘交易量
			   if(one_two.compareTo(new BigDecimal("3")) > 0 && flagOne && flagTwo){
				   pushFlag.set(true);
				   if(two_three.compareTo(new BigDecimal("3")) > 0 && flagThree){
						// 强烈建议
					   storge.append(symbol).append("、");
					   storgeFlag.set(true);
				   } else {
					   // 一般情况
					   other.append(symbol).append("、");
					   otherFlag.set(true);
				   }
			   } else {
				   // 不建议
			   }
		   } catch (Exception e) {
			   log.error("日志ID:{},计算交易量错误",logId);
		   }
	   });
	   if(pushFlag.get()){
		   if(storgeFlag.get()){
			   stringBuffer.append(storge.append("\n"));
		   } else {
			   stringBuffer.append("强势标的: 无").append("\n");
		   }
		   if(otherFlag.get()){
			   stringBuffer.append(other.append("\n"));
		   } else {
			   stringBuffer.append("一般标的: 无").append("\n");
		   }
		   stringBuffer.append("\n").append("推送时间:").append(DateUtil.now());
		   pushService.pushManySymbol(logId,stringBuffer.toString());
	   }
	}

	/**
	 * 指标信号通知
	 *
	 * @param logId 日志ID
	 */
	@Override
	public void indicatorSignal(Long logId) {
		// 获取所有的交易对
		List<String> symbolList = globalListComponent.getGlobalList();
		symbolList.add("SOLUSDT");

		List<SymbolBollVO> symbolBollVOList = new ArrayList<>();
		int count = 0;
		for (String symbol:symbolList) {
			try {
				List<SymbolBollVO> tempSymbolBollVOList = monitorBollIndicator(logId,symbol);
				symbolBollVOList.addAll(tempSymbolBollVOList);
			} catch (Exception e){
				log.error("日志ID:{},标的:{},查询报错",logId,symbol);
			}
			count++;
			log.info("日志ID:{},剩余数量:{}",logId,symbolList.size()-count);
		}
		List<SymbolBollVO> strongList = symbolBollVOList.stream().filter(item -> item.getUFlag() && item.getMFlag()).collect(Collectors.toList());
		List<SymbolBollVO> weakList = symbolBollVOList.stream().filter(item -> !item.getMFlag()).collect(Collectors.toList());


		// 分别取值
		List<SymbolBollVO> oneHourMoreThanM = symbolBollVOList.stream().filter(item -> item.getInterval().equals(IntervalEnum.ONE_HOUR.getName()) && item.getMFlag()).collect(Collectors.toList());
		List<SymbolBollVO> oneHourLessThanM = symbolBollVOList.stream().filter(item -> item.getInterval().equals(IntervalEnum.ONE_HOUR.getName()) && !item.getMFlag()).collect(Collectors.toList());

		List<SymbolBollVO> fourHourMoreThanM = symbolBollVOList.stream().filter(item -> item.getInterval().equals(IntervalEnum.FOUR_HOUR.getName()) && item.getMFlag()).collect(Collectors.toList());
		List<SymbolBollVO> fourHourLessThanM = symbolBollVOList.stream().filter(item -> item.getInterval().equals(IntervalEnum.FOUR_HOUR.getName()) && !item.getMFlag()).collect(Collectors.toList());

		List<SymbolBollVO> dayHourMoreThanM = symbolBollVOList.stream().filter(item -> item.getInterval().equals(IntervalEnum.ONE_DAY.getName()) && item.getMFlag()).collect(Collectors.toList());
		List<SymbolBollVO> dayHourLessThanM = symbolBollVOList.stream().filter(item -> item.getInterval().equals(IntervalEnum.ONE_DAY.getName()) && !item.getMFlag()).collect(Collectors.toList());

		List<SymbolBollVO> hourThanM = symbolBollVOList.stream().filter(item -> item.getInterval().equals(IntervalEnum.FOUR_HOUR.getName()) && item.getMFlag()).collect(Collectors.toList());
		List<SymbolBollVO> dayLessM = symbolBollVOList.stream().filter(item -> item.getInterval().equals(IntervalEnum.ONE_DAY.getName()) && !item.getMFlag()).collect(Collectors.toList());



		//SymbolLineDTO symbolLineDTO = SymbolLineDTO.builder()
		//		.symbol("BTCUSDT")
		//		.contractType(ContractTypeEnum.PERPETUAL.getCode())
		//		.interval(IntervalEnum.ONE_HOUR.getCode())
		//		.limit(1000).build();
		//List<MarketQuotation> marketQuotationList = getContinuousKLines(symbolLineDTO);
		//// 行情数据根据收盘时间降序排序
		//marketQuotationList.sort(Comparator.comparing(MarketQuotation::getCloseTime).reversed());
		//
		//// 获取前4K数据
		//List<MarketQuotation> lastList = marketQuotationList.subList(0,4);
		//
		//// 筛选TD指标
		//List<MarketQuotation> tdList = lastList.stream().filter(item -> item.getTd() != null && (item.getTd() < -8 || item.getTd() > 8) && DateUtil.between(DateUtil.date(item.getCloseTime().toLocalDate()), new Date(),DateUnit.HOUR) < 24).collect(Collectors.toList());
		//// 存在TD信号
		//if(CollUtil.isNotEmpty(tdList)){
		//	tdList.stream().forEach(item ->{
		//		log.info("日志ID:{},开盘时间:{},收盘时间:{},TD数据:{}",logId,item.getTimestamp(),item.getCloseTime(),item.getTd());
		//	});
		//}
		//// 计算是否触及MA120
		//lastList.stream().forEach(item ->{
		//	log.info("日志ID:{},开盘时间:{},收盘时间:{},当前价格:{},MA120数据:{},收盘价距离MA幅度:{}%",logId,item.getTimestamp(),item.getCloseTime(),item.getClose(),item.getMa120(),getRate(item.getClose(),item.getMa120()));
		//	BigDecimal result = getRate(item.getClose(),item.getMa120());
		//	if(result.compareTo(new BigDecimal("1")) < 0){
		//		log.info("日志ID:{},开盘时间:{},收盘时间:{},当前价格:{},MA120数据:{},幅度不及1%",logId,item.getTimestamp(),item.getCloseTime(),item.getClose(),item.getMa120());
		//	}
		//});
		//
		//// 筛选CCI指标
		//List<MarketQuotation> cciList = lastList.stream().filter(item -> item.getCci14() != null && (item.getCci14().getValue() < -140 || item.getCci14().getValue() > 140)).collect(Collectors.toList());
		//if(CollUtil.isNotEmpty(cciList)){
		//	cciList.stream().forEach(item ->{
		//		log.info("日志ID:{},开盘时间:{},收盘时间:{},当前价格:{},CCI数据:{}",logId,item.getTimestamp(),item.getCloseTime(),item.getClose(),item.getCci14());
		//	});
		//}
	}

	/**
	 * 多周期布林带计算
	 * @param logId			日志ID
	 * @param symbol		标的
	 * @return 				布林指标结果
	 */
	public List<SymbolBollVO> monitorBollIndicator(Long logId, String symbol) {
		log.info("日志ID:{},执行布林带监控逻辑,标的:{}",logId,symbol);
		List<SymbolBollVO> symbolBollVOList = new ArrayList<>();
		// 设置时间级别 1小时 4小时、1天
		List<IntervalEnum> intervalList = Arrays.asList(IntervalEnum.FOUR_HOUR);
		for (IntervalEnum intervalEnum:intervalList) {
			SymbolLineDTO symbolLineDTO = SymbolLineDTO.builder()
					.symbol(symbol)
					.contractType(ContractTypeEnum.PERPETUAL.getCode())
					.interval(intervalEnum.getCode())
					.limit(200).build();
			List<MarketQuotation> marketQuotationList = getContinuousKLines(symbolLineDTO);
			// 行情数据根据收盘时间降序排序
			marketQuotationList.sort(Comparator.comparing(MarketQuotation::getCloseTime).reversed());

			if(CollUtil.isNotEmpty(marketQuotationList)){
				// 获取前4K数据
				List<MarketQuotation> lastList = marketQuotationList.subList(0,1);
				// 获取符合条件的布林带数据
				List<MarketQuotation> bollList = lastList.stream().filter(item -> item.getBoll() != null).collect(Collectors.toList());
				if(CollUtil.isNotEmpty(bollList)){
					bollList.stream().forEach(item ->{
						BOLL boll = item.getBoll();
						//log.info("日志ID:{},布林带监控逻辑,标的:{},时间周期:{},开盘时间:{},收盘时间:{},当前价格:{},BOLL数据:{}",logId,symbol,intervalEnum.getName(),item.getTimestamp(),item.getCloseTime(),item.getClose(),item.getBoll());
						// 判断当前价格距离中轨的幅度
						BigDecimal result = getRate(item.getClose(),boll.getM());
						Boolean amplitude = Boolean.FALSE;
						if(result.compareTo(new BigDecimal("3")) < 0){
							//log.info("日志ID:{},布林带监控逻辑,标的:{},时间周期:{},开盘时间:{},收盘时间:{},当前价格:{},布林中轨价格:{},幅度不及1%",logId,symbol,intervalEnum.getName(),item.getTimestamp(),item.getCloseTime(),item.getClose(),item.getMa120());
							amplitude = Boolean.TRUE;
						}
						SymbolBollVO buildSymbolBollVO = SymbolBollVO.builder().symbol(symbol).interval(intervalEnum.getName())
								.closeTime(item.getCloseTime()).close(item.getClose())
								.u(boll.getU()).m(boll.getM()).d(boll.getD())
								.uFlag(item.getClose() >= boll.getU() ? Boolean.TRUE : Boolean.FALSE)
								.mFlag(item.getClose() >= boll.getM() ? Boolean.TRUE : Boolean.FALSE)
								.dFlag(item.getClose() < boll.getD() ? Boolean.TRUE : Boolean.FALSE)
								.mAmplitude(amplitude)
								.build();
						symbolBollVOList.add(buildSymbolBollVO);
					});
				}
			}

			ThreadUtil.sleep(100,TimeUnit.MILLISECONDS);
		}
		return symbolBollVOList;

	}

	/**
	 *
	 * @param priceA
	 * @param priceB
	 * @return
	 */
	private BigDecimal getRateBigDecimal(BigDecimal priceA, BigDecimal priceB) {
		BigDecimal tempA = priceA.subtract(priceB);
		BigDecimal tempB = tempA.divide(priceB,4, BigDecimal.ROUND_HALF_UP);
		BigDecimal res = tempB.multiply(new BigDecimal("100"));
		res.setScale(2, BigDecimal.ROUND_HALF_UP);
		return res;
	}
	private BigDecimal getRate(Double priceA, Double priceB) {
		Double res = (Math.abs(priceA - priceB) / priceB) * 100;
		BigDecimal bigDecimal = new BigDecimal(res).setScale(2, BigDecimal.ROUND_HALF_UP);
		return bigDecimal;
	}


	/**
	 * 超级趋势计算
	 *
	 * @param id
	 */
	@Override
	public void supertrend(long id) {
		SymbolLineDTO symbolLineDTO = SymbolLineDTO.builder()
				.symbol("BTCUSDT")
				.contractType(ContractTypeEnum.PERPETUAL.getCode())
				.interval(IntervalEnum.ONE_HOUR.getCode())
				.limit(500).build();
		List<MarketQuotation> marketQuotationList = getContinuousKLines(symbolLineDTO);
		log.info("{}", JSONObject.toJSONString(marketQuotationList));
	}
}
