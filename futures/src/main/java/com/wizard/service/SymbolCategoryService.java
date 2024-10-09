package com.wizard.service;

import com.wizard.model.po.SymbolCategoryPo;

import java.util.List;

public interface SymbolCategoryService {


    boolean saveBatchList(Long logId,List<SymbolCategoryPo> symbolCategoryPoList);
}
