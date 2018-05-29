package com.zbest.queue.disruptor.demo1;

import com.lmax.disruptor.EventTranslator;

import java.util.Random;

/**
 * Created by zhangbin on 2018/5/18.
 */
public class TradeTransactionEventTranslator implements EventTranslator<TradeTransaction> {

    private Random random=new Random();

    @Override
    public void translateTo(TradeTransaction tradeTransaction, long l) {
        this.generateTradeTransaction(tradeTransaction);
    }

    private TradeTransaction generateTradeTransaction(TradeTransaction trade){
        trade.setPrice(random.nextDouble()*9999);
        return trade;
    }

}
