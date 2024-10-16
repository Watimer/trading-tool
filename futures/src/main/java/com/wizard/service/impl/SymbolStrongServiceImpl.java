package com.wizard.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wizard.common.constants.ApiConstant;
import com.wizard.mapper.SymbolStrongMapper;
import com.wizard.model.po.SymbolStrongPo;
import com.wizard.service.SymbolStrongService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author 岳耀栎
 * @date 2024-10-16
 * @desc
 */
@Slf4j
@Service
public class SymbolStrongServiceImpl extends ServiceImpl<SymbolStrongMapper, SymbolStrongPo> implements SymbolStrongService {

	@Resource
	SymbolStrongMapper symbolStrongMapper;

	@Override
	public boolean saveBatchList(Long logId, List<SymbolStrongPo> symbolStrongPoList) {
		symbolStrongPoList.stream().forEach(symbolStrongPo -> {
			symbolStrongPo.setId(IdWorker.getId());
			symbolStrongPo.setDelFlag(ApiConstant.SUCCESS_CODE);
			symbolStrongPo.setCreateTime(new Date());
		});
		return saveBatch(symbolStrongPoList);
	}

	/**
	 * 查询指定周期内, 指定币种的强势记录
	 * @param logId  					日志ID
	 * @param symbol 					标的
	 * @param beginTime 				开始时间
	 * @param endTime 					结束时间
	 * @return 强势记录
	 */
	@Override
	public List<SymbolStrongPo> listSymbolStrongInPeriod(Long logId, String symbol, Date beginTime, Date endTime) {
		// 如果结束时间未指定，则默认为当前时间
		if(ObjectUtil.isNull(endTime)){
			endTime = new Date();
		}
		LambdaQueryWrapper<SymbolStrongPo> queryWrapper = new LambdaQueryWrapper<>(SymbolStrongPo.class);
		queryWrapper.eq(SymbolStrongPo::getSymbolName, symbol)
				.eq(SymbolStrongPo::getDelFlag, ApiConstant.SUCCESS_CODE)
				.gt(SymbolStrongPo::getCreateTime, beginTime)
				.lt(SymbolStrongPo::getCreateTime, endTime);

		return symbolStrongMapper.selectList(queryWrapper);
	}
}
