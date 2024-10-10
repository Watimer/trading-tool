package com.wizard;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;
import com.wizard.common.model.MarketQuotation;
import com.wizard.common.utils.DataTransformationUtil;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xlc.quant.data.indicator.IndicatorCalculateCarrier;
import xlc.quant.data.indicator.IndicatorCalculator;
import xlc.quant.data.indicator.IndicatorWarehouseManager;
import xlc.quant.data.indicator.calculator.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class TestIn {

    private static final Logger log = LoggerFactory.getLogger(TestIn.class);

    public static void main(String[] args) {
        String source = FileUtil.readUtf8String("/Users/yueyaoli/wizard/trading-tool/futures/src/test/java/com/wizard/line.txt");
        //行情数据
        List<MarketQuotation> listMarketQuotation = DataTransformationUtil.transform("BTCUSD",source);
        //行情按照收盘时间正序排序。
        List<MarketQuotation> listMarketQuotationOrderByCloseTimeAsc= listMarketQuotation.stream().sorted(Comparator.comparing(MarketQuotation::getCloseTime)).collect(Collectors.toList());
        singleIndicatorCalculate(listMarketQuotationOrderByCloseTimeAsc);

        multipleIndicatorCalculate(listMarketQuotationOrderByCloseTimeAsc,1);

        Core core = new Core();
        int macdLockBack1 = core.macdLookback(12,26,9);
        int startIndex = 0;
        int entIndex = listMarketQuotationOrderByCloseTimeAsc.size()-1;
        double[] closePrice = new double[listMarketQuotationOrderByCloseTimeAsc.size()];
        for (int i= 0;i<listMarketQuotationOrderByCloseTimeAsc.size();i++) {
            closePrice[i] = listMarketQuotationOrderByCloseTimeAsc.get(i).getClose();
        }
        //MInteger类: mutable integer，可变整数
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        double[] outMacd = new double[entIndex];
        double[] outMacdSignal = new double[entIndex];
        double[] outMacdHist = new double[entIndex];
        RetCode retCode = core.macd(startIndex,entIndex,closePrice,12,26,9,begin,length,outMacd,outMacdSignal,outMacdHist);
        if(retCode == RetCode.Success){
            log.info("begin:{}",begin);
            log.info("length:{}",length);
            List<Macdss> listMacdss = new ArrayList<>();
            for (int i= 0;i<outMacd.length;i++) {
                Macdss macdss = new Macdss();
                macdss.setIndex(i);
                macdss.setFast(new BigDecimal(outMacd[i]));
                macdss.setLow(new BigDecimal(outMacdSignal[i]));
                macdss.setWw(new BigDecimal(outMacdHist[i]));
                if(macdss.getFast().compareTo(new BigDecimal("0")) != 0){
                    listMacdss.add(macdss);
                }

            }
            log.info("listMacdss:{}", JSONObject.toJSONString(listMacdss));
        }
        int macdLockBack = core.macdLookback(12,26,9);
        System.out.println(macdLockBack1);
        System.out.println(macdLockBack);
    }

    /**
     * 演示计算单个指标
     */
    public static void singleIndicatorCalculate(List<MarketQuotation> listMarketQuotationOrderByCloseTimeAsc) {
        //KDJ-计算器
        IndicatorCalculator<MarketQuotation,KDJ> kdjCalculator = KDJ.buildCalculator(9, 3, 3,MarketQuotation::setKdj,MarketQuotation::getKdj);

        for (MarketQuotation mq : listMarketQuotationOrderByCloseTimeAsc) {
            //KDJ-计算
            KDJ kdj = kdjCalculator.input(mq);
//            System.out.println(kdj);
        }


        // 布林带计算
        IndicatorCalculator<MarketQuotation,BOLL> bollCalculator =BOLL.buildCalculator(400,2d,2,MarketQuotation::setBoll,MarketQuotation::getBoll);
        listMarketQuotationOrderByCloseTimeAsc.stream().forEach(item ->{
            BOLL boll = bollCalculator.input(item);
            System.out.println(boll);
        });
    }

    /**
     * @param listMarketQuotationOrderByCloseTimeAsc
     * @param indicatorSetScale   量价指标保留的小数点位数
     */
    public static void multipleIndicatorCalculate(List<MarketQuotation> listMarketQuotationOrderByCloseTimeAsc,int indicatorSetScale) {
        List<IndicatorCalculator<MarketQuotation, ?>> calculatorConfig = buildIndicatorCalculatorList(2);
        int maximum =400;//管理指标载体的最大数量
        IndicatorWarehouseManager<LocalDateTime,MarketQuotation> calculateManager = new IndicatorWarehouseManager<>(maximum, calculatorConfig);

        //循环-管理员接收 新行情数据-进行批量计算所有指标
        for (MarketQuotation mq : listMarketQuotationOrderByCloseTimeAsc) {
            calculateManager.accept(mq);
        }
        List<MarketQuotation> dataList = calculateManager.getDataList();
        dataList.stream().forEach(item ->{
//            System.out.println(item.getBoll());
            Integer td = item.getTd();
            if(td == 9 || td == 13 || td == -9 || td == -13){
                System.out.println(td+",,,"+dataList.indexOf(item));
            }
        });
    }

    /**
     * @param indicatorSetScale  指标精度的小数位
     * @return
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
        indicatorCalculatorList.add(TD.buildCalculator(13, 4,MarketQuotation::setTd,MarketQuotation::getTd));
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

@Data
class Macdss {
    private BigDecimal fast;
    private BigDecimal low;

    private BigDecimal ww;

    private Integer index;
}
