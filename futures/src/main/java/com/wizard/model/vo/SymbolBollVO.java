package com.wizard.model.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 岳耀栎
 * @date 2024-10-22
 * @desc
 */
@Data
@Builder
public class SymbolBollVO implements Serializable {

	/**
	 * 标的
	 */
	private String symbol;

	/**
	 * 时间级别
	 */
	private String interval;

	private LocalDateTime closeTime;

	private Double close;

	private Double bollUpper;

	private Double u;

	private Double m;

	private Boolean mAmplitude;

	private Double d;

	private Boolean mFlag;

	private Boolean uFlag;

	private Boolean dFlag;
}
