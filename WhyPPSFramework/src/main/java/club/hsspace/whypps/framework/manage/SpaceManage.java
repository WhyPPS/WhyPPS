package club.hsspace.whypps.framework.manage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: SpaceManage
 * @CreateTime: 2022/7/17
 * @Comment: 运行空间管理器
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class SpaceManage {

    private static final Logger logger = LoggerFactory.getLogger(SpaceManage.class);

    private Map<ClassLoader, RunningSpace> appSpaceMap = new HashMap<>();

    private Map<String, RunningSpace> pluginSpaceMap = new HashMap<>();

    public void registerRunningSpace(ClassLoader classLoader, RunningSpace runningSpace) {
        appSpaceMap.put(classLoader, runningSpace);
    }

    public void registerRunningSpace(String name, RunningSpace runningSpace) {
        pluginSpaceMap.put(name, runningSpace);
    }

    public RunningSpace getRunningSpace(ClassLoader classLoader){
        return appSpaceMap.get(classLoader);
    }

    public RunningSpace getRunningSpace(String name){
        return pluginSpaceMap.get(name);
    }

}
