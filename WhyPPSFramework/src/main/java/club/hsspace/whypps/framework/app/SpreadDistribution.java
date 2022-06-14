package club.hsspace.whypps.framework.app;

import club.hsspace.whypps.handle.DataStream;
import club.hsspace.whypps.model.RadioMsg;
import club.hsspace.whypps.model.senior.Radio;
import club.hsspace.whypps.model.senior.SwapR;
import club.hsspace.whypps.model.senior.SwapS;
import club.hsspace.whypps.processor.impl.SpreadProcessorImpl;
import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: SpreadDistribution
 * @CreateTime: 2022/6/14
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class SpreadDistribution extends SpreadProcessorImpl {

    private static final Logger logger = LoggerFactory.getLogger(SpreadDistribution.class);

    private MethodController methodController;

    public void injection(MethodController methodController) {
        this.methodController = methodController;
    }

    @Override
    public void listenerRadioMsg(DataStream dataStream, Radio radio, RadioMsg radioMsg) {
        super.listenerRadioMsg(dataStream, radio, radioMsg);
    }

    @Override
    public SwapS listenerSwapMsg(DataStream dataStream, SwapS swapS) {
        return super.listenerSwapMsg(dataStream, swapS);
    }

    @Override
    public SwapR returnSwapMsg(DataStream dataStream, JSONArray data, SwapS swapS, int involve) {
        return super.returnSwapMsg(dataStream, data, swapS, involve);
    }
}
