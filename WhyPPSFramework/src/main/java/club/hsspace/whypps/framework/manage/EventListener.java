package club.hsspace.whypps.framework.manage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName: EnentListener
 * @CreateTime: 2022/7/14
 * @Comment: 事件监听器注解
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventListener {

    int sort() default 0;

    boolean autoRegister() default true;

}
