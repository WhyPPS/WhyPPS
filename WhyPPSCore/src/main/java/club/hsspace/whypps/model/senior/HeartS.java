package club.hsspace.whypps.model.senior;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: HeartS
 * @CreateTime: 2022/5/1
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class HeartS extends SeniorBaseModel{

    private HeartS() {

    }

    public static HeartS of(String requestId) {
        HeartS heartS = new HeartS();
        heartS.requestId = requestId;
        heartS.time = System.currentTimeMillis();
        return heartS;
    }

}
