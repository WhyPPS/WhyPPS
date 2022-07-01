package club.hsspace.whypps.framework.app.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName: ApiRadioMsg
 * @CreateTime: 2022/6/14
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiSwapMsg {

    String value();

}
