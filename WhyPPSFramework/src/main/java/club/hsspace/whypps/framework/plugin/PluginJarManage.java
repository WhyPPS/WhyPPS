package club.hsspace.whypps.framework.plugin;

import club.hsspace.whypps.action.Init;
import club.hsspace.whypps.framework.app.ApiJarManage;
import club.hsspace.whypps.framework.manage.FileManage;
import club.hsspace.whypps.manage.ContainerManage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarFile;

/**
 * @ClassName: PluginJarLoader
 * @CreateTime: 2022/7/1
 * @Comment: 插件Jar读取器
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class PluginJarManage {

    private static final Logger logger = LoggerFactory.getLogger(PluginJarManage.class);

    public PluginJarManage() {

    }

    private String runPath;

    @Init
    private void initPluginClassLoader(FileManage fileManage, ContainerManage containerManage) {
        File file = fileManage.getFile("\\plugins");
        runPath = file.getPath();

        List<URL> urls = new ArrayList<>();

        Arrays.stream(Objects.requireNonNull(fileManage.getFile("\\plugins\\pluginlib")
                        .listFiles((dir, name) -> name.endsWith(".jar"))))
                .map(n -> "file:///" + n)
                .map(ApiJarManage::URLOf)
                .forEach(urls::add);

        File[] files = file.listFiles((dir, name) -> name.endsWith(".jar"));
        Arrays.stream(files)
                .map(n -> "file:///" + n)
                .map(ApiJarManage::URLOf)
                .forEach(urls::add);

        ClassLoader pluginClassLoader = new URLClassLoader(urls.toArray(URL[]::new), PluginJarManage.class.getClassLoader());

        for (File f : files) {
            PluginControl pluginControl = new PluginControl(f);
            try {
                containerManage.injection(pluginControl);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
