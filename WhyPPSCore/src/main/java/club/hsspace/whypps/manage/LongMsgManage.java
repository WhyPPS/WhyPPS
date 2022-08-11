package club.hsspace.whypps.manage;

import club.hsspace.whypps.action.Container;
import club.hsspace.whypps.action.Init;
import club.hsspace.whypps.handle.LongMsgStream;
import club.hsspace.whypps.model.ContainerClosable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: LongMsgManage
 * @CreateTime: 2022/5/2
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@Container(sort = -100)
public class LongMsgManage implements ContainerClosable {

    private static final Logger logger = LoggerFactory.getLogger(LongMsgManage.class);

    private LongMsgManage() {

    }

    private Map<String, LongMsgStream> longMsgStreamMap;

    @Init
    private void initMap() {
        longMsgStreamMap = new HashMap<>();
    }

    public boolean putLongMsgStream(String id, LongMsgStream msgStream) {
        if(!longMsgStreamMap.containsKey(id)){
            longMsgStreamMap.put(id, msgStream);
            return true;
        }
        return false;
    }

    public LongMsgStream getStream(String id) {
        return longMsgStreamMap.get(id);
    }

    public void receive(String id, byte[] data) throws IOException {
        LongMsgStream lms = longMsgStreamMap.get(id);
        lms.receiveData(data);
    }

    @Override
    public void close() throws IOException {
        for (LongMsgStream value : longMsgStreamMap.values()) {
            value.close();
        }
        logger.info("已经关闭所有长连接通道");
    }
}
