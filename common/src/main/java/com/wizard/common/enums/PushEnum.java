package com.wizard.common.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author 巫师
 * @date 2024-05-08
 * @desc 推送类型枚举对象
 */
@Getter
@NoArgsConstructor
public enum PushEnum {
	/**
	 * 枚举类型
	 */
	FUTURES_OPEN_INTEREST_LONG(98,"合约持仓量:","LONG","增加"),
	FUTURES_OPEN_INTEREST_SHORT(98,"合约持仓量:","SHORT","减少"),
	FUTURES_SYMBOL_ADD(98,"标的增减:","ADD","增加"),
	FUTURES_SYMBOL_DELETE(98,"标的增减:","DELETE","减少"),
	FUTURES_SYMBOL_RATE(98,"资金费率:","LONG","负费率"),
	DOWNLOAD_ERROR(99,"出现错误","ERROR","失败");

	/** 状态编码 */
	private Integer code;

	/** 状态描述 */
	private String description;

	/**
	 * 方向
	 */
	private String direction;

	private String detail;

	PushEnum(Integer code, String description,String direction,String detail){
		this.description = description;
		this.code = code;
		this.direction = direction;
		this.detail = detail;
	}

	/**根据 code 查询 description 的静态方法*/
	public static String getDescriptionByCode(Integer code) {
		for (PushEnum status : PushEnum.values()) {
			if (getCode(status).equals(code)) {

				return status.description;
			}
		}
		// 如果没有匹配的 code，可以抛出异常或返回一个默认值，这里返回空字符串
		return "未知";
	}

	/**
	 * 获取code
	 * */
	public static Integer getCode(PushEnum pushEnum) {
		return pushEnum.code;
	}

	/**
	 * 获取方向
	 * @param pushEnum
	 * @return
	 */
	public static String getDirection(PushEnum pushEnum){
		return pushEnum.direction;
	}

	public static String getDetail(PushEnum pushEnum){
		return pushEnum.detail;
	}

	public static PushEnum setDescription(String description,String detail){
		PushEnum pushEnum = PushEnum.FUTURES_SYMBOL_RATE;
		pushEnum.description = description;
		pushEnum.detail = detail;
		return pushEnum;
	}
}
