package com.wizard.model.po;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author 岳耀栎
 * @date 2024-09-19
 * @desc 标的信息
 */
@Data
@Builder
@TableName(value = "symbol_info")
public class SymbolInfoPo implements Serializable {

	@TableId(value = "id",type = IdType.NONE)
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	/**
	 * 标的简称
	 */
	private String symbol;

	/**
	 * 标的全称
	 */
	private String symbolName;

	/**
	 * 板块标签
	 */
	private String tags;

	/**
	 * 操盘机构
	 */
	private String institution;

	private String content;

	private Date createTime;

	private Integer delFlag;
}
