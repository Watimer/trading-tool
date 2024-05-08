package com.wizard.common.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author 岳耀栎
 * @date 2024-05-08
 * @desc 交易所枚举对象
 */
@Getter
@NoArgsConstructor
public enum ExchangeEnum {

	EXCHANGE_BINANCE(10001,"币安:","BINANCE"),
	EXCHANGE_OKEX(10002,"欧易:","OKEX"),
	EXCHANGE_OTHER(99999,"未知:","OTHER");

	/** 编码 */
	private Integer code;

	/** 中文名称 */
	private String name;

	/**
	 * 英文名称
	 */
	private String enName;

	ExchangeEnum(Integer code,String name,String enName){
		this.code = code;
		this.name = name;
		this.enName = enName;
	}
}
