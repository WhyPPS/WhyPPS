package club.hsspace.whypps.framework.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName: Access
 * @CreateTime: 2022/7/14
 * @Comment: 访问控制接口注解
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Access {

}
