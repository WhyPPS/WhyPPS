package club.hsspace.whypps.framework.app.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName: Interceptor
 * @CreateTime: 2022/7/16
 * @Comment: 拦截器
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Interceptor {

    int sort();

    RequestEnum[] type() default {RequestEnum.DATA, RequestEnum.BIN, RequestEnum.HEART,
            RequestEnum.LONG, RequestEnum.SWAP, RequestEnum.RADIO};

    boolean regexMode() default false;

    String regex() default "";

    String[] list() default {};

    boolean whiteListMode() default true;

}
