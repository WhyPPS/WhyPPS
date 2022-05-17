package club.hsspace.whypps.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: CertificateNotFoundException
 * @CreateTime: 2022/3/12
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class CertificateNotFoundError extends BaseException{

    private static final Logger logger = LoggerFactory.getLogger(CertificateNotFoundError.class);

    public CertificateNotFoundError(String message) {
        super(message);
    }

}
