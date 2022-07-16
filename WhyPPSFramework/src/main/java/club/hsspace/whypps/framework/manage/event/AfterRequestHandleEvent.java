package club.hsspace.whypps.framework.manage.event;

import club.hsspace.whypps.framework.manage.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @ClassName: AfterRequestHandleEvent
 * @CreateTime: 2022/7/16
 * @Comment: 请求后触发事件
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class AfterRequestHandleEvent implements Event {

    private static final Logger logger = LoggerFactory.getLogger(AfterRequestHandleEvent.class);

    /**
     * 运行方法
     */
    private Method runMethod;

    /**
     * 执行对象
     */
    private Object object;

    /**
     * 执行参数
     */
    private Object[] objects;

    /**
     * 是否是拦截器
     */
    private boolean isInterceptor;

    /**
     * 方法返回
     */
    private Object returnType;

    public AfterRequestHandleEvent(Method runMethod, Object object, Object[] objects, boolean isInterceptor, Object returnType) {
        this.runMethod = runMethod;
        this.object = object;
        this.objects = objects;
        this.isInterceptor = isInterceptor;
        this.returnType = returnType;
    }

    public Method getRunMethod() {
        return runMethod;
    }

    public void setRunMethod(Method runMethod) {
        this.runMethod = runMethod;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Object[] getObjects() {
        return objects;
    }

    public void setObjects(Object[] objects) {
        this.objects = objects;
    }

    public boolean isInterceptor() {
        return isInterceptor;
    }

    public void setInterceptor(boolean interceptor) {
        isInterceptor = interceptor;
    }

    public Object getReturnType() {
        return returnType;
    }

    public void setReturnType(Object returnType) {
        this.returnType = returnType;
    }
}
