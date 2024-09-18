package com.wizard.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author 岳耀栎
 * @date 2024-09-18
 * @desc 交易量统计实体
 */
@Data
@TableName(value = "volume_info")
public class VolumeInfoPo implements Serializable {

	@TableId(value = "id",type = IdType.NONE)
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	private Long groupFlag;

	private String symbol;

	private BigDecimal currentPrice;

	private String exchangeName;

	private BigDecimal exchangeProportion;

	private BigDecimal exchangeVolume;

	private String level;

	private Integer effectiveLiquidity;

	private Integer marketReputation;

	private LocalDateTime createTime;

	private Integer delFlag;
}
