package club.hsspace.whypps.framework.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: AppCustumClassLoader
 * @CreateTime: 2022/6/16
 * @Comment: APP自定义类加载器
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class AppCustomClassLoader extends ClassLoader {

    private static final Logger logger = LoggerFactory.getLogger(AppCustomClassLoader.class);

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

        return super.findClass(name);
    }
}
