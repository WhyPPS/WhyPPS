package club.hsspace.whypps.manage;

import club.hsspace.whypps.action.Container;
import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.handle.DataStream;
import club.hsspace.whypps.handle.TcpHandle;
import club.hsspace.whypps.model.Certificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: FrameManage
 * @CreateTime: 2022/3/15
 * @Comment: 数据帧管理器
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@Container(sort = -30)
public class FrameManage {

    private static final Logger logger = LoggerFactory.getLogger(FrameManage.class);

    @Injection
    private Authentication authentication;

    @Injection
    private ContainerManage containerManage;

    //源标记-数据流 Map
    private Map<String, DataStream> dataStreamMap = new HashMap<>();

    private FrameManage() {

    }

    public DataStream newLink(Certificate certificate, TcpHandle tcpHandle, byte[] AESKey){
        authentication.putCertificate(certificate);
        DataStream dataStream = new DataStream(certificate, tcpHandle, AESKey);
        dataStreamMap.put(certificate.getSign(), dataStream);
        try {
            containerManage.injection(dataStream);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return dataStream;
    }

    public DataStream getDataStream(String sign) {
        return dataStreamMap.get(sign);
    }

    public Collection<DataStream> getAllDataStream() {
        return dataStreamMap.values();
    }

}
