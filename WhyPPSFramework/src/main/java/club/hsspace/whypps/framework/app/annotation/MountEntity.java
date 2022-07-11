package club.hsspace.whypps.framework.app.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName: MountEntity
 * @CreateTime: 2022/7/11
 * @Comment: 挂载实体
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface MountEntity {



}
