package club.hsspace.whypps.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: BaseException
 * @CreateTime: 2022/3/12
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class RunTimeBaseException extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(RunTimeBaseException.class);

    public RunTimeBaseException() {
        super();
    }

    public RunTimeBaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public RunTimeBaseException(String message) {
        super(message);
    }

    public RunTimeBaseException(Throwable cause) {
        super(cause);
    }

}
