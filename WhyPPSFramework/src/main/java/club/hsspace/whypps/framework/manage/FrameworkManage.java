package club.hsspace.whypps.framework.manage;


import club.hsspace.whypps.framework.app.ApiJarManage;
import club.hsspace.whypps.manage.ContainerManage;
import club.hsspace.whypps.run.WhyPPSApplication;
import club.hsspace.whypps.framework.manage.XMLConfiguration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @ClassName: FrameworkManage
 * @CreateTime: 2022/6/5
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class FrameworkManage {

    private static final Logger logger = LoggerFactory.getLogger(FrameworkManage.class);


    private WhyPPSApplication whyPPSApplication;

    private ContainerManage containerManage;

    private XMLConfiguration xmlConfiguration;

    public FrameworkManage() throws InvocationTargetException, ParserConfigurationException, IOException, SAXException {

        //相对运行路径文件工具类初始化
        FileManage fileManage = new FileManage();

        //配置文件管理类初始化
        xmlConfiguration = new XMLConfiguration(fileManage);

        this.whyPPSApplication = WhyPPSApplication.run(this);

        this.containerManage = whyPPSApplication.getContainerManage();

        containerManage.registerObject(xmlConfiguration);
        containerManage.registerObject(fileManage);

        //应用管理器(app)
        ApiJarManage apiJarManage = new ApiJarManage();
        containerManage.registerObject(apiJarManage);
        containerManage.injection(apiJarManage);

        containerManage.injection(this);
    }

    public ContainerManage getContainerManage() {
        return containerManage;
    }

    public Properties getDefaultConfiguration() throws IOException {
        Properties prop = new Properties();
        byte[] bytes = Files.readAllBytes(Path.of(xmlConfiguration.getString(Configuration.AUTHORITY_PRIVATEKEY_PASSWORD)));
        prop.put("security.key", new String(bytes));
        prop.put("security.pbekey", xmlConfiguration.getString(Configuration.AUTHORITY_PRIVATEKEY_CIPHER));
        prop.put("network.local.autoDistribution", xmlConfiguration.getString(Configuration.LOCALSERVER_SERVER_AUTODISTRIBUTION));
        prop.put("network.local.port", xmlConfiguration.getString(Configuration.LOCALSERVER_SERVER_PORT));
        prop.put("certificate.effectiveTime", xmlConfiguration.getString(Configuration.AUTHORITY_SETTING_EFFECTIVETIME));
        prop.put("debug.debugkey", xmlConfiguration.getString(Configuration.DEBUG_DEBUGKEY));

        return prop;
    }

    public List<String> getCertificates() {

        List<String> result = new ArrayList<>();
        for (String string : xmlConfiguration.getStrings(Configuration.CERTIFICATES_DIRPATH)) {
            String[] list = new File(string).list();
            List<String> collect = Arrays.stream(list)
                    .map(n -> "fi:" + string + "\\" + n)
                    .collect(Collectors.toList());
            result.addAll(collect);
        }

        return result;
    }

}
