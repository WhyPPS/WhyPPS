package club.hsspace.whypps.model;

import club.hsspace.whypps.model.senior.SeniorBaseModel;
import com.alibaba.fastjson.JSONObject;

/**
 * @ClassName: RadioMsg
 * @CreateTime: 2022/6/12
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class RadioMsg extends SeniorBaseModel {

    public String api;

    public JSONObject data;

    public int range;

    public boolean blacklist;

    public String[] list;

    public static RadioMsg of(String api, JSONObject data, long sendTime, String requestId, int range, boolean blacklist, String[] list) {
        RadioMsg radioMsg = new RadioMsg();
        radioMsg.api = api;
        radioMsg.data = data;
        radioMsg.time = sendTime;
        radioMsg.requestId = requestId;
        radioMsg.range = range;
        radioMsg.blacklist = blacklist;
        radioMsg.list = list;
        return radioMsg;
    }



}
