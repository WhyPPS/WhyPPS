package club.hsspace.whypps.framework.app;

import club.hsspace.whypps.action.Init;
import club.hsspace.whypps.framework.manage.FileManage;
import club.hsspace.whypps.manage.ContainerManage;
import club.hsspace.whypps.processor.EquityProcessor;
import club.hsspace.whypps.processor.SpreadProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

/**
 * @ClassName: JarLoader
 * @CreateTime: 2022/6/5
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class ApiJarManage {

    private static final Logger logger = LoggerFactory.getLogger(ApiJarManage.class);

    public ApiJarManage() {

    }

    private MethodController methodController;

    private AttributeManage attributeManage;

    private MountManage mountManage;

    private ClassLoader apiClassLoader;

    @Init
    private void loadAllClass(ContainerManage containerManage, EquityProcessor equityProcessor, SpreadProcessor spreadProcessor) throws InvocationTargetException {
        methodController = new MethodController();
        containerManage.registerObject(methodController);
        containerManage.injection(methodController);

        attributeManage = new AttributeManage();
        containerManage.registerObject(attributeManage);
        containerManage.injection(attributeManage);

        mountManage = new MountManage();
        containerManage.registerObject(mountManage);
        containerManage.injection(mountManage);

        if(equityProcessor instanceof EquityDistribution ed){
            ed.injection(methodController, mountManage);
            logger.info("注入MethodController至EquityDistribution实现成功");
        }

        if(spreadProcessor instanceof SpreadDistribution sd){
            sd.injection(methodController, mountManage);
            logger.info("注入MethodController至SpreadDistribution实现成功");
        }

    }

    public static URL URLOf(String path) {
        try {
            return new URL(path);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    @Init(sort = 10)
    private void initAPIClassLoader(FileManage fileManage) {

        File file = fileManage.getFile("\\app\\applib");

        URL[] urls = Arrays.stream(file.listFiles((dir, name) -> name.endsWith(".jar") || name.endsWith(".jmod")))
                .map(n -> "file:///" + n)
                .map(ApiJarManage::URLOf)
                .toArray(URL[]::new);

        apiClassLoader = new URLClassLoader(urls, getClass().getClassLoader());
    }

    @Init(sort = 20)
    private void initAppClassLoader(ContainerManage containerManage, FileManage fileManage) {
        File file = fileManage.getFile("\\app");
        File[] files = file.listFiles((dir, name) -> name.endsWith(".jar"));

        for (File f : files) {
            AppJarControl ajl = new AppJarControl(apiClassLoader, f);
            try {
                containerManage.injection(ajl);
                logger.info("载入应用{}成功",f);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                continue;
            }
        }
    }


}
