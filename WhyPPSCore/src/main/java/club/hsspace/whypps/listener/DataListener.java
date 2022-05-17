package club.hsspace.whypps.listener;

import club.hsspace.whypps.action.Container;
import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.action.nongeneral.MsgListener;
import club.hsspace.whypps.handle.DataStream;
import club.hsspace.whypps.handle.EquityHandle;
import club.hsspace.whypps.model.DataLink;
import club.hsspace.whypps.model.senior.DataR;
import club.hsspace.whypps.model.senior.DataS;
import club.hsspace.whypps.processor.EquityProcessor;
import com.alibaba.fastjson.JSONObject;
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
@MsgListener(DataLabel.DATA_S)
public class DataListener implements LinkListener{

    private static final Logger logger = LoggerFactory.getLogger(DataListener.class);

    @Injection
    private EquityProcessor equityProcessor;

    @Override
    public boolean listener(DataStream dataStream, DataLink dataLink) {
        DataR dataR = equityProcessor.listenerDataMsg(dataStream, (DataS) dataLink.getData());

        dataStream.sendData(new DataLink(DataLabel.DATA_R, dataR, null));
        return true;
    }


}
