package com.wizard.common.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 岳耀栎
 * @date 2024-10-10
 * @desc TD 参数
 */
@Data
@Builder
public class MaParams extends IndicatorParams implements Serializable {

	private int moveSize = 4;

	private int capacity = 9;
}
