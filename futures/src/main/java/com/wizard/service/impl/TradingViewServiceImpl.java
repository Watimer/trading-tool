package com.wizard.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wizard.model.po.SymbolStrongPo;
import com.wizard.model.vo.TradingViewStrongSymbolVO;
import com.wizard.push.serivce.PushService;
import com.wizard.service.SymbolStrongService;
import com.wizard.service.TradingViewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wizard
 * @date 2024-06-03
 * @desc
 */
@Slf4j
@Service
public class TradingViewServiceImpl implements TradingViewService {

	@Resource
	PushService pushService;

	@Resource
	SymbolStrongService symbolStrongService;

	private String cok = "cookiePrivacyPreferenceBannerProduction=accepted; cookiesSettings={\"analytics\":true,\"advertising\":true}; _ga=GA1.1.134239995.1655090829; __gads=ID=80abeb8b8917d197:T=1689869744:RT=1700469334:S=ALNI_MZMk-5I6TuILfSxJ85vajeqbCHFSA; __gpi=UID=00000c22b48781fa:T=1689869744:RT=1700469334:S=ALNI_MarxMpLw9j3Zt8d0v9XC7aZvDC7vg; theme=dark; _ga_R53B6WMR8T=GS1.1.1712556222.3.1.1712556326.0.0.0; _ga_53M0R0ZT9V=GS1.1.1712556222.3.1.1712556326.0.0.0; device_t=dUtMRjox.cJiuwKDgZvh22N8e7pifIjq0lWu8tTt-UfS9ZLGN2Vs; sessionid=koir183bd5v15os9ck9kf093wmr4swhk; sessionid_sign=v2:aOLdqzwfMTzQ5mEL++pcvSKOjktc6tiyxrXoMtgK7ks=; tv_ecuid=da66e945-3230-45db-a827-b7ce52e5f5c2; _sp_ses.cf1a=*; _sp_id.cf1a=bce98ec1-dfd9-4862-9e76-68857698e196.1703693658.121.1717384986.1716990319.fa1c0945-c1a9-46ba-964e-ce3739b3a10f; _ga_YVVRYGL0E0=GS1.1.1717384986.304.0.1717384986.60.0.0";

	@Override
	public void scan(Long logId) {
		// 请求地址
		String url = "https://scanner.tradingview.com/coin/scan";

		// 请求参数
		String params = "{\"columns\":[\"base_currency\",\"base_currency_desc\",\"base_currency_logoid\",\"update_mode\",\"type\",\"typespecs\",\"exchange\",\"crypto_total_rank\",\"close\",\"pricescale\",\"minmov\",\"fractional\",\"minmove2\",\"currency\",\"24h_close_change|5\",\"24h_vol_to_market_cap\",\"24h_vol_cmc\",\"fundamental_currency_code\",\"market_cap_calc\",\"circulating_supply\",\"crypto_common_categories.tr\",\"Volatility.D\",\"crypto_blockchain_ecosystems.tr\"],\"filter\":[{\"left\":\"24h_vol_to_market_cap\",\"operation\":\"in_range\",\"right\":[0.01,2]},{\"left\":\"24h_vol_cmc\",\"operation\":\"egreater\",\"right\":100000000},{\"left\":\"crypto_common_categories\",\"operation\":\"has\",\"right\":[\"loyalty-rewards\",\"tourism\",\"identity\",\"enterprise-solutions\",\"smart-contract-platforms\",\"jobs\",\"web3\",\"centralized-exchange\",\"seigniorage\",\"developments-tools\",\"internet-of-things\",\"distributed-computing-storage\",\"sports\",\"logistics\",\"metaverse\",\"insurance\",\"marketplace\",\"move-to-earn\",\"defi\",\"hospitality\",\"energy\",\"payments\",\"real-estate\",\"decentralized-exchange\",\"algorithmic-stablecoins\",\"cybersecurity\",\"marketing\",\"layer-1\",\"wrapped-tokens\",\"memecoins\",\"derivatives\",\"asset-management\",\"scaling\",\"data-management-ai\",\"sec-security-token\",\"collectibles-nfts\",\"fan-tokens\",\"lending-borrowing\",\"interoperability\",\"privacy\",\"cryptocurrencies\",\"social-media-content\",\"fundraising\",\"transport\",\"e-commerce\",\"asset-backed-tokens\",\"analytics\",\"oracles\",\"prediction-markets\",\"dao\",\"education\",\"health\",\"gaming\"]},{\"left\":\"Volatility.D\",\"operation\":\"greater\",\"right\":5},{\"left\":\"BB.basis|240\",\"operation\":\"less\",\"right\":\"close|240\"}],\"ignore_unknown_fields\":false,\"options\":{\"lang\":\"zh\"},\"range\":[0,100],\"sort\":{\"sortBy\":\"24h_close_change|5\",\"sortOrder\":\"desc\"},\"symbols\":{},\"markets\":[\"coin\"]}";

		String result = HttpRequest.post(url)
				.contentType("application/json")
				.charset(CharsetUtil.UTF_8)
				.body(params)
				.cookie(cok)
				.execute()
				.body();

		log.info("日志ID:{},结果:{}",logId,result);
		List<TradingViewStrongSymbolVO> stringList = new ArrayList<>();
		// 解析返回结果
		JSONObject jsonResult = JSONObject.parseObject(result);
		List<JSONObject> dataList = jsonResult.getJSONArray("data").toJavaList(JSONObject.class);
		for (JSONObject jsonObject : dataList) {
		    JSONArray jsonArray = jsonObject.getJSONArray("d");
			String symbol = jsonArray.getString(0);
			TradingViewStrongSymbolVO tradingViewStrongSymbolVO = new TradingViewStrongSymbolVO();
			tradingViewStrongSymbolVO.setSymbol(symbol);
			tradingViewStrongSymbolVO.setLevel(jsonArray.getInteger(7));
			// 24h涨跌
			tradingViewStrongSymbolVO.setIncreaseInPrice(jsonArray.getBigDecimal(14));
			tradingViewStrongSymbolVO.setEffectiveLiquidity(jsonArray.getBigDecimal(15));
			// 获取tags
			JSONArray tagArray = jsonArray.getJSONArray(20);
			StringBuffer tagsBuffer = new StringBuffer();
			tagArray.stream().forEach(item -> tagsBuffer.append(item).append(","));
			tradingViewStrongSymbolVO.setTags(tagsBuffer.toString());
			tradingViewStrongSymbolVO.setVolatility(jsonArray.getBigDecimal(21));
			tradingViewStrongSymbolVO.setFirstFlag("否");
			stringList.add(tradingViewStrongSymbolVO);
		}
		List<SymbolStrongPo> saveSymbolStrongPoList = new ArrayList<>();
		// 判断是否首次入选
		Date beginTime = DateUtil.offsetDay(new Date(),-3);
		stringList.stream().forEach(tradingViewStrongSymbolVO -> {
		   List<SymbolStrongPo> symbolStrongPoList = symbolStrongService.listSymbolStrongInPeriod(logId, tradingViewStrongSymbolVO.getSymbol(), beginTime,new Date());
		   if(CollUtil.isEmpty(symbolStrongPoList)){
			   tradingViewStrongSymbolVO.setFirstFlag("是");
		   }
		   SymbolStrongPo symbolStrongPo = SymbolStrongPo.builder()
					.level("4H")
					.symbolName(tradingViewStrongSymbolVO.getSymbol())
					.increaseRate(tradingViewStrongSymbolVO.getIncreaseInPrice())
					.filteringPolicy("强势标的筛选器")
				   .symbolTags(tradingViewStrongSymbolVO.getTags())
					.volumeMarket(tradingViewStrongSymbolVO.getEffectiveLiquidity())
					.fluctuate(tradingViewStrongSymbolVO.getVolatility()).build();
		   saveSymbolStrongPoList.add(symbolStrongPo);
		});
		// 保存记录
		symbolStrongService.saveBatchList(logId, saveSymbolStrongPoList);

		if(!stringList.isEmpty()){
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("警报类型:强势标的").append("\n").append("\n");
			// 推送飞书
			for (TradingViewStrongSymbolVO tradingViewStrongSymbolVO : stringList){
				String symbol = tradingViewStrongSymbolVO.getSymbol();
				BigDecimal effectiveLiquidity = tradingViewStrongSymbolVO.getEffectiveLiquidity();
				effectiveLiquidity = effectiveLiquidity.setScale(2, BigDecimal.ROUND_HALF_UP);
				stringBuffer.append("首次入选:").append(tradingViewStrongSymbolVO.getFirstFlag()).append(", ")
						.append("流动性:").append(effectiveLiquidity).append(", ")
						.append("标的:").append(symbol).append("\n");
			}
			stringBuffer.append("\n");
			stringBuffer.append("时间:").append(DateUtil.now());
			pushService.pushManySymbol(logId,stringBuffer.toString());
		}
	}

	public static void main(String[] args) {
		String symbol = "BIGTIME";
		symbol = String.format("%-9s", symbol);

		System.out.println("【"+symbol+"】");
	}
}
