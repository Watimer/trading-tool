package com.wizard.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 巫师
 * @date 2024-04-25
 * @desc 全局缓存变量,用于存储所有可交易标的
 */
@Slf4j
@Component
public class GlobalListComponent {

	/**
	 * 待分析数据列表
	 */
	private List<String> globalList = new ArrayList<>();

	private static GlobalListComponent instance;

	private GlobalListComponent() {
		// private constructor to prevent instantiation
	}

	public static synchronized GlobalListComponent getInstance() {
		if (instance == null) {
			instance = new GlobalListComponent();
		}
		return instance;
	}

	/**
	 * 获取默认待分析列表【空List】
	 * @return List<ResourcePo>
	 */
	public List<String> getGlobalList() {
		return globalList;
	}

	/**
	 * 添加待分析数据
	 * @param logId	日志ID
	 * @param item	数据项
	 */
	public void addToGlobalList(Long logId,String item) {
		globalList.add(item);
		log.info("日志ID:{},添加标的:{}",logId,item);
	}

	public void addToGlobalList(Long logId,List<String> item) {
		globalList.addAll(item);
		log.info("日志ID:{},添加标的:{}",logId,item);
	}

	public void removeAll(Long logId){
		globalList.clear();
		log.info("日志ID:{},删除所有标的:{}",logId);
	}

	/**
	 * 移除待分析数据
	 * @param logId	日志ID
	 * @param item	数据项
	 */
	public void removeFromGlobalList(Long logId,String item) {
		globalList.remove(item);
		log.info("日志ID:{},移除标的:{}",logId,item);
	}
}
