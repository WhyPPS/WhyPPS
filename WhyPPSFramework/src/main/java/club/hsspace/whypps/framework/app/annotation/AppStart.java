package club.hsspace.whypps.framework.app.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName: AppStart
 * @CreateTime: 2022/7/11
 * @Comment: 应用启动方法(注意这是非必须的)
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AppStart {

    boolean thread() default false;

    int sort() default 0;

}
