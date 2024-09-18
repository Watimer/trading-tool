package com.wizard.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 大户持仓量多空比 实体
 */

@Data
public class LongShortPositionRatioVO implements Serializable {

    private String symbol;

    /**
     * 大户多空持仓量比值
     */
    private BigDecimal longShortRatio;

    /**
     * 大户多仓持仓量比例
     */
    private BigDecimal longAccount;

    /**
     * 大户空仓持仓量比例
     */
    private BigDecimal shortAccount;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 时间周期
     */
    private String period;
}
