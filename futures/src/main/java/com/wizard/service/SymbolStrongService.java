package com.wizard.service;

import com.wizard.model.po.SymbolStrongPo;

import java.util.Date;
import java.util.List;

/**
 * @author 岳耀栎
 * @date 2024-10-16
 * @desc
 */
public interface SymbolStrongService {

	/**
	 * 批量保存强势标的记录
	 * @param logId					日志ID
	 * @param symbolStrongPoList	强势标的列表
	 * @return 保存结果
	 */
	boolean saveBatchList(Long logId, List<SymbolStrongPo> symbolStrongPoList);

	/**
	 * 查询指定周期内, 指定币种的强势记录
	 * @param logId			日志ID
	 * @param symbol		标的
	 * @param beginTime		开始时间
	 * @param endTime		结束时间
	 * @return	强势记录
	 */
	List<SymbolStrongPo> listSymbolStrongInPeriod(Long logId, String symbol, Date beginTime,Date endTime);
}
