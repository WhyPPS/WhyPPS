package club.hsspace.whypps.processor.impl;

import club.hsspace.whypps.handle.DataStream;
import club.hsspace.whypps.model.RadioMsg;
import club.hsspace.whypps.model.senior.Code;
import club.hsspace.whypps.model.senior.Radio;
import club.hsspace.whypps.model.senior.SwapR;
import club.hsspace.whypps.model.senior.SwapS;
import club.hsspace.whypps.processor.SpreadProcessor;
import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: SpreadProcessorImpl
 * @CreateTime: 2022/6/12
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class SpreadProcessorImpl implements SpreadProcessor {

    private static final Logger logger = LoggerFactory.getLogger(SpreadProcessorImpl.class);

    @Override
    public void listenerRadioMsg(DataStream dataStream, Radio radio, RadioMsg radioMsg) {
        logger.debug("接收到广播消息{} - {}", radioMsg.api, radioMsg.data);
    }

    @Override
    public SwapR returnSwapMsg(DataStream dataStream, JSONArray data, SwapS swapS, int involve) {
        data.add(dataStream.getTcpHandle().getSocket().getLocalAddress().toString());
        logger.debug("接收到Swap消息{}，消息体{}", swapS.api, data);
        return SwapR.of(swapS.requestId, Code.OK, data, involve);
    }

}
