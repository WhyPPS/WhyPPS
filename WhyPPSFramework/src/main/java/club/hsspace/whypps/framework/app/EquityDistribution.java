package club.hsspace.whypps.framework.app;

import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.framework.app.annotation.RequestEnum;
import club.hsspace.whypps.handle.DataStream;
import club.hsspace.whypps.handle.LongMsgStream;
import club.hsspace.whypps.listener.DataLabel;
import club.hsspace.whypps.manage.LongMsgManage;
import club.hsspace.whypps.model.DataLink;
import club.hsspace.whypps.model.senior.*;
import club.hsspace.whypps.processor.impl.EquityProcessorImpl;
import club.hsspace.whypps.util.MD5Tools;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Map;

/**
 * @ClassName: EquityDistribution
 * @CreateTime: 2022/6/5
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class EquityDistribution extends EquityProcessorImpl {

    private static final Logger logger = LoggerFactory.getLogger(EquityDistribution.class);

    @Injection
    private MethodController methodController;

    @Injection
    private LongMsgManage longMsgManage;

    @Injection
    private InterceptorManage interceptorManage;

    @Override
    public DataR listenerDataMsg(DataStream dataStream, DataS dataS) {
        MethodController.MethodAndObject mo = methodController.getDataMethod(dataS.api);
        if (mo == null)
            return DataR.of(dataS.requestId, Code.NOT_FOUND, null);

        JSONObject data = dataS.data;
        if (data == null) {
            data = new JSONObject();
        }

        Object methodReturn;
        try {
            methodReturn = interceptorManage.executeRequest(RequestEnum.DATA, dataS.api, dataStream,data, mo, Map.of(DataS.class, dataS));
        } catch (Exception e) {
            e.printStackTrace();
            return DataR.of(dataS.requestId, Code.SERVER_ERROR, null);
        }

        if (methodReturn == null)
            return DataR.of(dataS.requestId, Code.REQUEST_FAIL, null);

        Class<?> returnClass = methodReturn.getClass();
        if (methodReturn instanceof Code code)
            return DataR.of(dataS.requestId, code, null);
        else if (methodReturn instanceof DataR dataR)
            return dataR;
        else if (methodReturn instanceof JSONObject jsonObject)
            return DataR.of(dataS.requestId, Code.OK, jsonObject);
        else if (methodReturn instanceof JSONArray jsonArray)
            return DataR.of(dataS.requestId, Code.OK, new JSONObject(Map.of("value", jsonArray)));
        else if (methodReturn instanceof Boolean bool)
            return DataR.of(dataS.requestId, bool ? Code.OK : Code.REQUEST_FAIL, null);
        else if (methodReturn instanceof Number || returnClass == String.class)
            return DataR.of(dataS.requestId, Code.OK, new JSONObject(Map.of("value", methodReturn)));

        return DataR.of(dataS.requestId, Code.OK, (JSONObject) JSONObject.toJSON(methodReturn));
    }

    @Override
    public DataLink listenerBinMsg(DataStream dataStream, BinS binS, byte[] extraData) {

        MethodController.MethodAndObject mo = methodController.getBinMethod(binS.api);
        if (mo == null)
            return DataLink.of(DataLabel.BIN_R, BinR.of(binS.requestId, Code.NOT_FOUND, null, false, null), null);

        JSONObject data = binS.data;
        if (data == null) {
            data = new JSONObject();
        }

        Object methodReturn;
        try {
            methodReturn = interceptorManage.executeRequest(RequestEnum.BIN, binS.api, dataStream,data, mo, Map.of(BinS.class, binS, byte[].class, extraData));
        } catch (Exception e) {
            return DataLink.of(DataLabel.BIN_R, BinR.of(binS.requestId, Code.SERVER_ERROR, null, false, null), null);
        }

        if (methodReturn == null)
            return emptyBinRDataLink(BinR.of(binS.requestId, Code.OK));

        Class<?> returnClass = methodReturn.getClass();
        if (methodReturn instanceof Code code)
            return emptyBinRDataLink(BinR.of(binS.requestId, code));
        else if (methodReturn instanceof BinR binR)
            return emptyBinRDataLink(binR);
        else if (methodReturn instanceof JSONObject jsonObject)
            return emptyBinRDataLink(BinR.of(binS.requestId, Code.OK, jsonObject));
        else if (methodReturn instanceof JSONArray jsonArray)
            return emptyBinRDataLink(BinR.of(binS.requestId, Code.OK, new JSONObject(Map.of("value", jsonArray))));
        else if (methodReturn instanceof Boolean bool)
            return emptyBinRDataLink(BinR.of(binS.requestId, bool ? Code.OK : Code.REQUEST_FAIL));
        else if (methodReturn instanceof Number || returnClass == String.class)
            return emptyBinRDataLink(BinR.of(binS.requestId, Code.OK, new JSONObject(Map.of("value", methodReturn))));
        else if (methodReturn instanceof byte[] bytes)
            return DataLink.of(DataLabel.BIN_R, BinR.of(binS.requestId, Code.OK, null, true, MD5Tools.md5String(bytes)), bytes);

        return emptyBinRDataLink(BinR.of(binS.requestId, Code.OK, (JSONObject) JSONObject.toJSON(methodReturn)));
    }

    private static DataLink emptyBinRDataLink(BinR binR) {
        return DataLink.of(DataLabel.BIN_R, binR, null);
    }

    @Override
    public HeartR listenerHeartMsg(DataStream dataStream, HeartS heartS) {
        return super.listenerHeartMsg(dataStream, heartS);
    }

    @Override
    public LongR listenerLongMsg(DataStream dataStream, LongS longS) {

        MethodController.MethodAndObject mo = methodController.getLongMethod(longS.api);
        if (mo == null)
            return LongR.of(longS.requestId, Code.NOT_FOUND, null);

        JSONObject data = longS.data;
        if (data == null) {
            data = new JSONObject();
        }

        LongMsgStream longMsgStream = longMsgManage.getStream(longS.requestId);

        Object methodReturn;
        try {
            methodReturn = interceptorManage.executeRequest(RequestEnum.LONG, longS.api, dataStream,data, mo, Map.of(LongS.class, longS,
                    LongMsgStream.class, longMsgStream,
                    InputStream.class, longMsgStream.getInputStream()));
        } catch (Exception e) {
            return LongR.of(longS.requestId, Code.SERVER_ERROR, null);
        }

        if (methodReturn == null)
            return LongR.of(longS.requestId, Code.REQUEST_FAIL, null);

        Class<?> returnClass = methodReturn.getClass();
        if (methodReturn instanceof Code code)
            return LongR.of(longS.requestId, code, null);
        else if (methodReturn instanceof LongR longR)
            return longR;
        else if (methodReturn instanceof JSONObject jsonObject)
            return LongR.of(longS.requestId, Code.OK, jsonObject);
        else if (methodReturn instanceof JSONArray jsonArray)
            return LongR.of(longS.requestId, Code.OK, new JSONObject(Map.of("value", jsonArray)));
        else if (methodReturn instanceof Boolean bool)
            return LongR.of(longS.requestId, bool ? Code.OK : Code.REQUEST_FAIL, null);
        else if (methodReturn instanceof Number || returnClass == String.class)
            return LongR.of(longS.requestId, Code.OK, new JSONObject(Map.of("value", methodReturn)));

        return LongR.of(longS.requestId, Code.OK, (JSONObject) JSONObject.toJSON(methodReturn));
    }

}
