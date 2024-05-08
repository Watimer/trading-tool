package com.wizard.common.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author 巫师
 * @date 2024-05-08
 * @desc 规则枚举
 */
@Getter
@NoArgsConstructor
public enum RuleEnum {


	/**
	 * 枚举类型
	 */
	OPEN_INTEREST_GT_OME_FIVE(15,"合约持仓量:","LONG","持仓量增加1.5倍以上"),
	OPEN_INTEREST_GT_TWO_ZERO(98,"合约持仓量:","SHORT","持仓量增加2倍以上"),
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

	RuleEnum(Integer code, String description,String direction,String detail){
		this.description = description;
		this.code = code;
		this.direction = direction;
		this.detail = detail;
	}

	/**根据 code 查询 description 的静态方法*/
	public static String getDescriptionByCode(Integer code) {
		for (RuleEnum status : RuleEnum.values()) {
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
	public static Integer getCode(RuleEnum pushEnum) {
		return pushEnum.code;
	}

	/**
	 * 获取方向
	 * @param pushEnum
	 * @return
	 */
	public static String getDirection(RuleEnum pushEnum){
		return pushEnum.direction;
	}

	public static String getDetail(RuleEnum pushEnum){
		return pushEnum.detail;
	}
}
