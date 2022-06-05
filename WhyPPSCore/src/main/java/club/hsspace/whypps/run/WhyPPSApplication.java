package club.hsspace.whypps.run;

import club.hsspace.whypps.manage.ContainerManage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * @ClassName: WhyPPSApplication
 * @CreateTime: 2022/3/8
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class WhyPPSApplication {

    private static final Logger logger = LoggerFactory.getLogger(WhyPPSApplication.class);

    private ContainerManage containerManage;

    private Class<?> runClass;

    private WhyPPSApplication(Object o) throws InvocationTargetException {

        long start = System.currentTimeMillis();

        //启动容器管理器
        if(o instanceof Class clazz) {
            this.runClass = clazz;
            containerManage = new ContainerManage(runClass);
        } else {
            this.runClass = o.getClass();
            containerManage = new ContainerManage(o);
        }

        containerManage.registerObject(this);
        logger.info("WhyPPSCore初始化成功，用时{}ms", System.currentTimeMillis() - start);
    }

    public static WhyPPSApplication run(Class<?> runClass) {
        try {
            return new WhyPPSApplication(runClass);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static WhyPPSApplication run(Object runObject) {
        try {
            return new WhyPPSApplication(runObject);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ContainerManage getContainerManage() {
        return containerManage;
    }
}
