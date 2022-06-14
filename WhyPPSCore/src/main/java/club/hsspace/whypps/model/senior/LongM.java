package club.hsspace.whypps.model.senior;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: LongM
 * @CreateTime: 2022/5/1
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class LongM extends SeniorBaseModel{

    public int count;

    private LongM() {

    }

    public static LongM of(String requestId, int count) {
        LongM longM = new LongM();
        longM.requestId = requestId;
        longM.time = System.currentTimeMillis();
        longM.count = count;
        return longM;
    }

}
