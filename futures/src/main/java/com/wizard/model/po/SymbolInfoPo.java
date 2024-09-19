package com.wizard.model.po;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
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
	@TableField(insertStrategy = FieldStrategy.IGNORED)
	private String tags;

	/**
	 * 操盘机构
	 */
	@TableField(insertStrategy = FieldStrategy.IGNORED)
	private String institution;

	@TableField(insertStrategy = FieldStrategy.IGNORED)
	private String content;

	private Integer rank;

	/**
	 * 最大供应量
	 */
	@TableField(insertStrategy = FieldStrategy.IGNORED)
	private Long maxSupply;

	/**
	 * 目前总量
	 */
	@TableField(insertStrategy = FieldStrategy.IGNORED)
	private BigDecimal totalSupply;

	/**
	 * 当前流通量
	 */
	@TableField(insertStrategy = FieldStrategy.IGNORED)
	private BigDecimal circulatingSupply;

	private Date createTime;

	private Integer delFlag;
}
