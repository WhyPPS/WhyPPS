package club.hsspace.whypps.listener;

import club.hsspace.whypps.action.Container;
import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.action.nongeneral.MsgListener;
import club.hsspace.whypps.exception.RunTimeBaseException;
import club.hsspace.whypps.handle.DataStream;
import club.hsspace.whypps.manage.Authentication;
import club.hsspace.whypps.manage.FrameManage;
import club.hsspace.whypps.manage.LocalHost;
import club.hsspace.whypps.model.Certificate;
import club.hsspace.whypps.model.DataLink;
import club.hsspace.whypps.model.RadioMsg;
import club.hsspace.whypps.model.senior.Radio;
import club.hsspace.whypps.model.senior.SeniorBaseModel;
import club.hsspace.whypps.processor.SpreadProcessor;
import club.hsspace.whypps.util.CiphertextTools;
import club.hsspace.whypps.util.NumberTools;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;

/**
 * @ClassName: RadioListener
 * @CreateTime: 2022/6/12
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@Container
@MsgListener(DataLabel.RADIO)
public class RadioListener implements LinkListener {

    private static final Logger logger = LoggerFactory.getLogger(RadioListener.class);

    @Injection
    private SpreadProcessor spreadProcessor;

    @Injection
    private Authentication authentication;

    @Injection
    private FrameManage frameManage;

    @Override
    public boolean listener(DataStream dataStream, DataLink dataLink) {
        byte[] extraData = dataLink.getExtraData();
        byte[] cerBytes = Arrays.copyOfRange(extraData, 0, 16);
        Certificate certificate = authentication.getCertificate(cerBytes);
        //TODO：判断是权威服务器的证书
        if (certificate == null) {
            certificate = dataStream.getTcpHandle().getCertificate(cerBytes);
            //TODO: 增加步骤去LocalHost获取证书
            if (certificate == null)
                throw new RunTimeBaseException("证书未获取成功");
        }

        byte[] contain;
        try {
            contain = CiphertextTools.publicDecryptRSASection(certificate.getPublicKey(), Arrays.copyOfRange(extraData, 16, extraData.length));
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }

        Radio radio = (Radio) dataLink.getData(Radio.class);
        RadioMsg radioMsg = JSONObject.toJavaObject(JSON.parseObject(new String(contain)), RadioMsg.class);

        radio.times++;
        radio.time = System.currentTimeMillis();
        //TODO: 增加判断requestId一段时间内(60s)是否重复，重复则不发送
        if (radio.times <= radioMsg.range || radioMsg.range < 0) {
            Collection<DataStream> allDataStream = frameManage.getAllDataStream();
            for (DataStream stream : allDataStream) {
                stream.sendData(dataLink);
            }
        }

        spreadProcessor.listenerRadioMsg(dataStream, radio, radioMsg);

        return true;
    }
}
