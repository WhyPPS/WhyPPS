package club.hsspace.whypps.framework.plugin;

import club.hsspace.whypps.action.Init;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @ClassName: PluginControl
 * @CreateTime: 2022/7/13
 * @Comment: 插件空间管理控制器
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class PluginControl {

    private static final Logger logger = LoggerFactory.getLogger(PluginControl.class);

    private File pluginPath;

    public PluginControl(File pluginPath) {
        this.pluginPath = pluginPath;
    }

    @Init
    public void initPlugin() {

    }
}
