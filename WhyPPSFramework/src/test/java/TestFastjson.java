import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: TestFastjson
 * @CreateTime: 2022/8/1
 * @Comment: 测试fastjson
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class TestFastjson {

    private static final Logger logger = LoggerFactory.getLogger(TestFastjson.class);

    @Test
    public void test() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("aaa", "测试");
        System.out.println(jsonObject.get("aaa"));
    }

}
