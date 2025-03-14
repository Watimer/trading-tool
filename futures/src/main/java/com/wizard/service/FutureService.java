package com.wizard.service;

import com.wizard.common.model.MarketQuotation;
import com.wizard.model.dto.SymbolLineDTO;

import java.util.List;

/**
 * @author 巫师
 * @date 2024-05-07
 * @desc
 */
public interface FutureService {

	void openInterestStatistics(Long logId);

	/**
	 * 检测是否存在新增标的
	 * @param logId 日志ID
	 */
	void checkNewSymbol(Long logId);

	/**
	 * 获取所有可交易标的列表
	 * @param logId 日志ID
	 * @return
	 */
	List<String> getExchangeInfo(Long logId);

	/**
	 * 检测资金费率
	 * @param logId
	 */
	void getRate(Long logId);

	/**
	 * 获取连续合约数据
	 * @param symbolLineDTO 标的
	 * @return
	 */
	List<MarketQuotation> getContinuousKLines(SymbolLineDTO symbolLineDTO);

	/**
	 * 指标信号通知
	 * @param logId		日志ID
	 */
	void indicatorSignal(Long logId);

	/**
	 * 成交量监控
	 * @param logId 	日志ID
	 */
	void monitorVolume(Long logId);

	/**
	 * 波动预警
	 * @param logId		日志ID
	 */
	void fluctuate(Long logId);

	/**
	 * 超级趋势计算
	 * @param id
	 */
	void supertrend(long id);
}
