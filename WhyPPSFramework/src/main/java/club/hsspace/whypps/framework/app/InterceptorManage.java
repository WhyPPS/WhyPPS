package club.hsspace.whypps.framework.app;

import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.framework.app.annotation.DataParam;
import club.hsspace.whypps.framework.app.annotation.Interceptor;
import club.hsspace.whypps.framework.app.annotation.RequestEnum;
import club.hsspace.whypps.framework.manage.EventManage;
import club.hsspace.whypps.framework.manage.event.AfterRequestHandleEvent;
import club.hsspace.whypps.framework.manage.event.BeforeRequestHandleEvent;
import club.hsspace.whypps.handle.DataStream;
import club.hsspace.whypps.manage.ContainerManage;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * @ClassName: InterceptorManage
 * @CreateTime: 2022/7/16
 * @Comment: 拦截器方法管理器
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class InterceptorManage {

    private static final Logger logger = LoggerFactory.getLogger(InterceptorManage.class);

    private Comparator<Interceptor> comparator = Comparator.comparingInt(Interceptor::sort);

    public Map<RequestEnum, Map<Interceptor, Method>> interceptorMap = Map.of(
            RequestEnum.DATA, new TreeMap<>(comparator),
            RequestEnum.BIN, new TreeMap<>(comparator),
            RequestEnum.HEART, new TreeMap<>(comparator),
            RequestEnum.LONG, new TreeMap<>(comparator),
            RequestEnum.SWAP, new TreeMap<>(comparator),
            RequestEnum.RADIO, new TreeMap<>(comparator)
    );

    public Map<Class<?>, Object> runObj = new HashMap<>();

    public void registerInterceptor(Interceptor interceptor, Method method, Object obj) {
        if(obj == null)
            return;

        for (RequestEnum requestEnum : interceptor.type()) {
            Map<Interceptor, Method> methodMap = interceptorMap.get(requestEnum);
            methodMap.put(interceptor, method);
        }

        runObj.put(method.getDeclaringClass(), obj);
    }

    @Injection
    private EventManage eventManage;

    public Object executeRequest(RequestEnum requestEnum, String api, DataStream dataStream, JSONObject data,
                                 MethodController.MethodAndObject mo, Map<Class<?>, Object> customObject) throws InvocationTargetException, IllegalAccessException {

        Map<Interceptor, Method> interceptorMethodMap = interceptorMap.get(requestEnum);
        for (Map.Entry<Interceptor, Method> entry : interceptorMethodMap.entrySet()) {
            Interceptor interceptor = entry.getKey();
            Method method = entry.getValue();
            if (interceptor.sort() >= 0) {
                if (interceptor.regexMode() && api.matches(interceptor.regex()) ||
                        !interceptor.regexMode() && Arrays.binarySearch(interceptor.list(), api) != 0) {

                    Object run = runObj.get(method.getDeclaringClass());

                    Parameter[] parameters = method.getParameters();
                    Object[] param = fillObject(dataStream, data, parameters, null);
                    fillCustomObject(param, parameters, customObject);
                    InterceptorHandle ih = new InterceptorHandle();
                    fillCustomObject(param, parameters, Map.of(InterceptorHandle.class, ih));

                    BeforeRequestHandleEvent event = new BeforeRequestHandleEvent(method, run, param, true);
                    eventManage.triggerEvent(event);
                    method = event.getRunMethod();
                    run = event.getObject();
                    param = event.getObjects();

                    Object invoke = method.invoke(run, param);
                    eventManage.triggerEvent(new AfterRequestHandleEvent(method, run, param, true, invoke));

                    if (ih.isInterrupt()) {
                        return ih.getInterruptReturn();
                    }

                }
            }
        }

        Object returnValue;

        Object object = mo.object();
        Method runMethod = mo.method();

        Parameter[] runParameters = runMethod.getParameters();
        Object[] runPram = fillObject(dataStream, data, runParameters, null);
        fillCustomObject(runPram, runParameters, customObject);

        BeforeRequestHandleEvent event = new BeforeRequestHandleEvent(runMethod, object, runPram, false);
        eventManage.triggerEvent(event);
        runMethod = event.getRunMethod();
        object = event.getObject();
        runPram = event.getObjects();

        returnValue = runMethod.invoke(object, runPram);
        eventManage.triggerEvent(new AfterRequestHandleEvent(runMethod, object, runPram, false, returnValue));

        for (Map.Entry<Interceptor, Method> entry : interceptorMethodMap.entrySet()) {
            Interceptor interceptor = entry.getKey();
            Method method = entry.getValue();
            if (interceptor.sort() < 0) {
                if (interceptor.regexMode() && api.matches(interceptor.regex()) ||
                        !interceptor.regexMode() && Arrays.binarySearch(interceptor.list(), api) != 0) {

                    Object run = runObj.get(method.getDeclaringClass());

                    Parameter[] parameters = method.getParameters();
                    Object[] param = fillObject(dataStream, data, parameters, returnValue);
                    fillCustomObject(param, parameters, customObject);
                    InterceptorHandle ih = new InterceptorHandle();
                    fillCustomObject(param, parameters, Map.of(InterceptorHandle.class, ih));

                    event = new BeforeRequestHandleEvent(method, run, param, true);
                    eventManage.triggerEvent(event);
                    method = event.getRunMethod();
                    run = event.getObject();
                    param = event.getObjects();

                    returnValue = method.invoke(run, param);
                    eventManage.triggerEvent(new AfterRequestHandleEvent(method, run, param, true, returnValue));

                    if (ih.isInterrupt()) {
                        return ih.getInterruptReturn();
                    }

                }
            }
        }

        return returnValue;

    }

    @Injection
    private MountManage mountManage;

    @Injection
    private ContainerManage containerManage;

    private void fillCustomObject(Object[] objects, Parameter[] parameters, Map<Class<?>, Object> objectMap) {
        Set<Class<?>> classes = objectMap.keySet();
        for (int i = 0; i < objects.length; i++) {
            if (classes.contains(parameters[i].getType())) {
                objects[i] = objectMap.get(parameters[i].getType());
            }
        }
    }

    private Object[] fillObject(DataStream dataStream, JSONObject data, Parameter[] parameters, Object returnValue) {

        Object[] param = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> clazz = parameter.getType();

            DataParam dataParam = parameter.getAnnotation(DataParam.class);
            Injection injection = parameter.getAnnotation(Injection.class);
            //TODO: 这里需要根据经验 使用频率排个序
            if (clazz == Object.class) {
                param[i] = returnValue;
            } else if (mountManage.hasMount(clazz)) {
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
                else
                    param[i] = data.toJavaObject(clazz);
            }
        }
        return param;
    }

}
