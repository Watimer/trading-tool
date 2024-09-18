package com.wizard.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 合约主动买卖量
 */
@Data
public class TakerLongShortRatioVO implements Serializable {

    /**
     * 标的
     */
    private String symbol;

    /**
     * 买入/卖出比
     */
    private BigDecimal buySellRatio;

    /**
     * 主动买入量
     */
    private BigDecimal buyVol;

    /**
     * 主动卖出量
     */
    private BigDecimal sellVol;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 时间级别
     */
    private String period;
}
