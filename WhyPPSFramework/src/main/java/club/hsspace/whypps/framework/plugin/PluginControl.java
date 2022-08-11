package club.hsspace.whypps.framework.plugin;

import club.hsspace.whypps.action.Init;
import club.hsspace.whypps.framework.app.JarFormatException;
import club.hsspace.whypps.framework.manage.*;
import club.hsspace.whypps.framework.manage.EventListener;
import club.hsspace.whypps.framework.manage.event.FrameworkStartedEvent;
import club.hsspace.whypps.manage.ContainerManage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @ClassName: PluginControl
 * @CreateTime: 2022/7/13
 * @Comment: 插件空间管理控制器
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class PluginControl {

    private static final Logger logger = LoggerFactory.getLogger(PluginControl.class);

    private File pluginPath;

    private ClassLoader pluginClassLoader;

    private String name;

    public PluginControl(File pluginPath, ClassLoader pluginClassLoader) {
        this.pluginClassLoader = pluginClassLoader;
        this.pluginPath = pluginPath;
    }

    private JarFile jarFile;

    private Properties props;

    @Init
    public void initPlugin() throws IOException, JarFormatException {

        jarFile = new JarFile(pluginPath.getPath());
        JarEntry app = jarFile.getJarEntry("whypps.plugin/plugin.properties");

        InputStream is = jarFile.getInputStream(app);
        props = new Properties();
        props.load(is);

        this.name = props.getProperty("plugin.name");
        //TODO: 正则匹配name只包含"a-zA-Z和."
        if (name == null || name.trim().equals("")) {
            throw new JarFormatException("plugin.name初始化失败");
        }
    }

    private RunningSpace runningSpace;

    @Init(sort = 10)
    public void initRunSpace(FileManage fileManage, SpaceManage spaceManage) throws IOException {
        File file = fileManage.getFile("\\plugins\\" + name);
        runningSpace = new RunningSpace(file.getPath());
        spaceManage.registerRunningSpace(name, runningSpace);

        if (!file.exists()) {
            file.mkdir();

            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if(jarEntry.getName().startsWith("whypps.plugin/init/")) {
                    File initFile = Path.of(runningSpace.getRunningSpace(), jarEntry.getName().substring(19)).toFile();
                    if (jarEntry.isDirectory()) {
                        initFile.mkdir();
                    } else {
                        try (OutputStream os = new FileOutputStream(initFile)) {
                            try (InputStream is = jarFile.getInputStream(jarEntry)) {
                                byte[] bytes = is.readAllBytes();
                                os.write(bytes);
                            }
                        }
                    }
                }
            }
        }
    }

    @Init(sort = 20)
    public void initClass(EventManage eventManage, ContainerManage containerManage) throws ClassNotFoundException {

        Set<Class<?>> classSet = new HashSet<>(16);

        Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String jarName = jarEntry.getName();
            if (!jarEntry.isDirectory() && !jarName.equals("module-info.class") && jarName.endsWith(".class")) {
                String className = jarName.replace(".class", "");
                className = className.replace('/', '.');
                Class<?> aClass = pluginClassLoader.loadClass(className);
                classSet.add(aClass);
            }
        }

        List<Object> initObj = new ArrayList<>();

        for (Class<?> aClass : classSet) {
            ScanPlugin scanPlugin = aClass.getAnnotation(ScanPlugin.class);
            if(scanPlugin != null) {
                eventManage.putRunningSpace(aClass, runningSpace);

                Method[] declaredMethods = aClass.getDeclaredMethods();
                Object obj = null;
                for (Method method : declaredMethods) {
                    EventListener eventListener = method.getAnnotation(EventListener.class);
                    if(eventListener != null && eventListener.autoRegister()) {
                        if(obj == null){
                            try {
                                Constructor<?> constructor = aClass.getConstructor();
                                obj = constructor.newInstance();
                                containerManage.registerObject(obj);
                                initObj.add(obj);
                            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                                     IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        eventManage.registerEvent(obj, method);
                    }
                }
            }
        }

        for (Object o : initObj) {
            try {
                containerManage.injection(o);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
