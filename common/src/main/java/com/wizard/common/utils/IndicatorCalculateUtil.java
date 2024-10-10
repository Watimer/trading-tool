package com.wizard.common.utils;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.wizard.common.model.*;
import xlc.quant.data.indicator.IndicatorCalculator;
import xlc.quant.data.indicator.IndicatorWarehouseManager;
import xlc.quant.data.indicator.calculator.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wizard
 * @date 2024-10-10
 * @desc 指标计算工具类
 */
public class IndicatorCalculateUtil {

	public static int getIntByDouble(Double d){
		return NumberUtil.parseInt(d.toString());
	}

	// TODO 自定义指标参数

	/**
	 *
	 * @param marketQuotationList	行情数据
	 * @param indicatorSetScale		量价指标保留的小数点位数
	 * @param kdj					KDJ指标,传入则以此参数计算
	 * @param macd					MACD指标,传入则以此参数计算
	 * @param boll					布林带指标,传入则以此参数计算
	 * @param dmi					DMI指标,传入则以此参数计算
	 * @param td					TD九转序列指标,传入则以此参数计算
	 * @param cci					CCI指标,传入则以此参数计算
	 * @param ma					简单平均线指标,传入则以此参数计算
	 * @param ema					移动平均线指标,传入则以此参数计算
	 * @param rsi					RSI指标,传入则以此参数计算
	 * @param bias					BIAS指标,传入则以此参数计算
	 * @param wr					WR指标,传入则以此参数计算
	 */
	public static void individuationIndicatorCalculate(List<MarketQuotation> marketQuotationList,
													   int indicatorSetScale,
													   KDJParams kdj,
													   MacdParams macd,
													   BollParams boll,
													   DmiParams dmi,
													   TdParams td,
													   CCI cci,
													   MA ma,
													   EMA ema,
													   RSI rsi,
													   BiasParams bias,
													   WrParams wr){
		if(ObjectUtil.isNull(indicatorSetScale)){
			indicatorSetScale = 2;
		}
		List<IndicatorCalculator<MarketQuotation, ?>> indicatorCalculatorList =  new ArrayList<>();
		if(ObjectUtil.isNull(kdj)){
			indicatorCalculatorList.add(KDJ.buildCalculator(kdj.getCapacity(),kdj.getKCycle(),kdj.getDCycle(),MarketQuotation::setKdj,MarketQuotation::getKdj));
		}
		if(ObjectUtil.isNull(macd)){
			indicatorCalculatorList.add(MACD.buildCalculator(macd.getFastCycle(),macd.getSlowCycle(),macd.getDifCycle(),indicatorSetScale,MarketQuotation::setMacd,MarketQuotation::getMacd));
		}
		if(ObjectUtil.isNull(boll)){
			// BOLL-计算器
			indicatorCalculatorList.add(BOLL.buildCalculator(boll.getCapacity(), boll.getD(),indicatorSetScale,MarketQuotation::setBoll,MarketQuotation::getBoll));
		}
		if(ObjectUtil.isNull(dmi)){
			// DMI-计算
			indicatorCalculatorList.add(DMI.buildCalculator(dmi.getDiPeriod(), dmi.getAdxPeriod(),MarketQuotation::setDmi,MarketQuotation::getDmi));
		}

		if(ObjectUtil.isNull(td)){
			// TD九转序列-计算器
			indicatorCalculatorList.add(TD.buildCalculator(td.getCapacity(), td.getMoveSize(),MarketQuotation::setTd,MarketQuotation::getTd));
		}
		List<IndicatorCalculator<MarketQuotation, ?>> calculatorConfig = indicatorCalculatorList;
		int maximum =400;//管理指标载体的最大数量
		IndicatorWarehouseManager<LocalDateTime,MarketQuotation> calculateManager = new IndicatorWarehouseManager<>(maximum, calculatorConfig);
		//循环-管理员接收 新行情数据-进行批量计算所有指标
		for (MarketQuotation mq : marketQuotationList) {
			calculateManager.accept(mq);
		}
	}


	/**
	 * 演示计算单个指标
	 * @param marketQuotationList 行情数据
	 * @param indicatorSetScale   量价指标保留的小数点位数
	 */
	public static void singleIndicatorCalculate(List<MarketQuotation> marketQuotationList, int indicatorSetScale) {
		// 布林带计算
		IndicatorCalculator<MarketQuotation,BOLL> bollCalculator =BOLL.buildCalculator(400,2d,indicatorSetScale,MarketQuotation::setBoll,MarketQuotation::getBoll);
		marketQuotationList.stream().forEach(item ->{
			BOLL boll = bollCalculator.input(item);
			item.setBoll(boll);
		});
	}

	/**
	 * 计算全部指标,指标参数使用默认值
	 * @param marketQuotationList 行情数据
	 * @param indicatorSetScale   量价指标保留的小数点位数
	 */
	public static void multipleIndicatorCalculate(List<MarketQuotation> marketQuotationList, int indicatorSetScale) {
		if(ObjectUtil.isNull(indicatorSetScale)){
			indicatorSetScale = 2;
		}
		List<IndicatorCalculator<MarketQuotation, ?>> calculatorConfig = buildIndicatorCalculatorList(indicatorSetScale);
		int maximum =400;//管理指标载体的最大数量
		IndicatorWarehouseManager<LocalDateTime,MarketQuotation> calculateManager = new IndicatorWarehouseManager<>(maximum, calculatorConfig);

		//循环-管理员接收 新行情数据-进行批量计算所有指标
		for (MarketQuotation mq : marketQuotationList) {
			calculateManager.accept(mq);
		}
	}

	/**
	 * @param indicatorSetScale  指标精度的小数位
	 */
	protected static List<IndicatorCalculator<MarketQuotation, ?>> buildIndicatorCalculatorList(int indicatorSetScale) {
		List<IndicatorCalculator<MarketQuotation, ?>> indicatorCalculatorList =  new ArrayList<>();
		//技术指标===多值指标 XXX
		// KDJ-计算器
		indicatorCalculatorList.add(KDJ.buildCalculator(9,3,3,MarketQuotation::setKdj,MarketQuotation::getKdj));
		// MACD-计算器
		indicatorCalculatorList.add(MACD.buildCalculator(12, 26, 9,indicatorSetScale,MarketQuotation::setMacd,MarketQuotation::getMacd));
		// BOLL-计算器
		indicatorCalculatorList.add(BOLL.buildCalculator(20, 2,indicatorSetScale,MarketQuotation::setBoll,MarketQuotation::getBoll));
		// DMI-计算
		indicatorCalculatorList.add(DMI.buildCalculator(14, 6,MarketQuotation::setDmi,MarketQuotation::getDmi));

		// 技术指标===单属性值指标 XXX
		// TD九转序列-计算器
		indicatorCalculatorList.add(TD.buildCalculator(9, 4,MarketQuotation::setTd,MarketQuotation::getTd));
		// CCI-计算器: 顺势指标
		indicatorCalculatorList.add(CCI.buildCalculator(14,indicatorSetScale,MarketQuotation::setCci14,MarketQuotation::getCci14));

		//MA-计算器: 移动平均线
		indicatorCalculatorList.add(MA.buildCalculator(5,indicatorSetScale,MarketQuotation::setMa5));
		indicatorCalculatorList.add(MA.buildCalculator(10,indicatorSetScale,MarketQuotation::setMa10));
		indicatorCalculatorList.add(MA.buildCalculator(20,indicatorSetScale,MarketQuotation::setMa20));
		indicatorCalculatorList.add(MA.buildCalculator(40,indicatorSetScale,MarketQuotation::setMa40));
		indicatorCalculatorList.add(MA.buildCalculator(60,indicatorSetScale,MarketQuotation::setMa60));

		//EMA-计算器: 指数平滑移动平均线，简称指数平均线。
		indicatorCalculatorList.add(EMA.buildCalculator(5,indicatorSetScale,MarketQuotation::setEma5,MarketQuotation::getEma10));
		indicatorCalculatorList.add(EMA.buildCalculator(10,indicatorSetScale,MarketQuotation::setEma10,MarketQuotation::getEma10));
		indicatorCalculatorList.add(EMA.buildCalculator(20,indicatorSetScale,MarketQuotation::setEma20,MarketQuotation::getEma20));
		indicatorCalculatorList.add(EMA.buildCalculator(60,indicatorSetScale,MarketQuotation::setEma60,MarketQuotation::getEma60));

		//RSI-计算器: 相对强弱指标
		indicatorCalculatorList.add(RSI.buildCalculator(6,MarketQuotation::setRsi6,MarketQuotation::getRsi6));
		indicatorCalculatorList.add(RSI.buildCalculator(12,MarketQuotation::setRsi12,MarketQuotation::getRsi12));
		indicatorCalculatorList.add(RSI.buildCalculator(24,MarketQuotation::setRsi24,MarketQuotation::getRsi24));

		//BIAS-计算器: 乖离率指标
		indicatorCalculatorList.add(BIAS.buildCalculator(6,MarketQuotation::setBias6));
		indicatorCalculatorList.add(BIAS.buildCalculator(12,MarketQuotation::setBias12));
		indicatorCalculatorList.add(BIAS.buildCalculator(24,MarketQuotation::setBias24));

		//WR-计算器: 威廉指标
		indicatorCalculatorList.add(WR.buildCalculator(6,MarketQuotation::setWr6));
		indicatorCalculatorList.add(WR.buildCalculator(10,MarketQuotation::setWr10));
		indicatorCalculatorList.add(WR.buildCalculator(14,MarketQuotation::setWr14));
		indicatorCalculatorList.add(WR.buildCalculator(20,MarketQuotation::setWr20));

		return indicatorCalculatorList;
	}
}
