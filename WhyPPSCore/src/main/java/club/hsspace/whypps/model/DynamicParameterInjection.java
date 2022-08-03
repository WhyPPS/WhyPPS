package club.hsspace.whypps.model;

import java.lang.reflect.Method;

public interface DynamicParameterInjection {

    Object injection(Method method);

}
