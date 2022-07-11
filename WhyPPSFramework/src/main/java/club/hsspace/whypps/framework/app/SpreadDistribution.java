package club.hsspace.whypps.framework.app;

import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.framework.app.annotation.DataParam;
import club.hsspace.whypps.handle.DataStream;
import club.hsspace.whypps.manage.ContainerManage;
import club.hsspace.whypps.model.RadioMsg;
import club.hsspace.whypps.model.senior.*;
import club.hsspace.whypps.processor.impl.SpreadProcessorImpl;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName: SpreadDistribution
 * @CreateTime: 2022/6/14
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class SpreadDistribution extends SpreadProcessorImpl {

    private static final Logger logger = LoggerFactory.getLogger(SpreadDistribution.class);

    private MethodController methodController;

    @Injection
    private ContainerManage containerManage;

    private MountManage mountManage;

    public void injection(MethodController methodController, MountManage mountManage) {
        this.mountManage = mountManage;
        this.methodController = methodController;
    }

    @Override
    public void listenerRadioMsg(DataStream dataStream, Radio radio, RadioMsg radioMsg) {
        MethodController.MethodAndObject radioMethod = methodController.getRadioMethod(radioMsg.api);
        if (radioMethod == null)
            return;

        Method method = radioMethod.method();
        Object object = radioMethod.object();

        JSONObject data = radioMsg.data;
        if (data == null) {
            data = new JSONObject();
        }

        Parameter[] parameters = method.getParameters();
        Object[] param = fillObject(dataStream, data, parameters);
        fillCustomObject(param, parameters, Map.of(Radio.class, radio, RadioMsg.class, radioMethod));

        try {
            method.invoke(object, param);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO: 这里需要提供一种新的API，去重构Swap转发规则(有空再实现，感觉暂时需求场景不多)

    @Override
    public SwapR returnSwapMsg(DataStream dataStream, JSONArray ja, SwapS swapS, int involve) {
        MethodController.MethodAndObject mo = methodController.getSwapMethod(swapS.api);
        if (mo == null)
            return SwapR.of(swapS.requestId, Code.NOT_FOUND, ja, involve);

        Method method = mo.method();
        Object object = mo.object();

        JSONObject data = swapS.data;
        if (data == null) {
            data = new JSONObject();
        }

        Parameter[] parameters = method.getParameters();
        Object[] param = fillObject(dataStream, data, parameters);
        fillCustomObject(param, parameters, Map.of(SwapS.class, swapS, JSONArray.class, ja));

        Object methodReturn;

        try {
            methodReturn = method.invoke(object, param);
        } catch (Exception e) {
            return SwapR.of(swapS.requestId, Code.SERVER_ERROR, ja, involve);
        }

        if (methodReturn == null)
            return SwapR.of(swapS.requestId, Code.REQUEST_FAIL, ja, involve);

        Class<?> returnClass = methodReturn.getClass();
        if (methodReturn instanceof Code code)
            return SwapR.of(swapS.requestId, code, ja, involve);
        else if (methodReturn instanceof SwapR dataR)
            return dataR;
        else if (methodReturn instanceof JSONObject jsonObject) {
            ja.add(jsonObject);
            return SwapR.of(swapS.requestId, Code.OK, ja, involve);
        } else if (methodReturn instanceof Boolean bool)
            return SwapR.of(swapS.requestId, bool ? Code.OK : Code.REQUEST_FAIL, ja, involve);
        else if (methodReturn instanceof Number || returnClass == String.class) {
            JSONObject jo = new JSONObject(Map.of("value", methodReturn));
            ja.add(jo);
            return SwapR.of(swapS.requestId, Code.OK, ja, involve);
        }

        JSONObject jo = (JSONObject) JSONObject.toJSON(methodReturn);
        ja.add(jo);
        return SwapR.of(swapS.requestId, Code.OK, ja, involve);
    }

    private void fillCustomObject(Object[] objects, Parameter[] parameters, Map<Class<?>, Object> objectMap) {
        Set<Class<?>> classes = objectMap.keySet();
        for (int i = 0; i < objects.length; i++) {
            if (parameters[i].getAnnotation(DataParam.class) == null && classes.contains(parameters[i].getType())) {
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
