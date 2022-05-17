package club.hsspace.whypps.model.senior;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: DataR
 * @CreateTime: 2022/4/26
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class DataR extends SeniorBaseModel {

    private static final Logger logger = LoggerFactory.getLogger(DataR.class);

    @JSONField(serializeUsing = CodeFilter.class, deserializeUsing = CodeFilter.class)
    public Code code;

    public String msg;

    public JSONObject data;

    private DataR() {

    }

    public static DataR of(String requestId, Code code, JSONObject data) {
        DataR dataR = new DataR();
        dataR.requestId = requestId;
        dataR.code = code;
        dataR.msg = code.msg();
        dataR.time = System.currentTimeMillis();
        dataR.data = data;
        return dataR;
    }

}
