package club.hsspace.whypps.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: NotConstruatorDataLink
 * @CreateTime: 2022/4/25
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class NotConstructorDataLink extends BaseException {

    private static final Logger logger = LoggerFactory.getLogger(NotConstructorDataLink.class);

    public NotConstructorDataLink(String message) {
        super(message);
    }

}
