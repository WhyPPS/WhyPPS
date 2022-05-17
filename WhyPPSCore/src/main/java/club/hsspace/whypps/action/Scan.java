package club.hsspace.whypps.action;

import java.lang.annotation.*;

/**
 * @ClassName: Scan
 * @CreateTime: 2022/3/13
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Scans.class)
public @interface Scan {

    String value();

}
