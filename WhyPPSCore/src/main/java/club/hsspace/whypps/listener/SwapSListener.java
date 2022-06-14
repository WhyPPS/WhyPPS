package club.hsspace.whypps.listener;

import club.hsspace.whypps.action.Container;
import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.action.nongeneral.MsgListener;
import club.hsspace.whypps.handle.DataStream;
import club.hsspace.whypps.handle.SpreadHandle;
import club.hsspace.whypps.manage.FrameManage;
import club.hsspace.whypps.model.DataLink;
import club.hsspace.whypps.model.senior.SwapR;
import club.hsspace.whypps.model.senior.SwapS;
import club.hsspace.whypps.processor.SpreadProcessor;
import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * @ClassName: SwapSListener
 * @CreateTime: 2022/6/14
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@Container
@MsgListener(DataLabel.SWAP_S)
public class SwapSListener implements LinkListener {

    private static final Logger logger = LoggerFactory.getLogger(SwapSListener.class);

    @Injection
    private SpreadProcessor spreadProcessor;

    @Injection
    private FrameManage frameManage;

    @Override
    public boolean listener(DataStream dataStream, DataLink dataLink) {
        SwapS swapS = (SwapS) dataLink.getData();
        SwapS send = spreadProcessor.listenerSwapMsg(dataStream, swapS);
        JSONArray jsonArray = new JSONArray();
        int involve = 1;
        if (send != null) {
            Collection<DataStream> allDataStream = frameManage.getAllDataStream();
            for (DataStream stream : allDataStream) {
                SpreadHandle spreadHandle = stream.getSpreadHandle();
                SwapR swapR = spreadHandle.sendSwap(send);
                boolean col = spreadProcessor.summarySwapMsg(jsonArray, swapR);
                if(col)
                    involve += swapR.involve;
            }
        }
        SwapR result = spreadProcessor.returnSwapMsg(dataStream, jsonArray, swapS, involve);
        DataLink<SwapR> swapRDataLink = new DataLink<>(DataLabel.SWAP_R, result, null);
        dataStream.sendData(swapRDataLink);

        return true;
    }
}
