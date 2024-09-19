package com.wizard.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 岳耀栎
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
}
