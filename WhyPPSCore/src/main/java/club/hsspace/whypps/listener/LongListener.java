package club.hsspace.whypps.listener;

import club.hsspace.whypps.action.Container;
import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.action.nongeneral.MsgListener;
import club.hsspace.whypps.handle.DataStream;
import club.hsspace.whypps.handle.LongMsgStream;
import club.hsspace.whypps.manage.LongMsgManage;
import club.hsspace.whypps.model.DataLink;
import club.hsspace.whypps.model.senior.DataR;
import club.hsspace.whypps.model.senior.DataS;
import club.hsspace.whypps.model.senior.LongR;
import club.hsspace.whypps.model.senior.LongS;
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
@MsgListener(DataLabel.LONG_S)
public class LongListener implements LinkListener{

    private static final Logger logger = LoggerFactory.getLogger(LongListener.class);

    @Injection
    private LongMsgManage longMsgManage;

    @Injection
    private EquityProcessor equityProcessor;

    @Override
    public boolean listener(DataStream dataStream, DataLink dataLink) {
        LongR longR = equityProcessor.listenerLongMsg(dataStream, (LongS) dataLink.getData());

        LongMsgStream lms = new LongMsgStream();
        //TODO: 判断重复
        longMsgManage.putLongMsgStream(dataLink.getData().requestId, lms);

        dataStream.sendData(new DataLink(DataLabel.LONG_R, longR, null));
        return true;
    }


}
