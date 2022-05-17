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

    private WhyPPSApplication(Class<?> runClass) throws InvocationTargetException {
        System.out.println("""
                __/\\\\\\______________/\\\\\\__/\\\\\\________________________/\\\\\\\\\\\\\\\\\\\\\\\\\\____/\\\\\\\\\\\\\\\\\\\\\\\\\\_______/\\\\\\\\\\\\\\\\\\\\\\___       \s
                 _\\/\\\\\\_____________\\/\\\\\\_\\/\\\\\\_______________________\\/\\\\\\/////////\\\\\\_\\/\\\\\\/////////\\\\\\___/\\\\\\/////////\\\\\\_      \s
                  _\\/\\\\\\_____________\\/\\\\\\_\\/\\\\\\____________/\\\\\\__/\\\\\\_\\/\\\\\\_______\\/\\\\\\_\\/\\\\\\_______\\/\\\\\\__\\//\\\\\\______\\///__     \s
                   _\\//\\\\\\____/\\\\\\____/\\\\\\__\\/\\\\\\___________\\//\\\\\\/\\\\\\__\\/\\\\\\\\\\\\\\\\\\\\\\\\\\/__\\/\\\\\\\\\\\\\\\\\\\\\\\\\\/____\\////\\\\\\_________    \s
                    __\\//\\\\\\__/\\\\\\\\\\__/\\\\\\___\\/\\\\\\\\\\\\\\\\\\\\_____\\//\\\\\\\\\\___\\/\\\\\\/////////____\\/\\\\\\/////////_________\\////\\\\\\______   \s
                     ___\\//\\\\\\/\\\\\\/\\\\\\/\\\\\\____\\/\\\\\\/////\\\\\\_____\\//\\\\\\____\\/\\\\\\_____________\\/\\\\\\_____________________\\////\\\\\\___  \s
                      ____\\//\\\\\\\\\\\\//\\\\\\\\\\_____\\/\\\\\\___\\/\\\\\\__/\\\\_/\\\\\\_____\\/\\\\\\_____________\\/\\\\\\______________/\\\\\\______\\//\\\\\\__ \s
                       _____\\//\\\\\\__\\//\\\\\\______\\/\\\\\\___\\/\\\\\\_\\//\\\\\\\\/______\\/\\\\\\_____________\\/\\\\\\_____________\\///\\\\\\\\\\\\\\\\\\\\\\/___\s
                        ______\\///____\\///_______\\///____\\///___\\////________\\///______________\\///________________\\///////////_____
                """);

        this.runClass = runClass;

        //启动容器管理器
        containerManage = new ContainerManage(runClass);
    }

    public static void run(Class<?> runClass) {
        try {
            WhyPPSApplication app = new WhyPPSApplication(runClass);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
