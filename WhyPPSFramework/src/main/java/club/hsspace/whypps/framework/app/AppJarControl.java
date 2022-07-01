package club.hsspace.whypps.framework.app;

import club.hsspace.whypps.action.Init;
import club.hsspace.whypps.framework.app.annotation.AppInterface;
import club.hsspace.whypps.framework.manage.FileManage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * @ClassName: AppJarLoader
 * @CreateTime: 2022/7/1
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class AppJarControl {

    private static final Logger logger = LoggerFactory.getLogger(AppJarControl.class);

    private File jarFilePath;

    private String name;

    public AppJarControl(ClassLoader apiClassLoader, File jarFile) {
        this.apiClassLoader = apiClassLoader;
        this.jarFilePath = jarFile;
    }

    private ClassLoader appClassLoader;

    private ClassLoader apiClassLoader;

    private JarFile jarFile;

    @Init
    private void init() throws IOException, JarFormatException {
        jarFile = new JarFile(jarFilePath.getPath());
        JarEntry app = jarFile.getJarEntry("whypps.app/app.properties");

        InputStream is = jarFile.getInputStream(app);
        Properties props = new Properties();
        props.load(is);

        this.name = props.getProperty("app.name");
        //TODO: 正则匹配name只包含"a-zA-Z和."
        if (name == null || name.trim().equals("")) {
            throw new JarFormatException("app.name初始化失败");
        }

    }

    private String runPath;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Init(sort = 10)
    private void initLib(FileManage fileManage) throws IOException {
        File file = fileManage.getFile("\\app\\" + name);
        runPath = file.getPath();
        if (!file.exists()) {
            file.mkdir();
        }

        File lib = Path.of(runPath, "lib").toFile();
        if (!lib.exists()) {
            lib.mkdir();
        }

        //应用状态文件
        File run = Path.of(runPath, "app.s").toFile();
        if (!run.exists()) {
            run.createNewFile();
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(run)))) {
                bw.write("name: " + this.name + "\n");
                bw.write("init: " + formatter.format(ZonedDateTime.now()) + "\n");
            }

            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (!jarEntry.isDirectory() && jarEntry.getName().startsWith("whypps.app/init/")) {
                    File initFile = Path.of(runPath, jarEntry.getName().substring(16)).toFile();
                    try (OutputStream os = new FileOutputStream(initFile)) {
                        try (InputStream is = jarFile.getInputStream(jarEntry)) {
                            byte[] bytes = is.readAllBytes();
                            os.write(bytes);
                        }
                    }
                }
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(run, true)))) {
            bw.write("\napp_start: " + formatter.format(ZonedDateTime.now()) + "\n");
        }

        //加载应用级前置
        File[] files = file.listFiles((dir, name) -> name.endsWith(".jar"));

        URL[] urls = Stream.concat(Stream.of(jarFilePath), Arrays.stream(files))
                .map(n -> "file:///" + n)
                .map(ApiJarManage::URLOf)
                .toArray(URL[]::new);

        appClassLoader = new URLClassLoader(urls, apiClassLoader);
    }

    @Init(sort = 20)
    private void initClass(MethodController methodController) throws ClassNotFoundException {

        Set<Class<?>> classSet = new HashSet<>(16);

        Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String jarName = jarEntry.getName();
            if (!jarEntry.isDirectory() && jarName.endsWith(".class")) {
                String className = jarName.replace(".class", "");
                className = className.replace('/', '.');
                Class<?> aClass = appClassLoader.loadClass(className);
                classSet.add(aClass);
            }
        }

        for (Class<?> clazz : classSet) {
            AppInterface appInterface = clazz.getAnnotation(AppInterface.class);
            if (appInterface != null)
                methodController.scanMethod(clazz);
        }
    }

}
