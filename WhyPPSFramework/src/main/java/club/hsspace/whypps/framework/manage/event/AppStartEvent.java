package club.hsspace.whypps.framework.manage.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @ClassName: AppStartEvent
 * @CreateTime: 2022/8/10
 * @Comment: AppStart注解执行
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class AppStartEvent {

    private static final Logger logger = LoggerFactory.getLogger(AppStartEvent.class);

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
}
