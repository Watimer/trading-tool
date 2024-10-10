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
public class DmiParams extends IndicatorParams implements Serializable {

	private int diPeriod = 14;

	private int adxPeriod = 6;
}
