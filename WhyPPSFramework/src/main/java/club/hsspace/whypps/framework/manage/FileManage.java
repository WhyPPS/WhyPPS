package club.hsspace.whypps.framework.manage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * @ClassName: FileManage
 * @CreateTime: 2022/6/5
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class FileManage {

    private static final Logger logger = LoggerFactory.getLogger(FileManage.class);

    private String runPath;

    public FileManage() {

        //String property = System.getProperty("java.class.path");
        runPath = Path.of(this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile().substring(1)).getParent().toString();
        //runPath = Path.of(property).getParent().toString();
        logger.info("加载系统运行环境{}", runPath);

    }

    public File getFile(String path) {
        return Path.of(runPath, path).toFile();
    }

    public String getRunPath() {
        return runPath;
    }


    public Set<Class<?>> loadAllClass(String jarPath) throws IOException, ClassNotFoundException {
        Set<Class<?>> classSet = new HashSet<>(16);

        URL url = new URL("file:///" + jarPath);
        URLClassLoader classLoader = new URLClassLoader(new URL[]{url}, getClass().getClassLoader());

        JarFile jarFile = new JarFile(jarPath);
        Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String jarName = jarEntry.getName();
            //System.out.println(jarName);
            if (!jarEntry.isDirectory() && jarName.endsWith(".class")) {
                // 将文件路径名转换为包名称的形式
                String className = jarName.replace(".class", "");
                //className = ClassUtils.convertResourcePathToClassName(className);
                className = className.replace('/', '.');
                Class<?> aClass = classLoader.loadClass(className);
                classSet.add(aClass);
            }
        }

        return classSet;
    }
}
