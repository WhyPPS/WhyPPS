package club.hsspace.whypps.processor.impl;

import club.hsspace.whypps.action.Container;
import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.action.Scan;
import club.hsspace.whypps.action.Scans;
import club.hsspace.whypps.manage.ContainerManage;
import club.hsspace.whypps.processor.ContainerProcessor;
import club.hsspace.whypps.util.ClassScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @ClassName: ContainerProcessorImpl
 * @CreateTime: 2022/4/23
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class ContainerProcessorImpl implements ContainerProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ContainerProcessorImpl.class);

    @Injection(name = "runClass")
    private Class<?> runClass;

    @Injection
    private ContainerManage containerManage;

    @Override
    public List<Class<?>> searchCustomContainer() {
        Scans annotation = runClass.getAnnotation(Scans.class);
        if (annotation == null) {
            Scan scan = runClass.getAnnotation(Scan.class);
            if (scan != null) {
                annotation = new Scans() {
                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return Scans.class;
                    }

                    @Override
                    public Scan[] value() {
                        return new Scan[]{scan};
                    }
                };
            } else {
                return List.of();
            }
        }

        Scan[] value = annotation.value();
        List<Class<?>> scansClass = new ArrayList<>();
        for (Scan scan : value) {
            scansClass.addAll(ClassScanner.scannerClass(scan.value()));
        }

        //TODO: 先进行容器加载
        return scansClass.stream()
                .filter(n -> n.getAnnotation(Container.class) != null)
                .sorted(Comparator.comparingInt(n -> n.getAnnotation(Container.class).sort()))
                .toList();
    }

    @Override
    public <T> T initContainer(Class<T> clazz) {
        Container container = clazz.getAnnotation(Container.class);
        T object = null;

        if (container.register()) {
            try {
                Constructor<T> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                object = constructor.newInstance();
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : Arrays.stream(fields).filter(n -> Modifier.isStatic(n.getModifiers())).toList()) {
            Injection injection = field.getAnnotation(Injection.class);
            if (injection != null) {
                try {
                    field.setAccessible(true);
                    if (!injection.name().equals("")) {
                        Object o = containerManage.getFromName(injection.name());
                        if (o != null && o.getClass().isInstance(field.getType())) {
                            field.set(object, o);
                        }
                    } else {
                        Object o = containerManage.getFromClass(field.getType());
                        if (o != null) {
                            field.set(object, o);
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return object;
    }

}
