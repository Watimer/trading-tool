package com.wizard.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wizard
 * @date 2024-10-10
 * @desc
 */
@Data
@Builder
@TableName(value = "symbol_category_link")
public class SymbolCategoryLinkPo implements Serializable {

	@TableId(value = "id",type = IdType.NONE)
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	@JsonSerialize(using = ToStringSerializer.class)
	private Long symbolId;

	@JsonSerialize(using = ToStringSerializer.class)
	private Long categoryId;

	private String createBy;

	private Date createTime;

	private Integer delFlag;
}
