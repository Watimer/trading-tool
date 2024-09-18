package com.wizard.task;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 岳耀栎
 * @date 2024-09-18
 * @desc
 */
@Slf4j
public class CoinMarketCapTask {

	public static void main(String[] args) {
		String API_URL = "https://api.coinmarketcap.com/data-api/v3/cryptocurrency/market-pairs/latest?slug=zetachain&start=1&limit=10&category=spot&centerType=all&sort=cmc_rank_advanced&direction=desc&spotUntracked=true";
		String result = HttpUtil.get(API_URL);

		log.info("{}",result);
	}
}
