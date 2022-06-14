import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @ClassName: TestJarLoader
 * @CreateTime: 2022/6/6
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class TestAppJarLoader {

    private static final Logger logger = LoggerFactory.getLogger(TestAppJarLoader.class);

    @Test
    public void test() throws NoSuchMethodException {

        Method oM = Object.class.getMethod("hashCode");

        Method tM = TestClass.class.getMethod("hashCode");

        Method jM = TestAppJarLoader.class.getMethod("hashCode");

        assertEquals(oM, tM);
        assertNotEquals(oM, jM);
    }

    public static class TestClass {

    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
