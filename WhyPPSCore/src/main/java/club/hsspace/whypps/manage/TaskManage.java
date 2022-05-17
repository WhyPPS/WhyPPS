package club.hsspace.whypps.manage;

import club.hsspace.whypps.action.Container;
import club.hsspace.whypps.action.Init;
import club.hsspace.whypps.model.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassName: TaskManage
 * @CreateTime: 2022/3/13
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@Container(sort = -80)
public class TaskManage {

    private static final Logger logger = LoggerFactory.getLogger(TaskManage.class);

    private ExecutorService es;

    private TaskManage() {

    }

    @Init
    private void initManage() {
        int threadNum = Runtime.getRuntime().availableProcessors() * 2;
        es = Executors.newFixedThreadPool(threadNum);
        logger.info("初始化任务队列管理器成功，队列最大大小：{}", threadNum);
    }

    public void submit(Runnable run) {
        es.submit(run);
    }

    public<T> void submit(Callback<T> run, T t) {
        es.submit(() -> run.run(t));
    }

}
