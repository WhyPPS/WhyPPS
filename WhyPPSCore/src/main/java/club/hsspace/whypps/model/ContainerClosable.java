package club.hsspace.whypps.model;

import java.io.Closeable;

public interface ContainerClosable extends Closeable {

    default void closeTask() {

    }

}
