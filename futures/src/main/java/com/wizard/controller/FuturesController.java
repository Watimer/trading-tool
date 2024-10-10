package com.wizard.controller;

import com.wizard.common.model.MarketQuotation;
import com.wizard.model.dto.SymbolLineDTO;
import com.wizard.service.FutureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wizard
 * @date 2024-09-12
 * @desc
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class FuturesController {


	@Resource
	FutureService futureService;


	@RequestMapping(value = "/getContinuousKLines",method = RequestMethod.GET)
	public List<MarketQuotation> getContinuousKLines(SymbolLineDTO symbolLineDTO){

		return futureService.getContinuousKLines(symbolLineDTO);
	}
}
