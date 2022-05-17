package club.hsspace.whypps.listener;

import club.hsspace.whypps.action.Container;
import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.action.nongeneral.MsgListener;
import club.hsspace.whypps.handle.DataStream;
import club.hsspace.whypps.handle.LongMsgStream;
import club.hsspace.whypps.manage.LongMsgManage;
import club.hsspace.whypps.model.DataLink;
import club.hsspace.whypps.model.senior.LongR;
import club.hsspace.whypps.model.senior.LongS;
import club.hsspace.whypps.processor.EquityProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @ClassName: DataListener
 * @CreateTime: 2022/4/25
 * @Comment: 双向数据通信监听器
 *
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@Container
@MsgListener(DataLabel.LONG_M)
public class LongMListener implements LinkListener{

    private static final Logger logger = LoggerFactory.getLogger(LongMListener.class);

    @Injection
    private LongMsgManage longMsgManage;

    @Override
    public boolean listener(DataStream dataStream, DataLink dataLink) {
        //TODO: 数据JSON格式count校验
        try {
            longMsgManage.receive(dataLink.getData().requestId, dataLink.getExtraData());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }


}
