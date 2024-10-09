package com.wizard.component;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.wizard.service.FutureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author 巫师
 * @date 2024-05-07
 * @desc
 */
@Slf4j
@Component
public class StartComponent {

	@Resource
	FutureService futureService;

	@Resource
	GlobalListComponent globalListComponent;

	/**
	 * 项目启动时执行
	 * 用于将可交易标的放入内存中,方便后续直接调用或者计算是否有增减标的内容
	 */
//	@PostConstruct
	public void initGlobalList(){
		Long logId = IdWorker.getId();
		log.info("日志ID:{},启动时执行1",logId);
		List<String> symbolList= futureService.getExchangeInfo(logId);
		log.info("日志ID:{},标的列表:\n{}",logId, JSONObject.toJSONString(symbolList));
		globalListComponent.addToGlobalList(logId,symbolList);
		log.info("日志ID:{},数据初始化完成..",logId);
	}
}
