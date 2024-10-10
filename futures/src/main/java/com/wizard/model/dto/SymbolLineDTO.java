package com.wizard.model.dto;

import com.wizard.common.enums.ContractTypeEnum;
import com.wizard.common.enums.IntervalEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wizard
 * @date 2024-10-10
 * @desc
 */
@Data
public class SymbolLineDTO implements Serializable {

	private String symbol;

	private IntervalEnum interval;

	private ContractTypeEnum contractType;

	private Integer limit;
}
