package club.hsspace.whypps.processor.impl;

import club.hsspace.whypps.handle.DataStream;
import club.hsspace.whypps.listener.DataLabel;
import club.hsspace.whypps.model.DataLink;
import club.hsspace.whypps.model.senior.*;
import club.hsspace.whypps.processor.EquityProcessor;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: EquityProcessorImpl
 * @CreateTime: 2022/4/27
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class EquityProcessorImpl implements EquityProcessor {

    private static final Logger logger = LoggerFactory.getLogger(EquityProcessorImpl.class);

    @Override
    public DataR listenerDataMsg(DataStream dataStream, DataS dataS) {
        return DataR.of(dataS.requestId, Code.OK, null);
    }

    @Override
    public DataLink listenerBinMsg(DataStream dataStream, BinS binS, byte[] extraData) {
        DataLink dataLink = new DataLink(DataLabel.BIN_R, BinR.of(binS.requestId, Code.OK, null, false, null), null);
        return dataLink;
    }

    @Override
    public HeartR listenerHeartMsg(DataStream dataStream, HeartS heartS) {
        long time = System.currentTimeMillis();
        return HeartR.of(heartS.requestId, time, (int) (time - heartS.time));
    }

    @Override
    public LongR listenerLongMsg(DataStream dataStream, LongS longS) {
        return LongR.of(longS.requestId, Code.OK, null);
    }

}
