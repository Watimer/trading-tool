package com.wizard.common.model;

import lombok.Data;

/**
 * @author 巫师
 * @date 2025-01-06
 * @desc 超级趋势类
 */
@Data
public class Supertrend {
	double value;
	boolean isUptrend;

	public Supertrend(double value, boolean isUptrend) {
		this.value = value;
		this.isUptrend = isUptrend;
	}
}
