package club.hsspace.whypps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: ArgsManage
 * @CreateTime: 2022/6/30
 * @Comment: Args识别器
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class ArgsManage {

    private static final Logger logger = LoggerFactory.getLogger(ArgsManage.class);

    private Map<String, String> maps = new HashMap<>();

    public ArgsManage(String[] args) {
        for (String arg : args) {
            if(arg.indexOf(':') != -1) {
                String[] split = arg.split(":");
                maps.put(split[0], split[1]);
            }
        }
    }

    public String getArgs(String key) {
        return maps.get(key);
    }

}
