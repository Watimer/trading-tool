package com.wizard.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.Task;

/**
 * @author 巫师
 * @date 2024-05-07
 * @desc
 */
@Slf4j
@Configuration
public class ScheduleConfig implements SchedulingConfigurer {

	/**
	 * Callback allowing a {@link TaskScheduler
	 * TaskScheduler} and specific {@link Task Task}
	 * instances to be registered against the given the {@link ScheduledTaskRegistrar}.
	 *
	 * @param scheduledTaskRegistrar the registrar to be configured.
	 */
	@Override
	public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.setPoolSize(10);
		taskScheduler.setThreadNamePrefix("TRADING-");
		// 设置超时时间为5分钟
		taskScheduler.setAwaitTerminationSeconds(300);
		taskScheduler.setErrorHandler(t -> {
			// 执行异常策略，例如记录日志或发送通知
			log.error("线程:{},执行异常:{}",t.getCause(),t.getMessage());
			log.error("{}",t);
		});
		taskScheduler.initialize();

		scheduledTaskRegistrar.setTaskScheduler(taskScheduler);
	}
}
