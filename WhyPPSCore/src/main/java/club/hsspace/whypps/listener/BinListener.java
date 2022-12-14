package club.hsspace.whypps.listener;

import club.hsspace.whypps.action.Container;
import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.action.nongeneral.MsgListener;
import club.hsspace.whypps.handle.DataStream;
import club.hsspace.whypps.model.DataLink;
import club.hsspace.whypps.model.senior.BinR;
import club.hsspace.whypps.model.senior.BinS;
import club.hsspace.whypps.model.senior.DataR;
import club.hsspace.whypps.model.senior.DataS;
import club.hsspace.whypps.processor.EquityProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: DataListener
 * @CreateTime: 2022/4/25
 * @Comment: 双向数据通信监听器
 *
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@Container
@MsgListener(DataLabel.BIN_S)
public class BinListener implements LinkListener{

    private static final Logger logger = LoggerFactory.getLogger(BinListener.class);

    @Injection
    private EquityProcessor equityProcessor;

    @Override
    public boolean listener(DataStream dataStream, DataLink dataLink) {
        DataLink result = equityProcessor.listenerBinMsg(dataStream, (BinS) dataLink.getData(), dataLink.getExtraData());

        dataStream.sendData(result);
        return true;
    }


}
