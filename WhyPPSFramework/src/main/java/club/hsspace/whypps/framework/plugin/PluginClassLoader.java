package club.hsspace.whypps.framework.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @ClassName: PluginClassLoader
 * @CreateTime: 2022/7/14
 * @Comment: 插件类加载器
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class PluginClassLoader extends URLClassLoader {

    private static final Logger logger = LoggerFactory.getLogger(PluginClassLoader.class);

    public PluginClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public Class<?> accessClass(String name) {
        try {
            //TODO： 逻辑更新，不能用loadClass，改为只查PluginClassLoader尝试加载的类
            Class<?> aClass = loadClass(name, true);
            /*Access access = aClass.getAnnotation(Access.class);
            if(access != null)*/
            return aClass;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }


}
