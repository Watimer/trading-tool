package com.wizard.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author wizard
 * @date 2024-09-18
 * @desc
 */
@Data
public class TradingViewStrongSymbolVO implements Serializable {

	/**
	 * 标的
	 */
	private String symbol;

	/**
	 * 推荐系数
	 */
	private Integer level;

	/**
	 * 描述
	 */
	private String direction;

	/**
	 * 流动性分数 = 交易量/市值
	 */
	private BigDecimal effectiveLiquidity;

	/**
	 * 波动率
	 */
	private BigDecimal volatility;

	/**
	 * 涨幅
	 */
	private BigDecimal increaseInPrice;

	/**
	 * 三日内是否首次入选
	 */
	private String firstFlag;

	private String tags;
}
