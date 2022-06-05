package club.hsspace.whypps;

import club.hsspace.whypps.action.Container;
import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.action.Scan;
import club.hsspace.whypps.handle.DataStream;
import club.hsspace.whypps.handle.EquityHandle;
import club.hsspace.whypps.handle.LongMsgStream;
import club.hsspace.whypps.handle.TcpHandle;
import club.hsspace.whypps.manage.Authentication;
import club.hsspace.whypps.manage.LocalHost;
import club.hsspace.whypps.model.Certificate;
import club.hsspace.whypps.model.senior.LongR;
import club.hsspace.whypps.run.WhyPPSApplication;
import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Scan("club.hsspace.whypps.PeerMain")
@Container(register = false)
public class PeerMain {

    @Injection
    private static LocalHost localHost;

    @Injection
    private static Authentication authentication;

    public static void main(String[] args) throws IOException{

        WhyPPSApplication.run(PeerMain.class);
        TcpHandle connect = localHost.connect("127.0.0.1", 2683);

        Certificate ce = connect.applyCertificate(authentication.getCertificate("C00E57ED610D693098015A2241C4D24B"), null);
        System.out.println("申请证书完成");

        DataStream ds = connect.buildConnection(authentication.getCertificate("C00E57ED610D693098015A2241C4D24B"));
        System.out.println("建立连接完成");

        EquityHandle equityHandle = ds.getEquityHandle();

        equityHandle.sendData(dataR -> {
            System.out.println(dataR.toJSON());
        }, "/getUserMsg", JSON.parseObject("{\"name\":\"hanshuo\"}"));

        equityHandle.sendBin(binRDataLink -> {
            System.out.println(binRDataLink.getData().toJSON());
        }, "/videoStream", null, null);

        equityHandle.sendHeart(heart -> {
            System.out.println("ping: "+heart);
        });

        equityHandle.sendLong(op -> {
            LongR key = op.key();
            System.out.println(key.toJSON());
            LongMsgStream value = op.value();
        }, "long", null, 5000);
    }

    public static List<String> getCertificates() {
        return List.of("/whypps/Certificate.ce");
    }

    public static String getDefaultConfiguration() {
        return "/whypps/client.properties";
    }

}
