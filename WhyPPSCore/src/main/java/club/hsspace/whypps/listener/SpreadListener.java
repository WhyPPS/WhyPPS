package club.hsspace.whypps.listener;

import club.hsspace.whypps.action.Container;
import club.hsspace.whypps.action.nongeneral.MsgListener;
import club.hsspace.whypps.handle.DataStream;
import club.hsspace.whypps.handle.SpreadHandle;
import club.hsspace.whypps.model.DataLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: RadioRListener
 * @CreateTime: 2022/6/13
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@Container
@MsgListener({DataLabel.RADIO_R, DataLabel.SWAP_R})
public class SpreadListener implements LinkListener{

    private static final Logger logger = LoggerFactory.getLogger(SpreadListener.class);

    @Override
    public boolean listener(DataStream dataStream, DataLink dataLink) {
        SpreadHandle spreadHandle = dataStream.getSpreadHandle();
        spreadHandle.receiveDataLink(dataLink);
        return true;
    }
}
