package test;

import club.hsspace.whypps.listener.DataLabel;
import club.hsspace.whypps.model.DataLink;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @ClassName: DataLinkTest
 * @CreateTime: 2022/4/26
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class DataLinkTest {

    private static final Logger logger = LoggerFactory.getLogger(DataLinkTest.class);

    /**
     * 生成方法 单元测试(不带附加数据)
     */
    @Test
    public void testGenerateDataLink() {
        DataLink dataLink = new DataLink(("DATA_R\n" +
                "{\"requestId\":\"536E9CAD\",\"code\":10000,\"msg\":\"请求成功\",\"time\":1646450375797,\"data\":{\"userId\":902,\"userName\":\"PPS\",\"userAres\":\"CN\"}}\n" +
                "abc").getBytes(StandardCharsets.UTF_8));
        assertEquals(dataLink.getDataLabel(), DataLabel.DATA_R);
        //assertEquals(dataLink.getData().getString("requestId"), "536E9CAD");
        assertArrayEquals(dataLink.getExtraData(), "abc".getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成方法 单元测试(带附加数据)
     */
    @Test
    public void testGenerateDataLinkWithExtraData() {
        DataLink dataLink = new DataLink(("DATA_R\n" +
                "{\"requestId\":\"536E9CAD\",\"code\":10000,\"msg\":\"请求成功\",\"time\":1646450375797,\"data\":{\"userId\":902,\"userName\":\"PPS\",\"userAres\":\"CN\"}}\n").getBytes(StandardCharsets.UTF_8));
        assertEquals(dataLink.getDataLabel(), DataLabel.DATA_R);
        //assertEquals(dataLink.getData().getString("requestId"), "536E9CAD");
        assertArrayEquals(dataLink.getExtraData(), "".getBytes(StandardCharsets.UTF_8));
    }

}
