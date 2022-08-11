package club.hsspace.whypps;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @ClassName: LibManage
 * @CreateTime: 2022/6/30
 * @Comment: 前置Jar处理器
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class LibManage {

    private static final Logger logger = LoggerFactory.getLogger(LibManage.class);

    private Path runPath;

    private ArgsManage argsManage;

    private URLClassLoader urlClassLoader;

    public LibManage(ArgsManage argsManage) throws WhyPPSFrameworkNotFoundException {
        this.argsManage = argsManage;

        runPath = Path.of(this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile().substring(1)).getParent();
        System.out.println("加载系统运行环境: " + runPath);

        loadJar();

        start();
    }

    public void loadJar() throws WhyPPSFrameworkNotFoundException {
        String frameworkPath = argsManage.getArgs("frameworkPath");
        WhyPPSFrameworkNotFoundException ex = new WhyPPSFrameworkNotFoundException("未识别到参数frameworkPath或参数无效，请指定框架运行目录");
        if(frameworkPath == null) {
            throw ex;
        } else {
            URL url;
            try {
                url = new URL("file:///" + Path.of(runPath.toString(), frameworkPath));
            } catch (MalformedURLException e) {
                throw ex;
            }
            urlClassLoader = new URLClassLoader(new URL[]{url}, LibManage.class.getClassLoader());
        }
    }

    private Object whyPPSFramework;

    public synchronized void start() throws WhyPPSFrameworkNotFoundException {
        try {
            Class<?> aClass = urlClassLoader.loadClass("club.hsspace.whypps.framework.run.WhyPPSFramework");
            Method run = aClass.getMethod("run");
            whyPPSFramework = run.invoke(null);

            /*new Thread(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                logger.info("======执行关闭程序======");
                try {
                    Method close = aClass.getMethod("close");
                    close.invoke(whyPPSFramework);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }).start();*/
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new WhyPPSFrameworkNotFoundException("包WhyPPSFramework版本不符或错误");
        }
    }

    public void stop() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method close = urlClassLoader.loadClass("club.hsspace.whypps.framework.run.WhyPPSFramework").getMethod("close");
        close.invoke(whyPPSFramework);
    }

}
