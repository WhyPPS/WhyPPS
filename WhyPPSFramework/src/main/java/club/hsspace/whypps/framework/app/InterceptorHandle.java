package club.hsspace.whypps.framework.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: InterceptorHandle
 * @CreateTime: 2022/7/16
 * @Comment: 拦截器处理机
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class InterceptorHandle {

    private static final Logger logger = LoggerFactory.getLogger(InterceptorHandle.class);

    private Object returnParam;

    public boolean isInterrupt() {
        return returnParam != null;
    }

    public Object getInterruptReturn() {
        return returnParam;
    }

    public void interruptReturn(Object obj) {
        returnParam = obj;
    }

}
