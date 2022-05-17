package club.hsspace.whypps.model;

/**
 * @ClassName: Callback
 * @CreateTime: 2022/3/13
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@FunctionalInterface
public interface CallbackDouble<T, K> {

    void run(T t, K k);

}
