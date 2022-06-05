package club.hsspace.whypps.framework.app;

import club.hsspace.whypps.handle.DataStream;
import club.hsspace.whypps.model.DataLink;
import club.hsspace.whypps.model.senior.*;
import club.hsspace.whypps.processor.impl.EquityProcessorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: EquityDistribution
 * @CreateTime: 2022/6/5
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class EquityDistribution extends EquityProcessorImpl {

    private static final Logger logger = LoggerFactory.getLogger(EquityDistribution.class);

    @Override
    public DataR listenerDataMsg(DataStream dataStream, DataS dataS) {
        return super.listenerDataMsg(dataStream, dataS);
    }

    @Override
    public DataLink listenerBinMsg(DataStream dataStream, BinS binS, byte[] extraData) {
        return super.listenerBinMsg(dataStream, binS, extraData);
    }

    @Override
    public HeartR listenerHeartMsg(DataStream dataStream, HeartS heartS) {
        return super.listenerHeartMsg(dataStream, heartS);
    }

    @Override
    public LongR listenerLongMsg(DataStream dataStream, LongS longS) {
        return super.listenerLongMsg(dataStream, longS);
    }

}
