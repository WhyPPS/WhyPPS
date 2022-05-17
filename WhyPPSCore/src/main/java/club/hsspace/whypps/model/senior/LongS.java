package club.hsspace.whypps.model.senior;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: LongS
 * @CreateTime: 2022/5/1
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class LongS extends SeniorBaseModel{

    public String api;

    public JSONObject data;

    public int timeout;

    private LongS() {

    }

    public static LongS of(String requestId, String api, JSONObject data, int timeout){
        LongS longS = new LongS();
        longS.requestId = requestId;
        longS.time = System.currentTimeMillis();
        longS.api = api;
        longS.data = data;
        longS.timeout = timeout;
        return longS;
    }

}
