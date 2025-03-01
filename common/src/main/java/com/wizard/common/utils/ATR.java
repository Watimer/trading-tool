package com.wizard.common.utils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import xlc.quant.data.indicator.Indicator;
import xlc.quant.data.indicator.IndicatorCalculateCarrier;
import xlc.quant.data.indicator.IndicatorCalculator;
import xlc.quant.data.indicator.calculator.BOLL;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author 岳耀栎
 * @date 2025-02-22
 * @desc ATR 指标,TR 通过计算一定周期内的“真实范围”（True Range）并取其平均值来得出。真实范围的计算公式是：
 * TR = max(High - Low, |High - Previous Close|, |Low - Previous Close|)
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class ATR extends Indicator {

	private Double atrValue;

	ATR(Double atrValue){
		super();
		this.atrValue = atrValue;
	}

	public static <CARRIER extends IndicatorCalculateCarrier<?>> IndicatorCalculator<CARRIER, ATR> buildCalculator(
			int capacity,int period, int indicatorSetScale,
			BiConsumer<CARRIER, ATR> propertySetter,
			Function<CARRIER, ATR> propertyGetter) {
		return new ATRCalculator<>(capacity,period,indicatorSetScale,propertySetter,propertyGetter);
	}

	private static class ATRCalculator<CARRIER extends IndicatorCalculateCarrier<?>> extends IndicatorCalculator<CARRIER, ATR> {

		/**
		 * 指标精度
		 */
		private final int indicatorSetScale;


		private final Function<CARRIER, ATR> propertyGetter;

		/**
		 * ATR 周期
		 */
		private final int period;

		ATRCalculator(int capacity,int period, int indicatorSetScale,
					  BiConsumer<CARRIER, ATR> propertySetter,
					  Function<CARRIER, ATR> propertyGetter){
			super(capacity, true,propertySetter);
			this.period = period;
			this.indicatorSetScale = indicatorSetScale;
			this.propertyGetter = propertyGetter;

		}
		/**
		 * 执行计算，由子类具体某个指标的计算器实现
		 *
		 * @return
		 */
		@Override
		protected ATR executeCalculate() {
			log.info("进入ATR计算器:{}",getHead());
			log.info("进入ATR计算器:{}",getDataList());
			List<Double> highs = new ArrayList<>();
			List<Double> lows = new ArrayList<>();
			List<Double> closes = new ArrayList<>();
			int capacity = capacity();
			for (int i = 0; i <capacity; i++) {
				highs.add(get(i).getHigh());
				lows.add(get(i).getLow());
				closes.add(get(i).getClose());
			}

			double atr = 0;
			for (int i = 1; i <= period; i++) {
				double highToday = highs.get(i);
				double lowToday = lows.get(i);
				double closeYesterday = closes.get(i - 1);
				double tr = calculateTrueRange(highToday, lowToday, closeYesterday);
				atr += tr;
			}

			Double res = atr / period;
			return new ATR(res);
		}
	}

	// 计算True Range（TR）
	private static double calculateTrueRange(double highToday, double lowToday, double closeYesterday) {
		double temp = 0.0;
		try {
			temp = Math.max(Math.max(highToday - lowToday, Math.abs(highToday - closeYesterday)), Math.abs(lowToday - closeYesterday));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return temp;
	}
}
