package com.wizard.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wizard
 * @date 2024-10-10
 * @desc
 */
@Data
@Builder
public class SymbolLineDTO implements Serializable {

	private String symbol;

	private String interval;

	private String contractType;

	private Integer limit;
}
