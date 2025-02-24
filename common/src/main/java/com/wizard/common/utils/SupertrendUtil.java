package com.wizard.common.utils;

import com.wizard.common.model.MarketQuotation;
import com.wizard.common.model.Supertrend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 巫师
 * @date 2025-01-06
 * @desc
 */
public class SupertrendUtil {

	public static List<Supertrend> calculateSuperTrend(List<MarketQuotation> marketQuotationList, int period, double multiplier) {
		List<Double> highs = new ArrayList<>();
		List<Double> lows = new ArrayList<>();
		List<Double> closes = new ArrayList<>();
		marketQuotationList.forEach(marketQuotation -> {
			highs.add(marketQuotation.getHigh());
			lows.add(marketQuotation.getLow());
			closes.add(marketQuotation.getClose());
		});
		return calculateSupertrend(highs, lows, closes, period, multiplier);
	}

	// 计算超级趋势
	public static List<Supertrend> calculateSupertrend(List<Double> highs, List<Double> lows, List<Double> closes, int period, double multiplier) {
		List<Double> atr = CalculateATR.calculateATR(highs, lows, closes, period);
		List<Supertrend> supertrend = new ArrayList<>();

		double upperBand = 0;
		double lowerBand = 0;
		double supertrendValue = 0;
		boolean isUptrend = true;

		for (int i = 0; i < highs.size(); i++) {
			if (i < period - 1) {
				supertrend.add(new Supertrend(0, true)); // 填充初始值
				continue;
			}

			double hl2 = (highs.get(i) + lows.get(i)) / 2;
			upperBand = hl2 + (multiplier * atr.get(i));
			lowerBand = hl2 - (multiplier * atr.get(i));

			if (i == period - 1) {
				// 初始趋势方向
				isUptrend = closes.get(i) > upperBand;
				supertrendValue = isUptrend ? lowerBand : upperBand;
			} else {
				if (isUptrend && closes.get(i) < lowerBand) {
					isUptrend = false;
					supertrendValue = upperBand;
				} else if (!isUptrend && closes.get(i) > upperBand) {
					isUptrend = true;
					supertrendValue = lowerBand;
				} else {
					supertrendValue = isUptrend ? Math.max(lowerBand, supertrendValue) : Math.min(upperBand, supertrendValue);
				}
			}

			supertrend.add(new Supertrend(supertrendValue, isUptrend));
		}

		return supertrend;
	}

	public static void main(String[] args) {
		// 示例数据
		List<Double> highs = Arrays.asList(16189.0, 16362.0, 16428.0, 16435.0, 16298.0, 16446.0, 16435.0);
		List<Double> lows = Arrays.asList(15731.0, 15807.0, 15956.0, 15825.0, 15995.0, 16140.0, 15890.0);
		List<Double> closes = Arrays.asList(15918.0, 16260.0, 16104.0, 16162.0, 16257.0, 16391.0, 16157.0);

		int period = 3; // ATR周期
		double multiplier = 13; // 超级趋势系数

		// 计算超级趋势
		List<Supertrend> supertrend = calculateSupertrend(highs, lows, closes, period, multiplier);

		// 打印结果
		for (int i = 0; i < supertrend.size(); i++) {
			System.out.println("周期 " + (i + 1) + ": " + supertrend.get(i));
		}
	}
}
