package com.wizard.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author 巫师
 * @date 2024-05-09
 * @desc 资金费率实体
 */
@Data
public class SymbolFundingRateVO implements Serializable {

	/**
	 * 标的
	 */
	private String symbol;

	/**
	 * 资金费时间
	 */
	private Long fundingTime;

	/**
	 * 资金费率
	 */
	private BigDecimal fundingRate;

	/**
	 * 当前标记价格
	 */
	private BigDecimal markPrice;
}
