package com.wizard.common.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 岳耀栎
 * @date 2024-10-10
 * @desc BIAS 乖离率
 */
@Data
@Builder
public class BiasParams extends IndicatorParams implements Serializable {

	private int capacity = 6;
}
