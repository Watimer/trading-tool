package com.wizard.common.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 岳耀栎
 * @date 2024-10-10
 * @desc 指标参数
 */
@Data
public class IndicatorParams implements Serializable {

	private int indicatorSetScale;

	private int capacity;
}
