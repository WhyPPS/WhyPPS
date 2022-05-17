package club.hsspace.whypps.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: NotLocalCertificate
 * @CreateTime: 2022/5/4
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class NotLocalCertificate extends RunTimeBaseException{

    private static final Logger logger = LoggerFactory.getLogger(NotLocalCertificate.class);

    public NotLocalCertificate(String message) {
        super(message);
    }

}
