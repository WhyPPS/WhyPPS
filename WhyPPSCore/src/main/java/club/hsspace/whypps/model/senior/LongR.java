package club.hsspace.whypps.model.senior;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: LongR
 * @CreateTime: 2022/5/1
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class LongR extends SeniorBaseModel{

    @JSONField(serializeUsing = CodeFilter.class, deserializeUsing = CodeFilter.class)
    public Code code;

    public String msg;

    public JSONObject data;

    private LongR() {

    }

    public static LongR of(String requestId, Code code, JSONObject data) {
        LongR longR = new LongR();
        longR.requestId = requestId;
        longR.time = System.currentTimeMillis();
        longR.code = code;
        longR.msg = code.msg();
        longR.data = data;
        return longR;
    }

}
