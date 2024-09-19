package com.wizard.common.base;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 岳耀栎
 * @date 2024-09-19
 * @desc
 */
@Data
public class ResultInfo<T> implements Serializable {

	private int code;

	private String msg;

	private T data;
}
