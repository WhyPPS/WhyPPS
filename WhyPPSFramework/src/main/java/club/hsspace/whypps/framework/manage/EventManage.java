package club.hsspace.whypps.framework.manage;

import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.manage.ContainerManage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @ClassName: EventManage
 * @CreateTime: 2022/7/14
 * @Comment: 事件驱动器
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class EventManage {

    private static final Logger logger = LoggerFactory.getLogger(EventManage.class);

    private Map<Class<?>, SortedMap<Method, Object>> eventMap = new HashMap<>();

    private Map<Class<?>, RunningSpace> runningSpaceMap = new HashMap<>();

    public EventManage() {

    }

    public void registerEvent(Object obj) {
        Class<?> aClass = obj.getClass();
        Method[] methods = aClass.getDeclaredMethods();
        for (Method method : methods) {
            registerMethod(obj, method);
        }
    }

    public void registerEvent(Object obj, Method method) {
        registerMethod(obj, method);
    }

    public void putRunningSpace(Class<?> runClass, RunningSpace runningSpace) {
        runningSpaceMap.put(runClass, runningSpace);
    }

    private void registerMethod(Object obj, Method method) {
        if(obj == null)
            return;

        EventListener eventListener = method.getAnnotation(EventListener.class);
        if(eventListener != null) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if(parameterTypes.length > 0 && Event.class.isAssignableFrom(parameterTypes[0])){
                Class<?> eventType = parameterTypes[0];
                if(!eventMap.containsKey(eventType)) {
                    eventMap.put(eventType, new TreeMap<>((o1, o2) -> {
                        EventListener e1 = o1.getAnnotation(EventListener.class);
                        EventListener e2 = o2.getAnnotation(EventListener.class);
                        return Integer.compare(e1.sort(), e2.sort());
                    }));
                }
                SortedMap<Method, Object> sm = eventMap.get(eventType);
                sm.put(method, obj);
            }
        }
    }

    @Injection
    private ContainerManage containerManage;

    public void triggerEvent(Event event) {
        triggerEvent(event, null);
    }

    public void triggerEvent(Event event, Map<Class<?>, Object> param) {
        Class<? extends Event> eventClass = event.getClass();
        SortedMap<Method, Object> sm = eventMap.get(eventClass);
        if(sm == null)
            return;

        Map<Class<?>, Object> libMap = Map.of(event.getClass(), event);
        if(param == null) {
            param = new HashMap<>(libMap);
        }else {
            param = new HashMap<>(param);
            param.putAll(libMap);
        }

        Map<Class<?>, Object> finalParam = param;
        sm.forEach((method, obj) -> {
            try {
                RunningSpace value = runningSpaceMap.get(method.getDeclaringClass());
                finalParam.put(RunningSpace.class, value);

                containerManage.invokeMethod(method, obj, finalParam);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }


}
