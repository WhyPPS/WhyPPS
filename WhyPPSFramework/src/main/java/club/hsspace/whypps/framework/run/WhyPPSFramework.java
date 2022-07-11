package club.hsspace.whypps.framework.run;

import club.hsspace.whypps.action.Container;
import club.hsspace.whypps.action.Scan;
import club.hsspace.whypps.framework.manage.FrameworkManage;
import club.hsspace.whypps.framework.manage.XMLConfiguration;
import club.hsspace.whypps.manage.ContainerManage;
import club.hsspace.whypps.run.WhyPPSApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Properties;

/**
 * @ClassName: WhyPPSFramwork
 * @CreateTime: 2022/5/7
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@Scan("club.hsspace.whypps.framework.run.WhyPPSFramework")
@Container(register = false)
public class WhyPPSFramework {

    private static final Logger logger = LoggerFactory.getLogger(WhyPPSFramework.class);

    private FrameworkManage frameworkManage;

    private WhyPPSFramework() throws Exception {
        frameworkManage = new FrameworkManage();
    }

    /**
     * WhyPPSFramework启动方法
     * @return
     * @throws InvocationTargetException
     */
    public static WhyPPSFramework run() throws Exception {

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

        WhyPPSFramework whyPPSFramework = new WhyPPSFramework();

        ContainerManage cm = whyPPSFramework.getContainerManage();
        cm.registerObject(whyPPSFramework);

        return whyPPSFramework;
    }

    public ContainerManage getContainerManage() {
        return frameworkManage.getContainerManage();
    }

    public static void main(String[] args) throws Exception {
        WhyPPSFramework.run();
    }

}
