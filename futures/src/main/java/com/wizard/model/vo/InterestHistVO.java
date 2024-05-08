package com.wizard.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author 巫师
 * @date 2024-05-07
 * @desc
 */
@Data
public class InterestHistVO implements Serializable {


	 /**标的名称*/
	private String symbol;

	/**持仓总数量*/
	private BigDecimal sumOpenInterest;

	/**持仓总价值*/
	private BigDecimal sumOpenInterestValue;

	/**时间戳*/
	private Long timestamp;
}
