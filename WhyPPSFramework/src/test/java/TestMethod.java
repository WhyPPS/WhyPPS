import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @ClassName: TestMethod
 * @CreateTime: 2022/6/6
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class TestMethod {

    private static final Logger logger = LoggerFactory.getLogger(TestMethod.class);

    @Test
    public void testMethod() throws NoSuchMethodException {
        Class<TestMethod> clazz = TestMethod.class;
        Method method = clazz.getMethod("test", String.class, int.class);

        Parameter[] parameters = method.getParameters();
        System.out.println(parameters[0].getName());  //arg0
        System.out.println(parameters[1].getName());  //arg1
        // (垃圾设计(偷偷说)，一个参数名又不占多少地方)
    }

    @Test
    public void testClass() {
        System.out.println(Boolean.TRUE instanceof Boolean);
    }

    public void test(String a1, int a2) {

    }

}
