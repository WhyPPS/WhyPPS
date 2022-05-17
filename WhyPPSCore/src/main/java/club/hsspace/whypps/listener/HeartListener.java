package club.hsspace.whypps.listener;

import club.hsspace.whypps.action.Container;
import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.action.nongeneral.MsgListener;
import club.hsspace.whypps.handle.DataStream;
import club.hsspace.whypps.model.DataLink;
import club.hsspace.whypps.model.senior.BinS;
import club.hsspace.whypps.model.senior.HeartR;
import club.hsspace.whypps.model.senior.HeartS;
import club.hsspace.whypps.processor.EquityProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: DataListener
 * @CreateTime: 2022/4/25
 * @Comment: 双向数据通信监听器
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@Container
@MsgListener(DataLabel.HEART_S)
public class HeartListener implements LinkListener {

    private static final Logger logger = LoggerFactory.getLogger(HeartListener.class);

    @Injection
    private EquityProcessor equityProcessor;

    @Override
    public boolean listener(DataStream dataStream, DataLink dataLink) {
        HeartS heartS = (HeartS) dataLink.getData();
        HeartR heartR = equityProcessor.listenerHeartMsg(dataStream, heartS);

        dataStream.sendData(new DataLink(DataLabel.HEART_R, heartR, null));
        return true;
    }

}
