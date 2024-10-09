package com.wizard.controller;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.wizard.common.constants.ApiConstant;
import com.wizard.model.po.SymbolCategoryPo;
import com.wizard.model.po.SymbolInfoPo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author 巫师
 * @date 2024-10-10
 * @desc
 */
@Slf4j
@SpringBootTest
@WebAppConfiguration
@RunWith(SpringRunner.class)
public class SymbolCategoryControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	// 启动单元测试禁用定时任务
	@TestConfiguration
	static class TestConfig {
		@Bean
		public TaskScheduler taskScheduler() {
			// 使用默认的无任务调度器
			return new ConcurrentTaskScheduler();
		}
	}

	//@Before
	public void setUp() {
		log.info("进入前置方法");
		// 初始化MockMvc,并设置编码格式、结果校验等公共信息
		mockMvc = MockMvcBuilders
				.webAppContextSetup(wac)
				.alwaysExpect(content().contentType(MediaType.APPLICATION_JSON))
				.alwaysExpect(status().isOk())
				.build();
	}

	@Test
	public void testSaveBatchList() throws Exception {
		List<SymbolCategoryPo> symbolCategoryPoList = new ArrayList<>();
		SymbolCategoryPo symbolCategoryPo = SymbolCategoryPo.builder()
				.delFlag(0)
				.id(IdWorker.getId())
				.name("SOL系列代币")
				.code("SOL")
				.createTime(DateTime.now())
				.content("SOL系列代币").build();
		symbolCategoryPoList.add(symbolCategoryPo);
		String result = mockMvc.perform(MockMvcRequestBuilders.post("/symbolCategory/saveBatchList")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JSONObject.toJSONString(symbolCategoryPoList)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(ApiConstant.SUCCESS_CODE))
				.andExpect(jsonPath("$.msg").value(ApiConstant.SUCCESS_MESSAGE))
				.andReturn()
				.getResponse()
				.getContentAsString();
		log.debug("{}",result);
	}
}
