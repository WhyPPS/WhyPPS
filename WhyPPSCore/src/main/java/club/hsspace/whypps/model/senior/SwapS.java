package club.hsspace.whypps.model.senior;

import com.alibaba.fastjson.JSONObject;

/**
 * @ClassName: SwapS
 * @CreateTime: 2022/6/12
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class SwapS extends SeniorBaseModel{

    public String api;

    public JSONObject data;

    public int times;

    public int range;

    public boolean wait;

    public int valid;

    public static SwapS of(String api, JSONObject data,  String requestId, int times, int range, boolean wait, int valid) {
        SwapS swapS = new SwapS();
        swapS.api = api;
        swapS.data = data;
        swapS.requestId = requestId;
        swapS.time = System.currentTimeMillis();
        swapS.times = times;
        swapS.range = range;
        swapS.wait = wait;
        swapS.valid = valid;
        return swapS;
    }
}
