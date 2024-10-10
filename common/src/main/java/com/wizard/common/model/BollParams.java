package com.wizard.common.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 岳耀栎
 * @date 2024-10-10
 * @desc 布林带自定义参数
 */
@Data
@Builder
public class BollParams extends IndicatorParams implements Serializable {

	private int d = 2;

	private int capacity = 20;
}
