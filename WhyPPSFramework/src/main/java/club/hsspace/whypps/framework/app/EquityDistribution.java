package club.hsspace.whypps.framework.app;

import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.framework.app.annotation.DataParam;
import club.hsspace.whypps.handle.DataStream;
import club.hsspace.whypps.handle.LongMsgStream;
import club.hsspace.whypps.listener.DataLabel;
import club.hsspace.whypps.manage.ContainerManage;
import club.hsspace.whypps.manage.LongMsgManage;
import club.hsspace.whypps.model.DataLink;
import club.hsspace.whypps.model.senior.*;
import club.hsspace.whypps.processor.impl.EquityProcessorImpl;
import club.hsspace.whypps.util.MD5Tools;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @ClassName: EquityDistribution
 * @CreateTime: 2022/6/5
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class EquityDistribution extends EquityProcessorImpl {

    private static final Logger logger = LoggerFactory.getLogger(EquityDistribution.class);

    private MethodController methodController;

    @Injection
    private ContainerManage containerManage;

    @Injection
    private LongMsgManage longMsgManage;

    private MountManage mountManage;

    public void injection(MethodController methodController, MountManage mountManage) {
        this.mountManage = mountManage;
        this.methodController = methodController;
    }

    @Override
    public DataR listenerDataMsg(DataStream dataStream, DataS dataS) {
        MethodController.MethodAndObject mo = methodController.getDataMethod(dataS.api);
        if (mo == null)
            return DataR.of(dataS.requestId, Code.NOT_FOUND, null);

        Method method = mo.method();
        Object object = mo.object();

        JSONObject data = dataS.data;
        if (data == null) {
            data = new JSONObject();
        }

        Parameter[] parameters = method.getParameters();
        Object[] param = fillObject(dataStream, data, parameters);
        fillCustomObject(param, parameters, Map.of(DataS.class, dataS));

        Object methodReturn;

        try {
            methodReturn = method.invoke(object, param);
        } catch (Exception e) {
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

        Method method = mo.method();
        Object object = mo.object();

        JSONObject data = binS.data;
        if (data == null) {
            data = new JSONObject();
        }

        Parameter[] parameters = method.getParameters();
        Object[] param = fillObject(dataStream, data, parameters);
        fillCustomObject(param, parameters, Map.of(BinS.class, binS, byte[].class, extraData));

        Object methodReturn;

        try {
            methodReturn = method.invoke(object, param);
        } catch (Exception e) {
            return DataLink.of(DataLabel.BIN_R, BinR.of(binS.requestId, Code.SERVER_ERROR, null, false, null), null);
        }

        if (methodReturn == null)
            return emptyBinRDataLink(BinR.of(binS.requestId, Code.REQUEST_FAIL));

        Class<?> returnClass = methodReturn.getClass();
        if (methodReturn instanceof Code code)
            return emptyBinRDataLink(BinR.of(binS.requestId, code));
        else if (methodReturn instanceof BinR binR)
            return emptyBinRDataLink(binR);
        else if (methodReturn instanceof JSONObject jsonObject)
            return emptyBinRDataLink(BinR.of(binS.requestId, Code.OK, jsonObject));
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

        Method method = mo.method();
        Object object = mo.object();

        JSONObject data = longS.data;
        if (data == null) {
            data = new JSONObject();
        }

        Parameter[] parameters = method.getParameters();
        Object[] param = fillObject(dataStream, data, parameters);
        LongMsgStream longMsgStream = longMsgManage.getStream(longS.requestId);
        fillCustomObject(param, parameters, Map.of(LongS.class, longS,
                LongMsgStream.class, longMsgStream,
                InputStream.class, longMsgStream.getInputStream()));

        Object methodReturn;

        try {
            methodReturn = method.invoke(object, param);
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
        else if (methodReturn instanceof Boolean bool)
            return LongR.of(longS.requestId, bool ? Code.OK : Code.REQUEST_FAIL, null);
        else if (methodReturn instanceof Number || returnClass == String.class)
            return LongR.of(longS.requestId, Code.OK, new JSONObject(Map.of("value", methodReturn)));

        return LongR.of(longS.requestId, Code.OK, (JSONObject) JSONObject.toJSON(methodReturn));
    }

    private void fillCustomObject(Object[] objects, Parameter[] parameters, Map<Class<?>, Object> objectMap) {
        Set<Class<?>> classes = objectMap.keySet();
        for (int i = 0; i < objects.length; i++) {
            if (classes.contains(parameters[i].getType())) {
                objects[i] = objectMap.get(parameters[i].getType());
            }
        }
    }

    private Object[] fillObject(DataStream dataStream, JSONObject data, Parameter[] parameters) {

        Object[] param = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> clazz = parameter.getType();

            DataParam dataParam = parameter.getAnnotation(DataParam.class);
            Injection injection = parameter.getAnnotation(Injection.class);
            if (mountManage.hasMount(clazz)) {
                param[i] = mountManage.getInstance(dataStream, clazz);
            } else if (clazz == DataStream.class) {
                param[i] = dataStream;
            } else if (dataParam != null) {
                if (clazz == String.class)
                    param[i] = data.getString(dataParam.value());
                else if (clazz == Integer.class || clazz == int.class)
                    param[i] = data.getInteger(dataParam.value());
                else if (clazz == Long.class || clazz == long.class)
                    param[i] = data.getLong(dataParam.value());
                else if (clazz == Boolean.class || clazz == boolean.class)
                    param[i] = data.getBoolean(dataParam.value());
                else if (clazz == BigInteger.class)
                    param[i] = data.getBigInteger(dataParam.value());
                else if (clazz == BigDecimal.class)
                    param[i] = data.getBigDecimal(dataParam.value());
                else
                    param[i] = data.getObject(dataParam.value(), clazz);
            } else if (injection != null) {
                if (injection.name().equals(""))
                    param[i] = containerManage.getFromClass(clazz);
                else
                    param[i] = containerManage.getFromName(injection.name());
            } else {
                Object obj = containerManage.getFromClass(clazz);
                if (obj != null)
                    param[i] = obj;
                else if (clazz.isAssignableFrom(Object.class))
                    param[i] = data.toJavaObject(clazz);
            }
        }
        return param;
    }

}
