package com.wizard.controller;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.wizard.common.base.ResultInfo;
import com.wizard.common.constants.ApiConstant;
import com.wizard.common.utils.ResultInfoUtil;
import com.wizard.model.po.SymbolInfoPo;
import com.wizard.service.SymbolInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 岳耀栎
 * @date 2024-09-19
 * @desc
 */
@Slf4j
@RestController
@RequestMapping("/symbolInfo")
public class SymbolInfoController {

	@Resource
	SymbolInfoService symbolInfoService;

	@RequestMapping(value = "/getSymbolInfo",method = RequestMethod.GET)
	public SymbolInfoPo getSymbolInfo(String symbol) {
		return symbolInfoService.getSymbolInfo(symbol);
	}

	@RequestMapping(value = "/saveBatchList",method = RequestMethod.POST)
	public ResultInfo<Boolean> saveBatchList(@RequestBody List<SymbolInfoPo> symbolList) {
		ResultInfo<Boolean> resultInfo = ResultInfoUtil.buildError(ApiConstant.ERROR_MESSAGE);
		try {
			if(symbolInfoService.saveBatchList(IdWorker.getId(),symbolList)){
				resultInfo = ResultInfoUtil.buildSuccess(null,Boolean.TRUE);
			}
		} catch (Exception e) {
			resultInfo = ResultInfoUtil.buildErrorMsg(e.getMessage());
		}
		return resultInfo;
	}

	/**
	 * 同步所有的标的
	 * @return
	 */
	@RequestMapping(value = "/syncSymbolInfo",method = RequestMethod.GET)
	public ResultInfo<Boolean> syncSymbolInfo() {
		ResultInfo<Boolean> resultInfo = ResultInfoUtil.buildError(ApiConstant.ERROR_MESSAGE);
		try {
			if(symbolInfoService.syncSymbolInfo(IdWorker.getId())){
				resultInfo = ResultInfoUtil.buildSuccess(null,Boolean.TRUE);
			}
		} catch (Exception e) {
			resultInfo = ResultInfoUtil.buildErrorMsg(e.getMessage());
		}
		return resultInfo;
	}
}
