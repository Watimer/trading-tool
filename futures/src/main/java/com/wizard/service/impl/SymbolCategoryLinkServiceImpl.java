package com.wizard.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wizard.common.constants.ApiConstant;
import com.wizard.mapper.SymbolCategoryLinkMapper;
import com.wizard.model.po.SymbolCategoryLinkPo;
import com.wizard.service.SymbolCategoryLinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author wizard
 * @date 2024-10-10
 * @desc
 */
@Slf4j
@Service
public class SymbolCategoryLinkServiceImpl extends ServiceImpl<SymbolCategoryLinkMapper, SymbolCategoryLinkPo> implements SymbolCategoryLinkService {

	@Override
	public boolean saveBatchList(Long logId, List<SymbolCategoryLinkPo> list) {
		list.stream().forEach(symbolCategoryLinkPo -> {
			symbolCategoryLinkPo.setDelFlag(ApiConstant.SUCCESS_CODE);
			symbolCategoryLinkPo.setCreateTime(new Date());
			symbolCategoryLinkPo.setId(IdWorker.getId());
		});
		return saveBatch(list);
	}
}
