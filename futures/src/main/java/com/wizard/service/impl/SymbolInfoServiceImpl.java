package com.wizard.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wizard.mapper.SymbolInfoMapper;
import com.wizard.model.po.SymbolInfoPo;
import com.wizard.service.SymbolInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * @author 岳耀栎
 * @date 2024-09-19
 * @desc
 */
@Slf4j
@Service
public class SymbolInfoServiceImpl extends ServiceImpl<SymbolInfoMapper, SymbolInfoPo> implements SymbolInfoService {

	/**
	 * 批量新增标的信息
	 *
	 * @param logId 日志ID
	 * @param list  标的列表
	 * @return 新增成功或失败
	 */
	@Override
	@Transactional
	public boolean saveBatchList(Long logId, List<SymbolInfoPo> list) {
		list.forEach(item ->{
			if(ObjectUtil.isNull(item.getId())){
				item.setId(IdWorker.getId());
			}
			item.setCreateTime(DateTime.now());
			item.setDelFlag(0);
		});
		log.info("日志ID:{},添加信息:{}",logId, JSONObject.toJSONString(list));
		return saveBatch(list);
	}

	/**
	 * 查询标的信息
	 *
	 * @param symbol 标的简称
	 * @return
	 */
	@Override
	public SymbolInfoPo getSymbolInfo(String symbol) {
		return getOne(new LambdaQueryWrapper<>(SymbolInfoPo.class).eq(SymbolInfoPo::getSymbol, symbol).last("limit 1"));
	}

	@Override
	public List<SymbolInfoPo> listSymbolInfoPo(Long logId, LambdaQueryWrapper<SymbolInfoPo> queryWrapper) {
		return list(queryWrapper);
	}
}
