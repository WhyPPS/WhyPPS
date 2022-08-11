package club.hsspace.whypps.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @ClassName: ClassScanner
 * @CreateTime: 2022/3/12
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class ClassScanner {

    private static final Logger logger = LoggerFactory.getLogger(ClassScanner.class);

    private static ClassLoader classLoader = ClassScanner.class.getClassLoader();

    private static List<Class<?>> scannerClass(String packet, boolean scanSubdirectories) {
        List<Class<?>> result = new ArrayList<>();
        URL url = classLoader.getResource(packet.replace(".", "/"));
        String protocol = url.getProtocol();
        if ("file".equals(protocol)) {
            findClassLocal(packet, result, scanSubdirectories);
        } else if ("jar".equals(protocol)) {
            findClassJar(packet, result, scanSubdirectories);
        }
        return result;
    }

    public static List<Class<?>> scannerClass(String packet) {
        if (packet.endsWith(".**")) {
            List<Class<?>> classes = ClassScanner.scannerClass(packet.substring(0, packet.length() - 3), true);
            return classes;
        } else if (packet.endsWith(".*")) {
            List<Class<?>> classes = ClassScanner.scannerClass(packet.substring(0, packet.length() - 2), false);
            return classes;
        }
        try {
            return List.of(Class.forName(packet));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return List.of();
    }

    /**
     * 本地查找
     *
     * @param packName
     */
    private static void findClassLocal(String packName, List<Class<?>> result, boolean scanSubdirectories) {
        URI url = null;

        try {
            url = classLoader.getResource(packName.replace(".", "/")).toURI();
        } catch (URISyntaxException e1) {
            throw new RuntimeException("未找到策略资源");
        }

        File file = new File(url);
        file.listFiles(chiFile -> {
            if (chiFile.isDirectory() && scanSubdirectories) {
                findClassLocal(packName + "." + chiFile.getName(), result, true);
            }
            if (!chiFile.getName().equals("module-info.class") && chiFile.getName().endsWith(".class")) {
                Class<?> clazz = null;
                try {
                    clazz = classLoader.loadClass(packName + "." + chiFile.getName().replace(".class", ""));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                result.add(clazz);
                return true;
            }
            return false;
        });

    }

    /**
     * jar包查找
     *
     * @param packName
     */
    private static void findClassJar(String packName, List<Class<?>> result, boolean scanSubdirectories) {
        String pathName = packName.replace(".", "/");
        JarFile jarFile = null;
        try {
            URL url = classLoader.getResource(pathName);
            JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
            jarFile = jarURLConnection.getJarFile();
        } catch (IOException e) {
            throw new RuntimeException("未找到策略资源");
        }

        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            String jarEntryName = jarEntry.getName();

            if (jarEntryName.contains(pathName) && !jarEntryName.equals(pathName + "/")) {
                //递归遍历子目录
                if (jarEntry.isDirectory() && scanSubdirectories) {
                    String clazzName = jarEntry.getName().replace("/", ".");
                    int endIndex = clazzName.lastIndexOf(".");
                    String prefix = null;
                    if (endIndex > 0) {
                        prefix = clazzName.substring(0, endIndex);
                    }
                    findClassJar(prefix, result, true);
                }
                if (!jarEntry.getName().equals("module-info.class") && jarEntry.getName().endsWith(".class")) {
                    Class<?> clazz = null;
                    try {
                        clazz = classLoader.loadClass(jarEntry.getName().replace("/", ".").replace(".class", ""));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    result.add(clazz);
                }
            }

        }

    }

}
