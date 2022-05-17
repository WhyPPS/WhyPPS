package test;

import club.hsspace.whypps.model.senior.Code;
import club.hsspace.whypps.model.senior.DataR;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: EquityModelTest
 * @CreateTime: 2022/4/26
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */

public class EquityModelTest {

    private static final Logger logger = LoggerFactory.getLogger(EquityModelTest.class);

    @Test
    public void testDataR() {
        DataR dataR = DataR.of("A1B2", Code.OK, null);
        System.out.println(JSON.toJSONString(dataR));

        dataR = JSON.parseObject("{\"code\":10000,\"msg\":\"请求成功\",\"requestId\":\"A1B2\",\"time\":123}", DataR.class);
        System.out.println();
    }

}
