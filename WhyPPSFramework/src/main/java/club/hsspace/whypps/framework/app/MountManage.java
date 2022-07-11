package club.hsspace.whypps.framework.app;

import club.hsspace.whypps.handle.DataStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName: MountManage
 * @CreateTime: 2022/7/11
 * @Comment: 挂载参数管理器
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class MountManage {

    private static final Logger logger = LoggerFactory.getLogger(MountManage.class);

    private Map<Class<?>, Map<DataStream, Object>> mountClass = new HashMap<>();

    public void registerMount(Class<?> clazz) {
        mountClass.put(clazz, new HashMap<>());
    }

    public boolean hasMount(Class<?> clazz) {
        return mountClass.containsKey(clazz);
    }

    public <T> T getInstance(DataStream dataStream, Class<T> clazz) {
        if (mountClass.containsKey(clazz)) {
            InvocationHandler handler = (proxy, method, args) -> {
                Class<?> returnType = method.getReturnType();
                Map<DataStream, Object> map = mountClass.get(clazz);
                if (returnType == void.class) {
                    map.put(dataStream, args[0]);
                } else if (returnType == boolean.class) {
                    return map.containsKey(dataStream);
                }
                return map.get(dataStream);
            };
            return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, handler);
        }
        return null;
    }

}
