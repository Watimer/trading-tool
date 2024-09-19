package com.wizard.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONArray;
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
import java.util.ArrayList;
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
		return saveBatch(list,10);
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

	@Override
	public boolean syncSymbolInfo(long logId) {
		String URL = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?limit=3000";
		String result = HttpRequest.get(URL)
				.header("X-CMC_PRO_API_KEY","53aad728-b547-4fca-a78c-b5d2479de5ea")
				.contentType("application/json")
				.charset(CharsetUtil.UTF_8)
				.execute()
				.body();
		log.info("日志ID:{},查询结果:{}",logId,result);
		if(StrUtil.isBlank(result)){
			return false;
		}
		JSONObject jsonObject = JSONObject.parseObject(result);
		if(ObjectUtil.isNull(jsonObject)){
			return false;
		}
		JSONObject statusJson = jsonObject.getJSONObject("status");
		if(ObjectUtil.isNull(statusJson)){
			return false;
		}
		if(!"0".equals(statusJson.getString("error_code"))){
			// TODO 接口请求失败
			return false;
		}
		List<JSONObject> jsonObjectList = JSONArray.parseArray(jsonObject.getString("data"), JSONObject.class);
		List<SymbolInfoPo> list = new ArrayList<>();
		for (JSONObject json : jsonObjectList) {
			List<String> tagList = JSONArray.parseArray(json.getString("tags"),String.class);
			String tagString = String.join(",", tagList);
			if(tagString.length() > 500){
				tagString = tagString.substring(0,500);
			}
			SymbolInfoPo symbolInfoPo = SymbolInfoPo.builder()
					.symbolName(json.getString("slug"))
					.symbol(json.getString("symbol"))
					.id(IdWorker.getId())
					.content(json.getString("name"))
					.tags(StrUtil.isBlank(tagString)?json.getString("symbol"):tagString)
					//.institution(json.getString("symbol"))
					.createTime(DateTime.now())
					.rank(json.getInteger("cmc_rank"))
					.circulatingSupply(json.getBigDecimal("circulating_supply"))
					.totalSupply(json.getBigDecimal("total_supply"))
					.maxSupply(ObjectUtil.isNull(json.getLong("max_supply"))?0L:json.getLong("max_supply"))
					.delFlag(0)
					.build();
			if(!symbolInfoPo.getSymbol().equals("BTT")){
				list.add(symbolInfoPo);
			}
		}
		remove(new LambdaQueryWrapper<>(SymbolInfoPo.class));
		return saveBatchList(logId,list);
	}
}
