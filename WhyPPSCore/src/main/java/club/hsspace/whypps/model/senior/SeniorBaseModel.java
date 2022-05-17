package club.hsspace.whypps.model.senior;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.nio.charset.StandardCharsets;

/**
 * @ClassName: EquityBaseModel
 * @CreateTime: 2022/4/26
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public abstract class SeniorBaseModel {

    public String requestId;

    public long time;

    protected SeniorBaseModel() {

    }

    public byte[] toBytes() {
        return JSON.toJSONString(this).getBytes(StandardCharsets.UTF_8);
    }

    public String toJSON() {
        return JSON.toJSONString(this);
    }

}
