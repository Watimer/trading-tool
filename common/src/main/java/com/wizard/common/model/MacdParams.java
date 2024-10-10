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
public class MacdParams extends IndicatorParams implements Serializable {

	private int fastCycle = 12;

	private int slowCycle = 26;

	private int difCycle = 9;
}
