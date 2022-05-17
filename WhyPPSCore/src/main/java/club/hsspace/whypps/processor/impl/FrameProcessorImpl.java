package club.hsspace.whypps.processor.impl;

import club.hsspace.whypps.action.Init;
import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.action.nongeneral.MsgListener;
import club.hsspace.whypps.handle.DataStream;
import club.hsspace.whypps.listener.DataLabel;
import club.hsspace.whypps.listener.LinkListener;
import club.hsspace.whypps.manage.ContainerManage;
import club.hsspace.whypps.model.DataFrame;
import club.hsspace.whypps.model.DataLink;
import club.hsspace.whypps.processor.FrameProcessor;
import club.hsspace.whypps.util.NumberTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @ClassName: FrameProcessorImpl
 * @CreateTime: 2022/4/23
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class FrameProcessorImpl implements FrameProcessor {

    private static final Logger logger = LoggerFactory.getLogger(FrameProcessorImpl.class);

    @Override
    public void processorData(DataStream dataStream, DataFrame.DataSign dataSign, byte[] data) {
        if (data != null && data.length != 0) {
            DataLink dataLink = new DataLink(data);
            labelStrategy.get(dataLink.getDataLabel()).listener(dataStream, dataLink);
        }
    }

    private Map<DataLabel, LinkListener> labelStrategy;

    /**
     * 初始化策略工厂模式，针对每个发送数据标签类型
     *
     * @param containerManage
     */
    @Init
    private void initStrategyHandle(ContainerManage containerManage) {
        labelStrategy = new HashMap<>();
        containerManage.getClassContainer()
                .filter(n -> n.getClass().getAnnotation(MsgListener.class) != null)
                .filter(n -> Arrays.asList(n.getClass().getInterfaces()).contains(LinkListener.class))
                .forEach(n -> Arrays.asList(n.getClass().getAnnotation(MsgListener.class).value())
                        .forEach(m -> labelStrategy.put(m, (LinkListener) n)));
    }

    public void setListener(DataLabel dataLabel, LinkListener listener) {
        labelStrategy.put(dataLabel, listener);
    }


}
