package com.wizard.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 巫师
 * @date 2025-01-06
 * @desc
 */
public class CalculateATR {

	// 计算 ATR（Average True Range）
	public static List<Double> calculateATR(List<Double> highs, List<Double> lows, List<Double> closes, int period) {
		List<Double> atr = new ArrayList<>();
		List<Double> trueRanges = new ArrayList<>();

		for (int i = 0; i < highs.size(); i++) {
			double highLow = highs.get(i) - lows.get(i);
			double highClose = i == 0 ? 0 : Math.abs(highs.get(i) - closes.get(i - 1));
			double lowClose = i == 0 ? 0 : Math.abs(lows.get(i) - closes.get(i - 1));
			double trueRange = Math.max(highLow, Math.max(highClose, lowClose));
			trueRanges.add(trueRange);

			if (i >= period - 1) {
				double sum = 0;
				for (int j = i; j > i - period; j--) {
					sum += trueRanges.get(j);
				}
				atr.add(sum / period);
			} else {
				atr.add(0.0); // 填充初始值
			}
		}

		return atr;
	}
}
