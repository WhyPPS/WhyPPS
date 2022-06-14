package club.hsspace.whypps.model.senior;

import club.hsspace.whypps.model.RadioMsg;
import com.alibaba.fastjson.JSONObject;

/**
 * @ClassName: RadioS
 * @CreateTime: 2022/6/12
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class RadioS extends RadioMsg {

    private RadioS() {

    }

    public static RadioS of(String api, JSONObject data, long sendTime, String requestId, int range, boolean blacklist, String[] list) {
        RadioS radioS = new RadioS();
        radioS.api = api;
        radioS.data = data;
        radioS.time = sendTime;
        radioS.requestId = requestId;
        radioS.range = range;
        radioS.blacklist = blacklist;
        radioS.list = list;
        return radioS;
    }

}
