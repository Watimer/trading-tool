package com.wizard.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wizard.common.constants.ApiConstant;
import com.wizard.mapper.SymbolCategoryMapper;
import com.wizard.model.po.SymbolCategoryPo;
import com.wizard.service.SymbolCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class SymbolCategoryServiceImpl extends ServiceImpl<SymbolCategoryMapper, SymbolCategoryPo> implements SymbolCategoryService {

    @Override
    public boolean saveBatchList(Long logId, List<SymbolCategoryPo> symbolCategoryPoList) {
        symbolCategoryPoList.stream().forEach(symbolCategoryPo -> {
            symbolCategoryPo.setId(IdWorker.getId());
            symbolCategoryPo.setDelFlag(ApiConstant.SUCCESS_CODE);
            symbolCategoryPo.setCreateTime(new Date());
        });
        log.info("日志ID:{},待添加数据:{}", logId, JSONObject.toJSONString(symbolCategoryPoList));
        return saveBatch(symbolCategoryPoList);
    }
}
