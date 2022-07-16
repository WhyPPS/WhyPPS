package club.hsspace.whypps.framework.app;

import club.hsspace.whypps.action.Init;
import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.framework.plugin.PluginClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @ClassName: ApiClassLoader
 * @CreateTime: 2022/7/14
 * @Comment: API类加载器
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class ApiClassLoader extends URLClassLoader {

    private static final Logger logger = LoggerFactory.getLogger(ApiClassLoader.class);

    public ApiClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Injection
    private PluginClassLoader pluginClassLoader;

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> aClass = null;
        try {
            aClass = super.loadClass(name, resolve);
        } catch (ClassNotFoundException e) {
            if(aClass == null)
                aClass = pluginClassLoader.accessClass(name);

            if(aClass == null)
                throw e;
        }
        return aClass;
    }
}
