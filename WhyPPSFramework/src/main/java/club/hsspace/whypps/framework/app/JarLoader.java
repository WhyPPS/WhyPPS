package club.hsspace.whypps.framework.app;

import club.hsspace.whypps.action.Init;
import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.framework.manage.FileManage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * @ClassName: JarLoader
 * @CreateTime: 2022/6/5
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class JarLoader {

    private static final Logger logger = LoggerFactory.getLogger(JarLoader.class);

    public JarLoader() {

    }

    @Init
    public void loadAllClass(@Injection FileManage fileManage) throws IOException, ClassNotFoundException {
        File file = fileManage.getFile("\\app");
        File[] files = file.listFiles();
        for (File jar : files) {
            Set<Class<?>> classes = fileManage.loadAllClass(jar.getPath());
            classes.forEach(System.out::println);
        }
    }



}
