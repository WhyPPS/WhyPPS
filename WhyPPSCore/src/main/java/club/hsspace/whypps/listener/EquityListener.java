package club.hsspace.whypps.listener;

import club.hsspace.whypps.action.Container;
import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.action.nongeneral.MsgListener;
import club.hsspace.whypps.handle.DataStream;
import club.hsspace.whypps.handle.EquityHandle;
import club.hsspace.whypps.handle.LongMsgStream;
import club.hsspace.whypps.manage.LongMsgManage;
import club.hsspace.whypps.model.DataLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: EquityListener
 * @CreateTime: 2022/4/26
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@Container
@MsgListener({DataLabel.DATA_R, DataLabel.BIN_R, DataLabel.HEART_R})
public class EquityListener implements LinkListener{

    private static final Logger logger = LoggerFactory.getLogger(EquityListener.class);

    @Injection
    private LongMsgManage longMsgManage;

    @Override
    public boolean listener(DataStream dataStream, DataLink dataLink) {

        EquityHandle equityHandle = dataStream.getEquityHandle();
        equityHandle.receiveData(dataLink);
        return true;
    }

}
