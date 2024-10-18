package com.wizard.model.dto;

import jnr.ffi.annotations.In;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wizard
 * @date 2024-10-18
 * @desc Bi123信号参数实体
 */
@Data
@Builder
public class SignalParamDTO implements Serializable {

	/**
	 * 当前页码
	 */
	private Integer current = 1;

	/**
	 * 每页数量
	 */
	private Integer size = 20;

	/**
	 * 信号类型
	 * 	0-趋势追踪
	 * 	2-MA5短线
	 * 	3-RSI背离
	 * 	4-多空头排列
	 * 	6-斐波那契回撤
	 */
	private Integer type;

	private String isUp;

	private String wellChosen;

	private String duration;

	private String contract;

	/**
	 * 交易对
	 */
	private String sortName;
}
