package club.hsspace.whypps.processor;

import club.hsspace.whypps.handle.DataStream;
import club.hsspace.whypps.model.DataLink;
import club.hsspace.whypps.model.senior.*;

//对等通讯协议处理机, 消息分配，上层监听器注入。
public interface EquityProcessor {

    DataR listenerDataMsg(DataStream dataStream, DataS dataS);

    DataLink listenerBinMsg(DataStream dataStream, BinS binS, byte[] extraData);

    HeartR listenerHeartMsg(DataStream dataStream, HeartS heartS);

    LongR listenerLongMsg(DataStream dataStream, LongS longS);

}
