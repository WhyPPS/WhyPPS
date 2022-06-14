package club.hsspace.whypps.model.senior;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @ClassName: RadioR
 * @CreateTime: 2022/6/12
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class RadioR extends SeniorBaseModel{

    @JSONField(serializeUsing = CodeFilter.class, deserializeUsing = CodeFilter.class)
    public Code code;

    private RadioR() {

    }

    public static RadioR of(String requestId, Code code) {
        RadioR radioR = new RadioR();
        radioR.time = System.currentTimeMillis();
        radioR.requestId = requestId;
        radioR.code = code;
        return radioR;
    }

}
