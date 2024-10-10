package com.wizard.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wizard.model.po.SymbolInfoPo;

import java.util.List;

/**
 * @author wizard
 * @date 2024-09-19
 * @desc
 */
public interface SymbolInfoService {

	/**
	 * 批量新增标的信息
	 * @param logId		日志ID
	 * @param list		标的列表
	 * @return	新增成功或失败
	 */
	boolean saveBatchList(Long logId, List<SymbolInfoPo> list);

	/**
	 * 查询标的信息
	 * @param symbol	标的简称
	 * @return
	 */
	SymbolInfoPo getSymbolInfo(String symbol);

	List<SymbolInfoPo> listSymbolInfoPo(Long logId, LambdaQueryWrapper<SymbolInfoPo> queryWrapper);

	boolean syncSymbolInfo(long id);
}
