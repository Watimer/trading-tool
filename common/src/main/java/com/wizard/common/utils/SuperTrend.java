package com.wizard.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.BiConsumer;
import java.util.function.Function;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import xlc.quant.data.indicator.Indicator;
import xlc.quant.data.indicator.IndicatorCalculateCarrier;
import xlc.quant.data.indicator.IndicatorCalculator;

/**
 * @author wizard
 * @date 2025-01-06
 * @desc 超级趋势（SuperTrend）指标
 * 
 *       <pre>
 * 公式说明：
 * SuperTrend指标基于ATR（平均真实波动范围）和价格数据计算趋势方向和支撑/阻力位。
 * 完全按照TradingView标准实现。
 * 
 * 计算步骤：
 * 1. 计算价格源：可选择收盘价(close)或hl2 = (high + low) / 2
 * 2. 计算ATR（平均真实波动范围）
 *    TR = max(High - Low, |High - Previous Close|, |Low - Previous Close|)
 *    ATR = RMA(TR, atrPeriods) 或 SMA(TR, atrPeriods)
 * 
 * 3. 计算基础上下轨线：
 *    basicUpperBand = 价格源 + (multiplier × ATR)
 *    basicLowerBand = 价格源 - (multiplier × ATR)
 * 
 * 4. 计算最终上下轨线：
 *    upperBand = basicUpperBand < prev upperBand or prev close > prev upperBand ? basicUpperBand : prev upperBand
 *    lowerBand = basicLowerBand > prev lowerBand or prev close < prev lowerBand ? basicLowerBand : prev lowerBand
 * 
 * 5. 确定趋势方向：
 *    if prev superTrend == prev upperBand
 *        trendDirection := close > upperBand ? isUpTrend : isDownTrend
 *    else
 *        trendDirection := close < lowerBand ? isDownTrend : isUpTrend
 * 
 * 6. SuperTrend值：
 *    superTrend = trendDirection == isUpTrend ? lowerBand : upperBand
 * 
 * 默认参数：
 * - ATR周期：13
 * - ATR乘数：3.0
 * - 价格源：hl2 = (high + low) / 2
 * - ATR计算：RMA（指数移动平均）
 *       </pre>
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SuperTrend extends Indicator {

    /** 上轨线（Upper Band）：阻力线 */
    private Double upperBand;

    /** 下轨线（Lower Band）：支撑线 */
    private Double lowerBand;

    /** 趋势方向：1=上升趋势，-1=下降趋势 */
    private Integer trend;

    /** 买入信号：趋势从下降转为上升 */
    private Boolean buySignal;

    /** 卖出信号：趋势从上升转为下降 */
    private Boolean sellSignal;

    /** SuperTrend值：当前有效的趋势线值 */
    private Double supertrendValue;

    /** 是否为上升趋势 */
    private Boolean isUptrend;

    /** ATR值 */
    private Double atr;

    public SuperTrend(Double upperBand, Double lowerBand, Integer trend,
            Boolean buySignal, Boolean sellSignal, Double supertrendValue, Boolean isUptrend) {
        super();
        this.upperBand = upperBand;
        this.lowerBand = lowerBand;
        this.trend = trend;
        this.buySignal = buySignal;
        this.sellSignal = sellSignal;
        this.supertrendValue = supertrendValue;
        this.isUptrend = isUptrend;
    }

    /**
     * 转换为兼容的Supertrend模型
     */
    public com.wizard.common.model.Supertrend toSupertrendModel() {
        return new com.wizard.common.model.Supertrend(
                this.supertrendValue != null ? this.supertrendValue : 0.0,
                this.isUptrend != null ? this.isUptrend : true);
    }

    /**
     * 从兼容的Supertrend模型创建SuperTrend对象
     */
    public static SuperTrend fromSupertrendModel(com.wizard.common.model.Supertrend supertrend) {
        if (supertrend == null) {
            return null;
        }
        return new SuperTrend(
                supertrend.getValue(), // upperBand
                supertrend.getValue(), // lowerBand
                supertrend.isUptrend() ? 1 : -1, // trend
                false, // buySignal
                false, // sellSignal
                supertrend.getValue(), // supertrendValue
                supertrend.isUptrend() // isUptrend
        );
    }

    /**
     * 格式化Double值到指定精度
     */
    private static Double formatDouble(double value, int scale) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return null;
        }
        return BigDecimal.valueOf(value)
                .setScale(scale, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * 价格源类型枚举
     */
    public enum PriceSource {
        /** 收盘价 */
        CLOSE,
        /** (最高价 + 最低价) / 2 */
        HL2
    }

    // =============
    // 内部类分隔符 XXX
    // =============
    /**
     * 构建计算器（完整参数）
     * 
     * @param atrPeriods        ATR计算周期
     * @param multiplier        ATR乘数
     * @param priceSource       价格源类型
     * @param useRMA            是否使用RMA计算ATR（true=RMA, false=SMA）
     * @param indicatorSetScale 指标精度
     * @param propertySetter    属性设置器
     * @param propertyGetter    属性获取器
     * @return SuperTrend计算器
     */
    public static <CARRIER extends IndicatorCalculateCarrier<?>> IndicatorCalculator<CARRIER, SuperTrend> buildCalculator(
            int atrPeriods, double multiplier, PriceSource priceSource, boolean useRMA, int indicatorSetScale,
            BiConsumer<CARRIER, SuperTrend> propertySetter, Function<CARRIER, SuperTrend> propertyGetter) {
        return new SuperTrendCalculator<>(atrPeriods, multiplier, priceSource, useRMA, indicatorSetScale,
                propertySetter,
                propertyGetter);
    }

    /**
     * 构建计算器（使用默认RMA）
     * 
     * @param atrPeriods        ATR计算周期
     * @param multiplier        ATR乘数
     * @param priceSource       价格源类型
     * @param indicatorSetScale 指标精度
     * @param propertySetter    属性设置器
     * @param propertyGetter    属性获取器
     * @return SuperTrend计算器
     */
    public static <CARRIER extends IndicatorCalculateCarrier<?>> IndicatorCalculator<CARRIER, SuperTrend> buildCalculator(
            int atrPeriods, double multiplier, PriceSource priceSource, int indicatorSetScale,
            BiConsumer<CARRIER, SuperTrend> propertySetter, Function<CARRIER, SuperTrend> propertyGetter) {
        return new SuperTrendCalculator<>(atrPeriods, multiplier, priceSource, true, indicatorSetScale, propertySetter,
                propertyGetter);
    }

    /**
     * 构建计算器（使用默认参数，HL2价格源）
     * 
     * @param indicatorSetScale 指标精度
     * @param propertySetter    属性设置器
     * @param propertyGetter    属性获取器
     * @return SuperTrend计算器
     */
    public static <CARRIER extends IndicatorCalculateCarrier<?>> IndicatorCalculator<CARRIER, SuperTrend> buildCalculator(
            int indicatorSetScale,
            BiConsumer<CARRIER, SuperTrend> propertySetter, Function<CARRIER, SuperTrend> propertyGetter) {
        return new SuperTrendCalculator<>(13, 3.0, PriceSource.HL2, true, indicatorSetScale, propertySetter,
                propertyGetter);
    }

    /**
     * 构建计算器（使用收盘价作为数据源）
     * 
     * @param atrPeriods        ATR计算周期
     * @param multiplier        ATR乘数
     * @param indicatorSetScale 指标精度
     * @param propertySetter    属性设置器
     * @param propertyGetter    属性获取器
     * @return SuperTrend计算器
     */
    public static <CARRIER extends IndicatorCalculateCarrier<?>> IndicatorCalculator<CARRIER, SuperTrend> buildCalculatorWithClose(
            int atrPeriods, double multiplier, int indicatorSetScale,
            BiConsumer<CARRIER, SuperTrend> propertySetter, Function<CARRIER, SuperTrend> propertyGetter) {
        return new SuperTrendCalculator<>(atrPeriods, multiplier, PriceSource.CLOSE, true, indicatorSetScale,
                propertySetter,
                propertyGetter);
    }

    /**
     * SuperTrend计算器
     * 
     * @author wizard
     */
    private static class SuperTrendCalculator<CARRIER extends IndicatorCalculateCarrier<?>>
            extends IndicatorCalculator<CARRIER, SuperTrend> {
        /** 默认ATR周期 */
        private static final int DEFAULT_ATR_PERIODS = 13;
        /** 默认ATR乘数 */
        private static final double DEFAULT_MULTIPLIER = 3.0;
        /** 默认使用RMA */
        private static final boolean DEFAULT_USE_RMA = true;
        /** 默认价格源 */
        private static final PriceSource DEFAULT_PRICE_SOURCE = PriceSource.HL2;

        /** ATR计算周期 */
        private final int atrPeriods;

        /** ATR乘数 */
        private final double multiplier;

        /** 价格源类型 */
        private final PriceSource priceSource;

        /** 是否使用RMA计算ATR */
        private final boolean useRMA;

        /** 指标精度 */
        private final int indicatorSetScale;

        /** 委托方法，从载体类获取计算结果的方法 */
        private final Function<CARRIER, SuperTrend> propertyGetter;

        /** RMA计算用的前一个ATR值 */
        private Double previousATR = null;

        /** 前一个计算结果 */
        private SuperTrend previousResult = null;

        /**
         * SuperTrend计算器构造函数
         * capacity参数被忽略，直接使用atrPeriods作为数据容量
         * 
         * @param atrPeriods        ATR计算周期，默认13，同时作为数据容量
         * @param multiplier        ATR乘数，默认3.0
         * @param priceSource       价格源类型，默认HL2
         * @param useRMA            是否使用RMA计算ATR，默认true
         * @param indicatorSetScale 指标精度
         * @param propertySetter    属性设置器
         * @param propertyGetter    属性获取器
         */
        SuperTrendCalculator(int atrPeriods, double multiplier, PriceSource priceSource, boolean useRMA,
                int indicatorSetScale, BiConsumer<CARRIER, SuperTrend> propertySetter,
                Function<CARRIER, SuperTrend> propertyGetter) {
            // capacity直接使用atrPeriods，忽略外部传入的capacity参数
            super(atrPeriods <= 0 ? DEFAULT_ATR_PERIODS : atrPeriods, true, propertySetter);
            this.atrPeriods = atrPeriods <= 0 ? DEFAULT_ATR_PERIODS : atrPeriods;
            this.multiplier = multiplier <= 0 ? DEFAULT_MULTIPLIER : multiplier;
            this.priceSource = priceSource != null ? priceSource : DEFAULT_PRICE_SOURCE;
            this.useRMA = useRMA;
            this.indicatorSetScale = indicatorSetScale;
            this.propertyGetter = propertyGetter;
        }

        /**
         * 获取前一个SuperTrend计算结果
         */
        private SuperTrend getPreviousResult() {
            return previousResult;
        }

        /**
         * 根据价格源类型计算价格值
         */
        private double calculatePriceSource(CARRIER carrier) {
            switch (priceSource) {
                case CLOSE:
                    return carrier.getClose();
                case HL2:
                default:
                    return (carrier.getHigh() + carrier.getLow()) / 2.0;
            }
        }

        /**
         * 计算当前ATR值（使用RMA方式，严格按照TradingView标准）
         */
        private double calculateCurrentATR() {
            if (capacity() < 2) {
                return 0.0;
            }

            CARRIER head = getHead();
            CARRIER prev = get(1);

            // 计算True Range
            double high = head.getHigh();
            double low = head.getLow();
            double prevClose = prev.getClose();

            double tr1 = high - low;
            double tr2 = Math.abs(high - prevClose);
            double tr3 = Math.abs(low - prevClose);
            double currentTR = Math.max(tr1, Math.max(tr2, tr3));

            // TradingView的RMA计算方式
            if (previousATR == null) {
                // 第一次计算：使用SMA作为初始值
                if (capacity() < atrPeriods + 1) {
                    // 数据不足时，使用当前可用数据的SMA
                    double sum = 0.0;
                    int count = 0;
                    int maxIndex = Math.min(capacity() - 1, atrPeriods);
                    for (int i = 0; i < maxIndex; i++) {
                        CARRIER bar = get(i);
                        CARRIER prevBar = get(i + 1);

                        double h = bar.getHigh();
                        double l = bar.getLow();
                        double pc = prevBar.getClose();

                        double tr = Math.max(h - l, Math.max(Math.abs(h - pc), Math.abs(l - pc)));
                        sum += tr;
                        count++;
                    }
                    return count > 0 ? sum / count : currentTR;
                } else {
                    // 有足够数据时，计算完整的SMA作为初始ATR
                    double sum = 0.0;
                    for (int i = 0; i < atrPeriods; i++) {
                        CARRIER bar = get(i);
                        CARRIER prevBar = get(i + 1);

                        double h = bar.getHigh();
                        double l = bar.getLow();
                        double pc = prevBar.getClose();

                        double tr = Math.max(h - l, Math.max(Math.abs(h - pc), Math.abs(l - pc)));
                        sum += tr;
                    }
                    return sum / atrPeriods;
                }
            } else {
                // 后续计算，使用RMA公式：RMA = (prevRMA * (n-1) + currentValue) / n
                return (previousATR * (atrPeriods - 1) + currentTR) / atrPeriods;
            }
        }

        @Override
        protected SuperTrend executeCalculate() {
            CARRIER head = getHead();

            // 至少需要2个数据点来计算TR
            if (capacity() < 2) {
                return null;
            }

            // 计算当前ATR值
            double currentATR = calculateCurrentATR();

            // 如果ATR为0或无效，返回null
            if (currentATR <= 0 || Double.isNaN(currentATR)) {
                return null;
            }

            // 计算价格源：根据设置选择收盘价或hl2
            double priceSourceValue = calculatePriceSource(head);

            // 计算基础上下轨线（严格按照TradingView标准）
            double basicUpperBand = priceSourceValue + (multiplier * currentATR);
            double basicLowerBand = priceSourceValue - (multiplier * currentATR);

            // 获取前一个SuperTrend结果
            SuperTrend prevResult = getPreviousResult();
            double prevUpperBand = prevResult != null ? prevResult.getUpperBand() : basicUpperBand;
            double prevLowerBand = prevResult != null ? prevResult.getLowerBand() : basicLowerBand;
            double prevClose = capacity() > 1 ? get(1).getClose() : head.getClose();

            // 计算最终上下轨线（严格按照TradingView逻辑）
            double finalUpperBand = (basicUpperBand < prevUpperBand || prevClose > prevUpperBand) ? basicUpperBand
                    : prevUpperBand;
            double finalLowerBand = (basicLowerBand > prevLowerBand || prevClose < prevLowerBand) ? basicLowerBand
                    : prevLowerBand;

            // 计算趋势方向（严格按照TradingView Pine Script逻辑）
            int direction;

            if (prevResult == null) {
                // 第一次计算，按照TradingView标准初始化为下降趋势（direction = -1）
                direction = -1;
            } else {
                double prevSuperTrend = prevResult.getSupertrendValue();

                // 按照Pine Script逻辑判断趋势
                if (Math.abs(prevSuperTrend - prevUpperBand) < 0.0001) { // prevSuperTrend == prevUpperBand
                    // 前一个SuperTrend等于上轨，检查是否突破上轨
                    direction = head.getClose() > finalUpperBand ? -1 : 1;
                } else {
                    // 前一个SuperTrend等于下轨，检查是否跌破下轨
                    direction = head.getClose() < finalLowerBand ? 1 : -1;
                }
            }

            // 计算SuperTrend值
            double superTrendValue = (direction == -1) ? finalLowerBand : finalUpperBand;

            // 判断买卖信号
            boolean buySignal = false;
            boolean sellSignal = false;
            if (prevResult != null) {
                int prevDirection = prevResult.getTrend();
                buySignal = (prevDirection == 1 && direction == -1);
                sellSignal = (prevDirection == -1 && direction == 1);
            }

            // 保存当前ATR值供下次使用
            previousATR = currentATR;

            // 创建结果
            SuperTrend result = new SuperTrend();
            result.setSupertrendValue(formatDouble(superTrendValue, indicatorSetScale));
            result.setTrend(direction);
            result.setUpperBand(formatDouble(finalUpperBand, indicatorSetScale));
            result.setLowerBand(formatDouble(finalLowerBand, indicatorSetScale));
            result.setAtr(formatDouble(currentATR, indicatorSetScale));
            result.setIsUptrend(direction == -1);
            result.setBuySignal(buySignal);
            result.setSellSignal(sellSignal);

            // 保存当前结果供下次使用
            previousResult = result;

            return result;
        }
    }
}
