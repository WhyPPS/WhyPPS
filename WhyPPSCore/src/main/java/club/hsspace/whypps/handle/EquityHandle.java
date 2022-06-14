package club.hsspace.whypps.handle;

import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.listener.DataLabel;
import club.hsspace.whypps.manage.LongMsgManage;
import club.hsspace.whypps.model.Callback;
import club.hsspace.whypps.model.CallbackDouble;
import club.hsspace.whypps.model.DataLink;
import club.hsspace.whypps.model.ObjectPair;
import club.hsspace.whypps.model.senior.*;
import club.hsspace.whypps.util.MD5Tools;
import club.hsspace.whypps.util.NumberTools;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @ClassName: EquityHandle
 * @CreateTime: 2022/4/26
 * @Comment: 对等通讯协议处理方法 数据发送 接收帧匹配
 *
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class EquityHandle {

    private static final Logger logger = LoggerFactory.getLogger(EquityHandle.class);

    private DataStream dataStream;

    @Injection
    private LongMsgManage longMsgManage;

    public EquityHandle(DataStream dataStream) {
        this.dataStream = dataStream;
    }

    private static Random random = new Random();

    public static String generateRequestId() {
        byte[] bytes = new byte[4];
        random.nextBytes(bytes);
        return NumberTools.bytes2HexString(bytes);
    }

    //TODO: 增加超时自动清理功能
    private Map<DataLabel, Map<String, Callback<?>>> receiveWaitPool = Map.of(DataLabel.DATA_R, new HashMap<>(),
            DataLabel.BIN_R, new HashMap<>(),
            DataLabel.HEART_R, new HashMap<>(),
            DataLabel.LONG_R, new HashMap<>());

    public void receiveData(DataLink<?> dataLink) {
        Callback callBack = receiveWaitPool.get(dataLink.getDataLabel())
                .get(dataLink.getData().requestId);
        if(callBack != null) {
            switch (dataLink.getDataLabel()){
                case DATA_R -> callBack.run(dataLink.getData());
                case BIN_R -> callBack.run(dataLink);
                case HEART_R -> {
                    HeartR data = dataLink.getData(HeartR.class);
                    callBack.run((int)(System.currentTimeMillis() - data.time + data.delay));
                }
                case LONG_R -> {
                    LongR data = dataLink.getData(LongR.class);
                    callBack.run(new ObjectPair<>(data, longMsgManage.getStream(data.requestId)));
                }
            }
        }
    }

    //数据通信方法 只提供异步方法
    public void sendData(Callback<DataR> run, String api, JSONObject data) {
        String requestId = generateRequestId();
        DataS dataS = DataS.of(requestId, api, data);

        DataLink dataLink = new DataLink(DataLabel.DATA_S, dataS, null);
        receiveWaitPool.get(DataLabel.DATA_R).put(requestId, run);
        dataStream.sendData(dataLink);
    }

    //TODO: 二进制通信方法，提供InputStream输入流调用
    public void sendBin(Callback<DataLink<BinR>> run, String api, JSONObject data, byte[] bin) {
        String requestId = generateRequestId();
        String md5 = null;
        if(bin != null)
            md5 = NumberTools.bytes2HexString(MD5Tools.md5(bin));
        BinS binS = BinS.of(requestId, api, data, bin != null, md5);

        DataLink dataLink = new DataLink<>(DataLabel.BIN_S, binS, bin);
        receiveWaitPool.get(DataLabel.BIN_R).put(requestId, run);
        dataStream.sendData(dataLink);
    }

    public void sendHeart(Callback<Integer> run) {
        String requestId = generateRequestId();
        HeartS heartS = HeartS.of(requestId);

        DataLink dataLink = new DataLink<>(DataLabel.HEART_S, heartS, null);
        receiveWaitPool.get(DataLabel.HEART_R).put(requestId, run);
        dataStream.sendData(dataLink);
    }

    public void sendLong(Callback<ObjectPair<LongR, LongMsgStream>> run, String api, JSONObject data, int timeout) {
        String requestId = generateRequestId();
        LongS longS = LongS.of(requestId, api, data, timeout);

        DataLink dataLink = new DataLink(DataLabel.LONG_S, longS, null);
        receiveWaitPool.get(DataLabel.LONG_R).put(requestId, run);
        dataStream.sendData(dataLink);
    }



}
