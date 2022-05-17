package club.hsspace.whypps.manage;

import club.hsspace.whypps.action.Container;
import club.hsspace.whypps.action.Init;
import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.util.NumberTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * @ClassName: Configuration
 * @CreateTime: 2022/3/8
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@Container(sort = -70)
public class Configuration {

    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    private Properties props;

    private Configuration() {

    }

    @Init
    private void initFile(@Injection(name = "runClass") Class<?> runClass) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method getDefaultPropertiesFile = runClass.getMethod("getDefaultPropertiesFile");
        String file = (String) getDefaultPropertiesFile.invoke(null);

        props = new Properties();
        props.load(getClass().getResourceAsStream(file));

        logger.info("配置管理器注册成功，读取参数{}条", props.size());
    }

    public byte[] getSecurity() {
        return NumberTools.hexString2Bytes(props.getProperty("security.key", ""));
    }

    public byte[] getPBEKey() throws IOException {
        File pbeKey = new File(props.getProperty("security.pbekey"));
        FileInputStream fis = new FileInputStream(pbeKey);
        byte[] bytes = fis.readAllBytes();
        fis.close();
        return bytes;
    }

    public int getPort() {
        return Integer.parseInt(props.getProperty("network.local.port"));
    }

    public boolean autoDistribution() {
        return Boolean.parseBoolean(props.getProperty("network.local.autoDistribution"));
    }

    public long getEffectiveTime() {
        return Long.parseLong(props.getProperty("certificate.effectiveTime", "1800"));
    }

}
