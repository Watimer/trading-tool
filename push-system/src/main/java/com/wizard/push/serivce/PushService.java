package com.wizard.push.serivce;

import com.wizard.common.enums.ExchangeEnum;
import com.wizard.common.enums.PushEnum;

/**
 * @author 巫师
 * @date 2024-05-08
 * @desc
 */
public interface PushService {

	Boolean pushFeiShu(Long logId, String symbol, String dateTime, String title, ExchangeEnum exchangeEnum, PushEnum pushEnum);
}
