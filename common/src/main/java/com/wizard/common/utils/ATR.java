package com.wizard.common.utils;

import lombok.Data;
import lombok.EqualsAndHashCode;
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
 * @desc
 */
@Data
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

		private final int capacity;

		ATRCalculator(int capacity,int period,int indicatorSetScale,
					  BiConsumer<CARRIER, ATR> propertySetter,
					  Function<CARRIER, ATR> propertyGetter){
			super(capacity, true,propertySetter);
			this.period = period;
			this.capacity = capacity;
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
			List<Double> highs = new ArrayList<>();
			List<Double> lows = new ArrayList<>();
			List<Double> closes = new ArrayList<>();
			for (int i = 1; i < capacity(); i++) {
				highs.add(get(i).getHigh());
				lows.add(get(i).getLow());
				closes.add(get(i).getClose());
			}
			if (highs.size() < period || lows.size() < period || closes.size() < period) {
				//return new ATR(0.0);
			}

			double atr = 0;
			for (int i = 2; i <= period; i++) {
				double highToday = highs.get(i - 1);
				double lowToday = lows.get(i - 1);
				double closeYesterday = closes.get(i - 2);
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
