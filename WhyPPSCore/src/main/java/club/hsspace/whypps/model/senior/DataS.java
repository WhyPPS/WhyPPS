package club.hsspace.whypps.model.senior;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: DataS
 * @CreateTime: 2022/4/26
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class DataS extends SeniorBaseModel{

    public String api;

    public JSONObject data;

    private DataS() {

    }

    public static DataS of(String requestId, String api, JSONObject data) {
        DataS dataS = new DataS();
        dataS.requestId = requestId;
        dataS.time = System.currentTimeMillis();
        dataS.api = api;
        dataS.data = data;
        return dataS;
    }

}
