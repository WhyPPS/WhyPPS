package club.hsspace.whypps.framework.plugin;

import club.hsspace.whypps.action.Init;
import club.hsspace.whypps.framework.manage.FileManage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
    private void initPluginClassLoader(FileManage fileManage) {
        File file = fileManage.getFile("\\plugins");
        runPath = file.getPath();

        List<URL> urls = new ArrayList<>();

        File[] files = file.listFiles((dir, name) -> name.endsWith(".jar"));
        for (File f : files) {
            try {
                JarFile jarFile = new JarFile(f.getPath());
            } catch (IOException e) {
                continue;
            }
        }
    }

}
