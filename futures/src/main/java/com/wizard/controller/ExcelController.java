package com.wizard.controller;

import com.wizard.common.base.ResultInfo;
import com.wizard.common.constants.ApiConstant;
import com.wizard.common.utils.ResultInfoUtil;
import com.wizard.service.ExcelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 岳耀栎
 * @date 2024-11-11
 * @desc
 */
@Slf4j
@RestController
@RequestMapping("/excel")
public class ExcelController {

	@Resource
	ExcelService excelService;

	@GetMapping(value = "/exportExcel")
	public ResultInfo<Boolean> exportExcel(String exportPath) {
		ResultInfo<Boolean> resultInfo = ResultInfoUtil.buildError(ApiConstant.ERROR_MESSAGE);
		try {
			if(excelService.excelService(exportPath)){
				resultInfo = ResultInfoUtil.buildSuccess(null,Boolean.TRUE);
			}
		} catch (Exception e) {
			resultInfo = ResultInfoUtil.buildErrorMsg(e.getMessage());
		}
		return resultInfo;
	}


}
