package com.wizard.service;

import com.wizard.model.vo.LongShortPositionRatioVO;
import com.wizard.model.vo.TakerLongShortRatioVO;

import java.util.List;

public interface BinanceService {

    /**
     *  大户持仓量多空比
     * @param logId     日志ID
     * @param symbol    标的
     * @param period    时间周期
     * @descript 大户的多头和空头总持仓量占比，大户指保证金余额排名前20%的用户。
     * 多仓持仓量比例 = 大户多仓持仓量 / 大户总持仓量
     * 空仓持仓量比例 = 大户空仓持仓量 / 大户总持仓量
     * 多空持仓量比值 = 多仓持仓量比例 / 空仓持仓量比例
     */
    List<LongShortPositionRatioVO> topLongShortPositionRatio(Long logId, String symbol, String period);

    /**
     * 合约主动买卖量
     * @param logId     日志ID
     * @param symbol    标的
     * @param period    时间周期
     * @return
     */
    List<TakerLongShortRatioVO> takerLongShortRatio(Long logId, String symbol, String period);
}
