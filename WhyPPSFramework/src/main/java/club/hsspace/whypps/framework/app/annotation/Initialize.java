package club.hsspace.whypps.framework.app.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName: Initialize
 * @CreateTime: 2022/7/11
 * @Comment: 初始化类
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Initialize {

    /**
     * 初始化顺序(类内)
     * @return
     */
    int sort() default 0;

}
