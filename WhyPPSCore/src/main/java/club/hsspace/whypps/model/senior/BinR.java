package club.hsspace.whypps.model.senior;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: BinR
 * @CreateTime: 2022/5/1
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class BinR extends SeniorBaseModel{

    @JSONField(serializeUsing = CodeFilter.class, deserializeUsing = CodeFilter.class)
    public Code code;

    public String msg;

    public JSONObject data;

    public boolean bin;

    public String MD5;

    private BinR() {

    }

    public static BinR of(String requestId, Code code, JSONObject data, boolean bin, String md5) {
        BinR binR = new BinR();
        binR.requestId = requestId;
        binR.code = code;
        binR.msg = code.msg();
        binR.data = data;
        binR.bin = bin;
        binR.MD5 = md5;
        binR.time = System.currentTimeMillis();
        return binR;
    }

    public static BinR of(String requestId, Code code, JSONObject data) {
        return of(requestId, code, data, false, null);
    }

    public static BinR of(String requestId, Code code) {
        return of(requestId, code, null);
    }

}
