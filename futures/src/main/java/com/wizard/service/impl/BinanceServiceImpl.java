package com.wizard.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import com.wizard.model.vo.LongShortPositionRatioVO;
import com.wizard.model.vo.TakerLongShortRatioVO;
import com.wizard.service.BinanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@Service
public class BinanceServiceImpl implements BinanceService {

    @Value("${BINANCE.API_KEY}")
    private String API_KEY;

    @Value("${BINANCE.SECRET_KEY}")
    private String SECRET_KEY;

    @Value("${PROXY.URL}")
    private String PROXY_URL;


    /**
     * 大户持仓量多空比
     *
     * @param logId  日志ID
     * @param symbol 标的
     * @param period 时间周期
     * @descript 大户的多头和空头总持仓量占比，大户指保证金余额排名前20%的用户。
     * 多仓持仓量比例 = 大户多仓持仓量 / 大户总持仓量
     * 空仓持仓量比例 = 大户空仓持仓量 / 大户总持仓量
     * 多空持仓量比值 = 多仓持仓量比例 / 空仓持仓量比例
     */
    @Override
    public List<LongShortPositionRatioVO> topLongShortPositionRatio(Long logId, String symbol, String period) {

        String URL = PROXY_URL + "/futures/data/topLongShortPositionRatio?symbol=" + symbol + "&period=" + period;

        String result = HttpRequest.get(URL)
//                .setHttpProxy("127.0.0.1", 7897)
                .execute()
                .body();
        log.info(result);
        if (StrUtil.isBlank(result)) {
            return null;
        }
        List<LongShortPositionRatioVO> longShortPositionRatioVOList = JSONObject.parseArray(result, LongShortPositionRatioVO.class);
        return longShortPositionRatioVOList;
    }

    /**
     * 合约主动买卖量
     *
     * @param logId  日志ID
     * @param symbol 标的
     * @param period 时间周期
     * @return 返回 合约主动买卖量
     */
    @Override
    public List<TakerLongShortRatioVO> takerLongShortRatio(Long logId, String symbol, String period) {

        UMFuturesClientImpl client = new UMFuturesClientImpl(API_KEY, SECRET_KEY, PROXY_URL);
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", symbol);
        parameters.put("period", symbol);
        String result = client.market().longShortRatio(parameters);
        List<TakerLongShortRatioVO> takerLongShortRatioVOList = JSONObject.parseArray(result, TakerLongShortRatioVO.class);
        return takerLongShortRatioVOList;
    }
}
