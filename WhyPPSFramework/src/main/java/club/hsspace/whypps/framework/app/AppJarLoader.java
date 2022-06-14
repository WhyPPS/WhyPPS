package club.hsspace.whypps.framework.app;

import club.hsspace.whypps.action.Init;
import club.hsspace.whypps.framework.app.annotation.AppInterface;
import club.hsspace.whypps.framework.manage.FileManage;
import club.hsspace.whypps.manage.ContainerManage;
import club.hsspace.whypps.processor.EquityProcessor;
import club.hsspace.whypps.processor.SpreadProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

/**
 * @ClassName: JarLoader
 * @CreateTime: 2022/6/5
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class AppJarLoader {

    private static final Logger logger = LoggerFactory.getLogger(AppJarLoader.class);

    public AppJarLoader() {

    }

    private MethodController methodController;

    @Init
    public void loadAllClass(FileManage fileManage, ContainerManage containerManage, EquityProcessor equityProcessor, SpreadProcessor spreadProcessor) throws IOException, ClassNotFoundException, InvocationTargetException {
        methodController = new MethodController();
        containerManage.registerObject(methodController);
        containerManage.injection(methodController);

        if(equityProcessor instanceof EquityDistribution ed){
            ed.injection(methodController);
            logger.info("注入MethodController至EquityDistribution实现成功");
        }

        if(spreadProcessor instanceof SpreadDistribution sd){
            sd.injection(methodController);
            logger.info("注入MethodController至SpreadDistribution实现成功");
        }

        File file = fileManage.getFile("\\app");
        File[] files = file.listFiles();
        for (File jar : files) {
            Set<Class<?>> classes = fileManage.loadAllClass(jar.getPath());
            for (Class<?> clazz : classes) {
                AppInterface appInterface = clazz.getAnnotation(AppInterface.class);
                if(appInterface != null)
                    methodController.scanMethod(clazz);
            }
        }
    }



}
