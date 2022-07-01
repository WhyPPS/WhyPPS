package club.hsspace.whypps.model.senior;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * @ClassName: SwapR
 * @CreateTime: 2022/6/12
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class SwapR extends SeniorBaseModel{

    @JSONField(serializeUsing = CodeFilter.class, deserializeUsing = CodeFilter.class)
    public Code code;

    public JSONArray data;

    public int involve;

    public static SwapR of(String requestId, Code code, JSONArray data, int involve) {
        SwapR swapR = new SwapR();
        swapR.code = code;
        swapR.requestId = requestId;
        swapR.time = System.currentTimeMillis();
        swapR.data = data;
        swapR.involve = involve;
        return swapR;
    }

}
