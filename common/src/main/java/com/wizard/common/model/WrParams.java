package com.wizard.common.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 岳耀栎
 * @date 2024-10-10
 * @desc WR 威廉指标 参数
 */
@Data
@Builder
public class WrParams extends IndicatorParams implements Serializable {

	private int capacity = 6;
}
