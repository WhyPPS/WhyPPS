package club.hsspace.whypps.processor;

import java.util.List;

public interface ContainerProcessor {

    List<Class<?>> searchCustomContainer();

    <T> T initContainer(Class<T> clazz);

}
