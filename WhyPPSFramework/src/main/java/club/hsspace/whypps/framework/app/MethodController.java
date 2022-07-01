package club.hsspace.whypps.framework.app;

import club.hsspace.whypps.action.Init;
import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.framework.app.annotation.*;
import club.hsspace.whypps.manage.ContainerManage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: MethodController
 * @CreateTime: 2022/6/6
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class MethodController {

    private static final Logger logger = LoggerFactory.getLogger(MethodController.class);

    private Map<String, Method> dataMsgMap = new HashMap<>();

    private Map<String, Method> binMsgMap = new HashMap<>();

    private Map<String, Method> longMsgMap = new HashMap<>();

    private Map<String, Method> radioMsgMap = new HashMap<>();

    private Map<String, Method> swapMsgMap = new HashMap<>();

    /** 执行实例 */
    private Map<Method, Object> runObject = new HashMap<>();

    @Injection
    private ContainerManage containerManage;

    public <T> int scanMethod(Class<T> clazz) {
        int registerMethod = 0;

        AppInterface appInterface = clazz.getAnnotation(AppInterface.class);
        T t = null;
        if(appInterface.autoRegister()) {
            try {
                t = newInstance(clazz);
                containerManage.registerObject(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            boolean runMethod = false;
            ApiDataMsg apiDataMsg = method.getAnnotation(ApiDataMsg.class);
            if(apiDataMsg != null) {
                dataMsgMap.put(apiDataMsg.value(), method);
                runMethod = true;
                registerMethod++;
            }

            ApiBinMsg apiBinMsg = method.getAnnotation(ApiBinMsg.class);
            if(apiBinMsg != null) {
                binMsgMap.put(apiBinMsg.value(), method);
                runMethod = true;
                registerMethod++;
            }

            ApiLongMsg apiLongMsg = method.getAnnotation(ApiLongMsg.class);
            if(apiLongMsg != null) {
                longMsgMap.put(apiLongMsg.value(), method);
                runMethod = true;
                registerMethod++;
            }

            ApiRadioMsg apiRadioMsg = method.getAnnotation(ApiRadioMsg.class);
            if(apiRadioMsg != null) {
                radioMsgMap.put(apiRadioMsg.value(), method);
                runMethod = true;
                registerMethod++;
            }

            ApiSwapMsg apiSwapMsg = method.getAnnotation(ApiSwapMsg.class);
            if(apiSwapMsg != null) {
                swapMsgMap.put(apiSwapMsg.value(), method);
                runMethod = true;
                registerMethod++;
            }

            if(t != null && runMethod) {
                runObject.put(method, t);
            }
        }
        return registerMethod;
    }

    public record MethodAndObject(Method method, Object object){}

    //获取执行组
    public MethodAndObject getDataMethod(String api) {
        Method method = dataMsgMap.get(api);
        if(method == null)
            return null;

        Object object = runObject.get(method);
        if(object == null)
            return null;
        return new MethodAndObject(method, object);
    }

    public MethodAndObject getBinMethod(String api) {
        Method method = binMsgMap.get(api);
        if(method == null)
            return null;

        Object object = runObject.get(method);
        if(object == null)
            return null;
        return new MethodAndObject(method, object);
    }

    public MethodAndObject getLongMethod(String api) {
        Method method = longMsgMap.get(api);
        if(method == null)
            return null;

        Object object = runObject.get(method);
        if(object == null)
            return null;
        return new MethodAndObject(method, object);
    }

    public MethodAndObject getRadioMethod(String api) {
        Method method = radioMsgMap.get(api);
        if(method == null)
            return null;

        Object object = runObject.get(method);
        if(object == null)
            return null;
        return new MethodAndObject(method, object);
    }

    public MethodAndObject getSwapMethod(String api) {
        Method method = swapMsgMap.get(api);
        if(method == null)
            return null;

        Object object = runObject.get(method);
        if(object == null)
            return null;
        return new MethodAndObject(method, object);
    }

    private <T> T newInstance(Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<T> constructor = clazz.getConstructor();
        T t = constructor.newInstance();
        return t;
    }

}
