package club.hsspace.whypps.model;

import club.hsspace.whypps.model.senior.SeniorBaseModel;

/**
 * @ClassName: Callback
 * @CreateTime: 2022/3/13
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@FunctionalInterface
public interface Callback<T> {

    void run(T t);

}
