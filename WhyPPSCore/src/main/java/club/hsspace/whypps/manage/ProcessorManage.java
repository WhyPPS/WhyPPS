package club.hsspace.whypps.manage;

import club.hsspace.whypps.action.Container;
import club.hsspace.whypps.action.Init;
import club.hsspace.whypps.processor.ContainerProcessor;
import club.hsspace.whypps.processor.EquityProcessor;
import club.hsspace.whypps.processor.FrameProcessor;
import club.hsspace.whypps.processor.impl.ContainerProcessorImpl;
import club.hsspace.whypps.processor.impl.EquityProcessorImpl;
import club.hsspace.whypps.processor.impl.FrameProcessorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @ClassName: ProcessorManage
 * @CreateTime: 2022/4/17
 * @Comment: 处理机管理器
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@Container(sort = -40)
public class ProcessorManage {

    private static final Logger logger = LoggerFactory.getLogger(ProcessorManage.class);

    private Map<Class<?>, Class<?>> implementationClass = new HashMap<>();

    private ProcessorManage() {
        implementationClass.put(ContainerProcessor.class, ContainerProcessorImpl.class);
        implementationClass.put(FrameProcessor.class, FrameProcessorImpl.class);
        implementationClass.put(EquityProcessor.class, EquityProcessorImpl.class);
    }

    //TODO: 这里要提供一个非文件读取式的嵌入注册机读取方式

    @Init
    private void getImpl() throws IOException {
        BufferedReader fileInput = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/whypps/club.hsspace.whypps.processor")));
        String line;
        while ((line = fileInput.readLine()) != null) {
            if (!line.trim().startsWith("#") && line.contains(":")) {
                String[] split = line.split(":");
                try {
                    Class<?> processorInterface = Class.forName(split[0].trim());
                    Class<?> processorImpl = Class.forName(split[1].trim());
                    if (processorInterface.isInterface())
                        implementationClass.put(processorInterface, processorImpl);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        fileInput.close();
    }

    @Init(sort = 10)
    private void initObject(ContainerManage containerManage) {
        implementationClass.forEach((k, v) -> {
            try {
                Constructor<?> constructor = v.getConstructor();
                Object o = constructor.newInstance();
                containerManage.registerObject(k, o);
                containerManage.injection(o);
                logger.info("初始化处理机{}完成，实现类{}", k.getName(), v.getName());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

}
