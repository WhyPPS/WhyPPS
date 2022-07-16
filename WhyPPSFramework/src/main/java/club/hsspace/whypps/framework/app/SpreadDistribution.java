package club.hsspace.whypps.framework.app;

import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.framework.app.annotation.RequestEnum;
import club.hsspace.whypps.handle.DataStream;
import club.hsspace.whypps.model.RadioMsg;
import club.hsspace.whypps.model.senior.*;
import club.hsspace.whypps.processor.impl.SpreadProcessorImpl;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @ClassName: SpreadDistribution
 * @CreateTime: 2022/6/14
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class SpreadDistribution extends SpreadProcessorImpl {

    private static final Logger logger = LoggerFactory.getLogger(SpreadDistribution.class);

    @Injection
    private MethodController methodController;

    @Injection
    private InterceptorManage interceptorManage;

    @Override
    public void listenerRadioMsg(DataStream dataStream, Radio radio, RadioMsg radioMsg) {
        MethodController.MethodAndObject mo = methodController.getRadioMethod(radioMsg.api);
        if (mo == null)
            return;

        JSONObject data = radioMsg.data;
        if (data == null) {
            data = new JSONObject();
        }

        try {
            interceptorManage.executeRequest(RequestEnum.RADIO, radioMsg.api, dataStream, data, mo, Map.of(Radio.class, radio, RadioMsg.class, radioMsg));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO: 这里需要提供一种新的API，去重构Swap转发规则(有空再实现，感觉暂时需求场景不多)

    @Override
    public SwapR returnSwapMsg(DataStream dataStream, JSONArray ja, SwapS swapS, int involve) {
        MethodController.MethodAndObject mo = methodController.getSwapMethod(swapS.api);
        if (mo == null)
            return SwapR.of(swapS.requestId, Code.NOT_FOUND, ja, involve);

        JSONObject data = swapS.data;
        if (data == null) {
            data = new JSONObject();
        }

        Object methodReturn;
        try {
            methodReturn = interceptorManage.executeRequest(RequestEnum.BIN, swapS.api, dataStream, data, mo, Map.of(SwapS.class, swapS, JSONArray.class, ja));
        } catch (Exception e) {
            return SwapR.of(swapS.requestId, Code.SERVER_ERROR, ja, involve);
        }

        if (methodReturn == null)
            return SwapR.of(swapS.requestId, Code.REQUEST_FAIL, ja, involve);

        Class<?> returnClass = methodReturn.getClass();
        if (methodReturn instanceof Code code)
            return SwapR.of(swapS.requestId, code, ja, involve);
        else if (methodReturn instanceof SwapR dataR)
            return dataR;
        else if (methodReturn instanceof JSONObject jsonObject) {
            ja.add(jsonObject);
            return SwapR.of(swapS.requestId, Code.OK, ja, involve);
        } else if (methodReturn instanceof Boolean bool)
            return SwapR.of(swapS.requestId, bool ? Code.OK : Code.REQUEST_FAIL, ja, involve);
        else if (methodReturn instanceof Number || returnClass == String.class) {
            JSONObject jo = new JSONObject(Map.of("value", methodReturn));
            ja.add(jo);
            return SwapR.of(swapS.requestId, Code.OK, ja, involve);
        }

        JSONObject jo = (JSONObject) JSONObject.toJSON(methodReturn);
        ja.add(jo);
        return SwapR.of(swapS.requestId, Code.OK, ja, involve);
    }
}
