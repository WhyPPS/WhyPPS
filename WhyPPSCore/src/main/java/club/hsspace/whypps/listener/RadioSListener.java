package club.hsspace.whypps.listener;

import club.hsspace.whypps.action.Container;
import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.action.nongeneral.MsgListener;
import club.hsspace.whypps.handle.DataStream;
import club.hsspace.whypps.manage.Authentication;
import club.hsspace.whypps.model.DataLink;
import club.hsspace.whypps.model.senior.Code;
import club.hsspace.whypps.model.senior.RadioR;
import club.hsspace.whypps.model.senior.RadioS;
import club.hsspace.whypps.model.senior.SeniorBaseModel;
import club.hsspace.whypps.util.CiphertextTools;
import club.hsspace.whypps.util.NumberTools;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

/**
 * @ClassName: RadioSListener
 * @CreateTime: 2022/6/12
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@Container
@MsgListener(DataLabel.RADIO_S)
public class RadioSListener implements LinkListener {

    private static final Logger logger = LoggerFactory.getLogger(RadioSListener.class);

    @Injection
    private Authentication authentication;

    @Override
    public boolean listener(DataStream dataStream, DataLink dataLink) {
        RadioS radioS = (RadioS) dataLink.getData();
        //TODO： 添加处理机处理授权判断验证。验证通过则返回，无权限返回异常处理
        byte[] privateKey = authentication.getPrivateKey();

        try {
            byte[] bytes = CiphertextTools.privateEncryptRSA(privateKey, JSONObject.toJSONString(radioS).getBytes(StandardCharsets.UTF_8));
            dataStream.sendData(new DataLink(DataLabel.RADIO_R, RadioR.of(radioS.requestId, Code.OK), NumberTools.bytesMerger(authentication.getLocalCertificate().getSignBytes(), bytes)));
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

}
