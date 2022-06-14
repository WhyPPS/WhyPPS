package club.hsspace.whypps.handle;

import club.hsspace.whypps.action.Container;
import club.hsspace.whypps.action.Init;
import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.manage.Authentication;
import club.hsspace.whypps.manage.ContainerManage;
import club.hsspace.whypps.model.Certificate;
import club.hsspace.whypps.model.DataFrame;
import club.hsspace.whypps.model.DataLink;
import club.hsspace.whypps.model.ObjectPair;
import club.hsspace.whypps.processor.FrameProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidKeyException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @ClassName: DataStream
 * @CreateTime: 2022/3/15
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class DataStream {

    private static final Logger logger = LoggerFactory.getLogger(DataStream.class);

    //源节点证书
    private Certificate srcCertificate;

    //连接方向
    private TcpHandle tcpHandle;

    private Map<Short, Deque<DataFrame.EncryptData>> dataCache = new HashMap<>();

    //发送数据帧 数据帧处理器
    private EquityHandle equityHandle;

    //主从通信协议 消息处理器
    private SpreadHandle spreadHandle;

    @Injection
    private Authentication authentication;

    //AES通讯密钥
    private byte[] key;

    public DataStream(Certificate srcCertificate, TcpHandle tcpHandle, byte[] key) {
        this.srcCertificate = srcCertificate;
        this.tcpHandle = tcpHandle;
        this.key = key;
    }

    public EquityHandle getEquityHandle() {
        return equityHandle;
    }

    public SpreadHandle getSpreadHandle() {
        return spreadHandle;
    }

    @Init
    public void initEquityHandle(ContainerManage containerManage) throws InvocationTargetException {
         equityHandle = new EquityHandle(this);
         containerManage.injection(equityHandle);
    }

    @Init
    public void initSpreadHandle(ContainerManage containerManage) throws InvocationTargetException {
        spreadHandle = new SpreadHandle(this);
        containerManage.injection(spreadHandle);
    }

    @Injection
    private FrameProcessor frameProcessor;

    public TcpHandle getTcpHandle() {
        return tcpHandle;
    }

    public boolean alive() {
        //TODO:
        return true;
    }

    public Certificate getSrcCertificate() {
        return srcCertificate;
    }

    public void sendData(byte[] data) {
        DataFrame.packFrame2Stream(key, authentication.getLocalCertificate().getSignBytes(), srcCertificate.getSignBytes(), DataFrame.DataSign.data, data)
                .map(n -> n.toBytes())
                .forEach(n -> {
                    try {
                        tcpHandle.send(n);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    public synchronized void putDataFrame(DataFrame dataFrame) {
        DataFrame.EncryptData decrypt = null;
        try {
            decrypt = dataFrame.decrypt(key);
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
        Deque<DataFrame.EncryptData> dataDeque = dataCache.get(decrypt.sign());
        if (dataDeque == null) {
            if (decrypt.end()) {
                frameProcessor.processorData(this, dataFrame.getDataSign(), decrypt.data());
            } else {
                dataDeque = new LinkedList<>();
                dataDeque.add(decrypt);
                dataCache.put(decrypt.sign(), dataDeque);
            }
        } else {
            if (decrypt.end()) {
                dataCache.remove(decrypt.sign());
                //TODO: 检验包
                ObjectPair<DataFrame.DataSign, byte[]> dataSignObjectPair = DataFrame.Frames2data(dataDeque, dataFrame.getDataSign());
                frameProcessor.processorData(this, dataSignObjectPair.key(), dataSignObjectPair.value());
            } else {
                //TODO: 检验包连续性(由于协议特性，包Count计数区字段必须连续)
                dataDeque.add(decrypt);
            }
        }
    }

    public void sendData(DataLink dataLink) {
        sendData(dataLink.toBytes());
    }
}
