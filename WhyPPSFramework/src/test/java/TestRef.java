import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @ClassName: TestRef
 * @CreateTime: 2022/7/11
 * @Comment: 测试反射
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class TestRef implements Comparable<TestRef>{

    private static final Logger logger = LoggerFactory.getLogger(TestRef.class);

    @Test
    public void testMethod() {
        Class<TestRef> testRefClass = TestRef.class;
        Method[] methods = testRefClass.getMethods();

        System.out.println();
    }

    @Override
    public int compareTo(TestRef o) {
        return 0;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
