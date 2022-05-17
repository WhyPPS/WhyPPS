package club.hsspace.whypps.model.senior;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: BinS
 * @CreateTime: 2022/5/1
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class BinS extends SeniorBaseModel {

    public String api;

    public JSONObject data;

    public boolean bin;

    public String MD5;

    private BinS() {

    }

    public static BinS of(String requestId, String api, JSONObject data, boolean bin, String MD5) {
        BinS binS = new BinS();
        binS.requestId = requestId;
        binS.api = api;
        binS.time = System.currentTimeMillis();
        binS.data = data;
        binS.bin = bin;
        binS.MD5 = MD5;
        if(binS.MD5 == null)
            binS.MD5 = "";
        return binS;
    }

}
