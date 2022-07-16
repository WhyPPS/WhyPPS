package club.hsspace.whypps.framework.app;

import club.hsspace.whypps.action.Init;
import club.hsspace.whypps.framework.app.annotation.*;
import club.hsspace.whypps.framework.manage.FileManage;
import club.hsspace.whypps.manage.ContainerManage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
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

    private Properties props;

    @Init
    private void init() throws IOException, JarFormatException {
        jarFile = new JarFile(jarFilePath.getPath());
        JarEntry app = jarFile.getJarEntry("whypps.app/app.properties");

        InputStream is = jarFile.getInputStream(app);
        props = new Properties();
        props.load(is);

        this.name = props.getProperty("app.name");
        //TODO: 正则匹配name只包含"a-zA-Z和."
        if (name == null || name.trim().equals("")) {
            throw new JarFormatException("app.name初始化失败");
        }

    }

    //TODO: 这里需要重构为RunningSpace和使用EventManage
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
        File[] files = lib.listFiles((dir, name) -> name.endsWith(".jar"));

        URL[] urls = Stream.concat(Stream.of(jarFilePath), Arrays.stream(files))
                .map(n -> "file:///" + n)
                .map(ApiJarManage::URLOf)
                .toArray(URL[]::new);

        appClassLoader = new URLClassLoader(urls, apiClassLoader);
    }

    @Init(sort = 20)
    private void initClass(MethodController methodController, ContainerManage containerManage, MountManage mountManage, InterceptorManage interceptorManage) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, JarFormatException {

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

        SortedSet<Method> initMethod = new TreeSet<>((o1, o2) -> {
            AppInterface o1App = o1.getDeclaringClass().getAnnotation(AppInterface.class);
            AppInterface o2App = o2.getDeclaringClass().getAnnotation(AppInterface.class);
            if (o1App.registerSort() != o2App.registerSort()) {
                return Integer.compare(o1App.registerSort(), o2App.registerSort());
            }

            Initialize o1Initialize = o1.getAnnotation(Initialize.class);
            Initialize o2Initialize = o2.getAnnotation(Initialize.class);
            return Integer.compare(o1Initialize.sort(), o2Initialize.sort());
        });

        SortedSet<Method> startMethod = new TreeSet<>((o1, o2) -> {
            AppStart o1Initialize = o1.getAnnotation(AppStart.class);
            AppStart o2Initialize = o2.getAnnotation(AppStart.class);
            return Integer.compare(o1Initialize.sort(), o2Initialize.sort());
        });

        for (Class<?> clazz : classSet) {
            AppInterface appInterface = clazz.getAnnotation(AppInterface.class);
            if (appInterface != null) {

                Object object = null;
                if (appInterface.autoRegister()) {
                    try {
                        Constructor<?> constructor = clazz.getDeclaredConstructor();
                        if (constructor != null) {
                            object = constructor.newInstance();
                            containerManage.registerObject(object);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                methodController.scanMethod(clazz, object);

                Method[] declaredMethods = clazz.getDeclaredMethods();
                for (Method declaredMethod : declaredMethods) {
                    Initialize initialize = declaredMethod.getAnnotation(Initialize.class);
                    if (initialize != null)
                        initMethod.add(declaredMethod);

                    AppStart appStart = declaredMethod.getAnnotation(AppStart.class);
                    if (appStart != null)
                        startMethod.add(declaredMethod);

                    Interceptor interceptor = declaredMethod.getAnnotation(Interceptor.class);
                    if(interceptor != null)
                        interceptorManage.registerInterceptor(interceptor, declaredMethod, object);
                }
            }

            MountEntity mountEntity = clazz.getAnnotation(MountEntity.class);
            if(mountEntity != null && clazz.isInterface()) {
                mountManage.registerMount(clazz);
            }

        }

        for (Class<?> clazz : classSet) {
            AppInterface appInterface = clazz.getAnnotation(AppInterface.class);
            Object obj = containerManage.getFromClass(clazz);
            if (appInterface != null) {
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    Object o = containerManage.getFromClass(field.getType());
                    if (o != null) {
                        field.set(obj, o);
                    }
                }
            }
        }

        for (Method method : initMethod) {
            Class<?> clazz = method.getDeclaringClass();
            Object obj = containerManage.getFromClass(clazz);
            if (obj != null)
                containerManage.invokeMethod(method, obj);
        }

        for (Method method : startMethod) {
            AppStart appStart = method.getAnnotation(AppStart.class);
            Class<?> clazz = method.getDeclaringClass();
            Object obj = containerManage.getFromClass(clazz);
            if (obj != null) {
                method.setAccessible(true);
                if(!appStart.thread()) {
                    containerManage.invokeMethod(method, obj);
                } else {
                    new Thread(() -> {
                        try {
                            containerManage.invokeMethod(method, obj);
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException(e);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }).start();
                }
            }else {
                throw new JarFormatException("应用格式化失败！启动方法" + method + "无实例");
            }
        }

    }

}
