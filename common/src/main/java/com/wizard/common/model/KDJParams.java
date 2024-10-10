package com.wizard.common.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 岳耀栎
 * @date 2024-10-10
 * @desc
 */
@Data
@Builder
public class KDJParams extends IndicatorParams implements Serializable {

	private int kCycle;

	private int dCycle;
}
