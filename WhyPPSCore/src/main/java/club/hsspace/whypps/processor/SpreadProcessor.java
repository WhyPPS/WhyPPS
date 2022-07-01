package club.hsspace.whypps.processor;

import club.hsspace.whypps.handle.DataStream;
import club.hsspace.whypps.model.RadioMsg;
import club.hsspace.whypps.model.senior.Code;
import club.hsspace.whypps.model.senior.Radio;
import club.hsspace.whypps.model.senior.SwapR;
import club.hsspace.whypps.model.senior.SwapS;
import com.alibaba.fastjson.JSONArray;

public interface SpreadProcessor {

    void listenerRadioMsg(DataStream dataStream, Radio radio, RadioMsg radioMsg);

    //交换协议 1.  监听消息构并构造转发请求体，返回null则不再转发
    default SwapS listenerSwapMsg(DataStream dataStream, SwapS swapS) {
        if (swapS.times == swapS.range)
            return null;

        swapS.times++;
        return swapS;
    }


    //交换协议 2.  多方请求收到回应后调用，返回汇总后的数据(决定抛弃或保留)
    default boolean summarySwapMsg(JSONArray jsonArray, SwapR swapR) {
        if(swapR.code.equals(Code.OK)) {
            jsonArray.addAll(swapR.data);
            return true;
        }
        return false;
    }

    //交换协议 3.  收集自身信息，汇总最后返回请求方
    SwapR returnSwapMsg(DataStream dataStream, JSONArray data, SwapS swapS, int involve);

}
