package com.wizard.service;

import com.wizard.model.po.SymbolCategoryLinkPo;

import java.util.List;

/**
 * @author wizard
 * @date 2024-10-10
 * @desc
 */
public interface SymbolCategoryLinkService {

	boolean saveBatchList(Long logId, List<SymbolCategoryLinkPo> list);
}
