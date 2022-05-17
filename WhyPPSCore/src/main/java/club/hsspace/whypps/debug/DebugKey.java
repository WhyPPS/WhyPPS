package club.hsspace.whypps.debug;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName: DebugKey
 * @CreateTime: 2022-3-9
 * @Comment: debug口令测试注解，注解到用户启动类中，避免繁琐的输入口令
 *
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DebugKey {

    String password();

}
