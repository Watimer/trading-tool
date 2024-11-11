package com.wizard.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.excel.EasyExcel;
import com.wizard.common.enums.ContractTypeEnum;
import com.wizard.common.enums.IntervalEnum;
import com.wizard.common.model.MarketQuotation;
import com.wizard.common.utils.DataTransformationUtil;
import com.wizard.component.GlobalListComponent;
import com.wizard.config.PrivateConfig;
import com.wizard.model.dto.SymbolLineDTO;
import com.wizard.model.vo.SymbolBollVO;
import com.wizard.service.ExcelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 岳耀栎
 * @date 2024-11-11
 * @desc
 */
@Slf4j
@Service
public class ExcelServiceImpl implements ExcelService {

	@Resource
	GlobalListComponent globalListComponent;

	@Override
	public boolean excelService(String exportPath) {
		List<String> symbolList = globalListComponent.getGlobalList();

		// 获取交易数据
		List<List<String>> data = new ArrayList<>();


		List<MarketQuotation> marketQuotationListTemp = new ArrayList<>();

		for (String symbol:symbolList){
			List<String> bigDecimalList = new ArrayList<>();
			SymbolLineDTO symbolLineDTO = SymbolLineDTO.builder()
					.symbol(symbol)
					.interval(IntervalEnum.FOUR_HOUR.getCode())
					.contractType(ContractTypeEnum.PERPETUAL.getCode())
					.limit(246)
					.build();
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
			// 合约类型
			params.append("&contractType=").append(symbolLineDTO.getContractType());
			// 时间级别
			params.append("&interval=").append(symbolLineDTO.getInterval());
			// 限制数量
			params.append("&limit=").append(symbolLineDTO.getLimit());
			String resultUrl = targetUrl+params;
			String result = HttpRequest.get(resultUrl).execute().body();
			List<MarketQuotation> marketQuotationList = DataTransformationUtil.transform(symbolLineDTO.getSymbol(), result);
			// 数据按照收盘时间排序
			marketQuotationList = marketQuotationList.stream().sorted(Comparator.comparing(MarketQuotation::getCloseTime)).collect(Collectors.toList());

			if(symbol.equals("BTCUSDT")){
				marketQuotationListTemp = marketQuotationList;
			}
			log.info("获取:{} 行情结果",symbol);
			bigDecimalList.add(symbol);
			for (MarketQuotation marketQuotation : marketQuotationList) {
				Double d = marketQuotation.getClose() - marketQuotation.getOpen();
				BigDecimal bigDecimal = new BigDecimal(d);
				BigDecimal bigDecimalOpen = new BigDecimal(marketQuotation.getOpen());
				BigDecimal tempRes = bigDecimal.divide(bigDecimalOpen, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
				tempRes = tempRes.setScale(2, BigDecimal.ROUND_HALF_UP);
				bigDecimalList.add(tempRes.toString());
			}
			data.add(bigDecimalList);
		}

		// 设置文件名
		String fileName = exportPath + "/"+ DateUtil.current()+".xlsx";
		EasyExcel.write(fileName)
				.head(getExcelTitle(marketQuotationListTemp))
				.sheet("111")
				.doWrite(data);
		return true;
	}

	private List<List<String>> getExcelTitle(List<MarketQuotation> marketQuotationList) {
		List<List<String>> resultList = new ArrayList<>();
		List<String> list = new ArrayList<>();
		list.add("标的");
		resultList.add(list);
		for (int i =0; i < marketQuotationList.size(); i++) {
			List<String> list4 = new ArrayList<>();
			list4.add(DateUtil.format(marketQuotationList.get(i).getCloseTime(), DatePattern.CHINESE_DATE_PATTERN));
			String closeDate = DateUtil.format(marketQuotationList.get(i).getCloseTime().plus(1, ChronoUnit.SECONDS), "HH");
			list4.add(closeDate);
			resultList.add(list4);
		}

		return resultList;
	}
}
