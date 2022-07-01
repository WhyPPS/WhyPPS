package club.hsspace.whypps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: WhyPPSFrameworkNotFoundException
 * @CreateTime: 2022/6/30
 * @Comment: 未找到框架异常
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class WhyPPSFrameworkNotFoundException extends Exception{

    private static final Logger logger = LoggerFactory.getLogger(WhyPPSFrameworkNotFoundException.class);

    public WhyPPSFrameworkNotFoundException(String message) {
        super(message);
    }
}
