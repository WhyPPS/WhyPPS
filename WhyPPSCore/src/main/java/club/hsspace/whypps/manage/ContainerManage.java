package club.hsspace.whypps.manage;

import club.hsspace.whypps.action.*;
import club.hsspace.whypps.processor.ContainerProcessor;
import club.hsspace.whypps.util.ClassScanner;
import club.hsspace.whypps.util.NumberTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * @ClassName: Container
 * @CreateTime: 2022/3/8
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class ContainerManage {

    private static final Logger logger = LoggerFactory.getLogger(ContainerManage.class);

    private Map<Class<?>, Object> containerClass = new HashMap<>();

    private Map<String, Object> containerName = new HashMap<>();

    private Class<?> runClass;

    public ContainerManage(Class<?> runClass) throws InvocationTargetException {
        this.runClass = runClass;
        registerObject("runClass", runClass);

        containerClass.put(ContainerManage.class, this);

        injection(this);
    }

    public ContainerManage(Object runObject) throws InvocationTargetException {
        this.runClass = runObject.getClass();
        registerObject("runClass", runClass);
        registerObject("runObject", runObject);
        registerObject(runObject);

        containerClass.put(ContainerManage.class, this);

        injection(this);
    }

    @Init
    public void initContainer() throws InvocationTargetException {
        //TODO: 内置manage 注解抽象工作
        List<Class<?>> coreContainer = Stream.concat(ClassScanner.scannerClass("club.hsspace.whypps.manage.*").stream(),
                        ClassScanner.scannerClass("club.hsspace.whypps.listener.*").stream())
                .filter(n -> n.getAnnotation(Container.class)!=null)
                .sorted(Comparator.comparingInt(n -> n.getAnnotation(Container.class).sort()))
                .toList();
        //List.of(ProcessorManage.class, TaskManage.class, Configuration.class, Authentication.class, LocalHost.class);

        List<Object> initList = new ArrayList<>();

        for (Class<?> aClass : coreContainer) {
            Object object = createObject(aClass);
            initList.add(object);
            registerObject(object);
        }

        for (Object o : initList) {
            injection(o);
        }
    }

    @Init(sort = 10)
    public void initCustomScans(ContainerProcessor containerProcessor) {
        for (Class<?> aClass : containerProcessor.searchCustomContainer()) {
            containerProcessor.initContainer(aClass);
        }
    }

    public void registerObject(Object o) {
        containerClass.put(o.getClass(), o);
    }

    public void registerObject(Class<?> clazz, Object o) {
        containerClass.put(clazz, o);
    }

    public void registerObject(String name, Object o) {
        containerName.put(name, o);
    }

    public Object getFromName(String name) {
        return containerName.get(name);
    }

    public <T> T getFromClass(Class<T> clazz){
        return (T) containerClass.get(clazz);
    }

    //获取Class类型容器Stream
    public Stream<?> getClassContainer() {
        return containerClass.entrySet().stream().map(n -> n.getValue());
    }

    private <T> T createObject(Class<T> clazz) throws InvocationTargetException {
        T object = null;
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            object = constructor.newInstance();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return object;
    }

    public int injection(Object object) throws InvocationTargetException {
        int result = 0;
        Class<?> aClass = object.getClass();
        //初始化字段
        Field[] fields = aClass.getDeclaredFields();
        for (Field field : fields) {
            Injection annotation = field.getAnnotation(Injection.class);
            if (annotation != null) {
                try {
                    field.setAccessible(true);
                    if (!annotation.name().equals("")) {
                        Object o = containerName.get(annotation.name());
                        if (o != null && o.getClass().isInstance(field.getType())) {
                            field.set(object, o);
                            result++;
                        }
                    } else {
                        Object o = containerClass.get(field.getType());
                        if (o != null) {
                            field.set(object, o);
                            result++;
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        //执行初始化方法
        Method[] methods = aClass.getDeclaredMethods();
        List<Method> ms = Arrays.stream(methods)
                .filter(n -> n.getAnnotation(Init.class) != null)
                .sorted(Comparator.comparingInt(o -> o.getAnnotation(Init.class).sort()))
                .toList();
        try {
            for (Method method : ms) {
                invokeMethod(method, object);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void invokeMethod(Method method, Object obj) throws InvocationTargetException, IllegalAccessException {
        method.setAccessible(true);
        Parameter[] parameters = method.getParameters();
        method.invoke(obj, fillObject(parameters));
    }

    private Object[] fillObject(Parameter[] parameters) {
        Object[] objects = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Injection annotation = parameters[i].getAnnotation(Injection.class);
            if (annotation != null) {
                objects[i] = containerName.get(annotation.name());
            }
            if(objects[i] == null){
                objects[i] = containerClass.get(parameters[i].getType());
            }
        }
        return objects;
    }

}
