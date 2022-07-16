package club.hsspace.whypps.framework.app.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName: ReturnValue
 * @CreateTime: 2022/7/16
 * @Comment: 拦截返回值
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReturnValue {

}
