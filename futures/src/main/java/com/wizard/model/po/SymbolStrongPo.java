package com.wizard.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 岳耀栎
 * @date 2024-10-16
 * @desc
 */
@Data
@Builder
@TableName(value = "symbol_strong")
public class SymbolStrongPo implements Serializable {

	@TableId(value = "id",type = IdType.NONE)
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	@JsonSerialize(using = ToStringSerializer.class)
	private Long symbolId;

	private String symbolName;

	private String level;

	private String filteringPolicy;

	private String symbolTags;

	/**
	 * 成交额/市值
	 */
	private BigDecimal volumeMarket;

	/**
	 * 24h涨幅
	 */
	private BigDecimal increaseRate;

	/**
	 * 波动
	 */
	private BigDecimal fluctuate;

	private Date createTime;

	private Integer delFlag;
}
