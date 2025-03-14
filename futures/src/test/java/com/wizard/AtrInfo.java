package com.wizard;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 岳耀栎
 * @date 2025-03-02
 * @desc
 */
@Data
public class AtrInfo {

	private Long closeTime;

	private Long timestamp;

	private String symbol;

	private double open;

	private BigDecimal bigDecimalOpen;

	private double low;

	private double high;

	private double close;
}
