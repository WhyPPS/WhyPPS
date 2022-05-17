package club.hsspace.whypps.action.nongeneral;

import club.hsspace.whypps.listener.DataLabel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName: MsgListener
 * @CreateTime: 2022/4/25
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MsgListener {

    DataLabel[] value();

}
