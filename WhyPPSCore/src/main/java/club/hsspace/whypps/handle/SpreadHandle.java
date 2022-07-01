package club.hsspace.whypps.handle;

import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.listener.DataLabel;
import club.hsspace.whypps.manage.FrameManage;
import club.hsspace.whypps.manage.LocalHost;
import club.hsspace.whypps.model.Callback;
import club.hsspace.whypps.model.DataLink;
import club.hsspace.whypps.model.RadioMsg;
import club.hsspace.whypps.model.senior.Radio;
import club.hsspace.whypps.model.senior.RadioR;
import club.hsspace.whypps.model.senior.SwapR;
import club.hsspace.whypps.model.senior.SwapS;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @ClassName: SpreadHandle
 * @CreateTime: 2022/6/12
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class SpreadHandle {

    private static final Logger logger = LoggerFactory.getLogger(SpreadHandle.class);

    private DataStream dataStream;

    @Injection
    private FrameManage frameManage;

    public SpreadHandle(DataStream dataStream) {
        this.dataStream = dataStream;
    }

    //注意，调用方DataStream为权威服务器授权端DS流，一旦授权完成，会过滤证书并发送给所有过滤后的DS流。
    public boolean sendRadio(String api, JSONObject data, boolean blackList, String[] list, int range) {
        RadioMsg radioMsg = RadioMsg.of(api, data, System.currentTimeMillis(), EquityHandle.generateRequestId(), range, blackList, list);
        DataLink<RadioR> radioRDataLink;
        try {
            radioRDataLink = encryptMsg(radioMsg);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (radioRDataLink != null) {
            RadioR radioR = radioRDataLink.getData();
            for (DataStream stream : frameManage.getAllDataStream()) {
                //TODO: 增加证书过滤(blackList)
                stream.sendData(new DataLink(DataLabel.RADIO, Radio.of(radioR.requestId, 1), radioRDataLink.getExtraData()));
            }
            return true;
        }
        return false;
    }

    public void receiveDataLink(DataLink dataLink) {
        if (dataLink.getDataLabel() == DataLabel.RADIO_R) {
            RadioR data = (RadioR) dataLink.getData();
            AtomicReference<DataLink<RadioR>> dataLinkAtomicReference = callbackMap.get(data.requestId);
            dataLinkAtomicReference.set(dataLink);
            synchronized (dataLinkAtomicReference) {
                dataLinkAtomicReference.notify();
            }
        } else if (dataLink.getDataLabel() == DataLabel.SWAP_R) {
            SwapR data = (SwapR) dataLink.getData();
            Callback<SwapR> run;
            AtomicReference<SwapR> swapRAtomicReference = synSwapWaitPool.get(data.requestId);
            if (swapRAtomicReference != null) {
                synchronized (swapRAtomicReference) {
                    swapRAtomicReference.set(data);
                    swapRAtomicReference.notify();
                }
            } else if ((run = swapWaitPool.get(data.requestId)) != null) {
                run.run(data);
            }
        }
        return;
    }

    //swap异步方法等待池
    private Map<String, Callback<SwapR>> swapWaitPool = new HashMap<>();

    /**
     * 发送swap请求，异步方法，推荐使用此方法！
     */
    public boolean sendSwap(Callback<SwapR> run, String api, JSONObject data, int times, int range, boolean wait, int valid) {
        //TODO: 实现wait和valid
        String requestId = EquityHandle.generateRequestId();
        SwapS swapS = SwapS.of(api, data, requestId, times, range, wait, valid);
        DataLink<SwapS> dataLink = new DataLink<>(DataLabel.SWAP_S, swapS, null);
        swapWaitPool.put(requestId, run);
        dataStream.sendData(dataLink);
        return true;
    }

    //swap同步方法等待池
    private Map<String, AtomicReference<SwapR>> synSwapWaitPool = new HashMap<>();

    //发送swap请求 同步方法 不建议使用此方法，建议使用异步方法
    public SwapR sendSwap(SwapS swapS) {
        //TODO: 实现wait和valid
        String requestId = swapS.requestId;
        DataLink<SwapS> dataLink = new DataLink<>(DataLabel.SWAP_S, swapS, null);
        AtomicReference<SwapR> swapRAtomicReference = new AtomicReference<>();
        synSwapWaitPool.put(requestId, swapRAtomicReference);
        synchronized (swapRAtomicReference) {
            dataStream.sendData(dataLink);
            try {
                swapRAtomicReference.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return swapRAtomicReference.get();
    }

    private Map<String, AtomicReference<DataLink<RadioR>>> callbackMap = new HashMap<>();

    //同步方法！
    public DataLink<RadioR> encryptMsg(RadioMsg radioMsg) throws InterruptedException {
        DataLink dataLink = new DataLink(DataLabel.RADIO_S, radioMsg, null);

        AtomicReference<DataLink<RadioR>> dataLinkAtomicReference = new AtomicReference<>();
        callbackMap.put(radioMsg.requestId, dataLinkAtomicReference);

        synchronized (dataLinkAtomicReference) {
            dataStream.sendData(dataLink);
            dataLinkAtomicReference.wait(5000);
            callbackMap.remove(radioMsg.requestId);
        }
        return dataLinkAtomicReference.get();
    }

}
