package com.wizard.model.vo;

import com.wizard.service.FutureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 岳耀栎
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
	public String getContinuousKLines(String symbol){

		return futureService.getContinuousKLines(symbol);
	}
}
