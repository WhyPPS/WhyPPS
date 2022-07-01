package club.hsspace.whypps;

import club.hsspace.whypps.framework.app.annotation.*;
import club.hsspace.whypps.handle.LongMsgStream;
import club.hsspace.whypps.model.senior.Code;
import club.hsspace.whypps.util.NumberTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @ClassName: TestApp
 * @CreateTime: 2022/6/6
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@AppInterface
public class TestEquityApp {

    private static final Logger logger = LoggerFactory.getLogger(TestEquityApp.class);

    @ApiDataMsg("/getUserMsg")
    public boolean getUserMsg(@DataParam("name") String name) {
        System.out.println(name);
        return true;
    }

    @ApiDataMsg("/login")
    public Code login(@DataParam("user") String user, @DataParam("psw") String psw) {
        //查询数据库取得密码
        String password = "123456";

        if(password.equals(psw)) {
            return Code.OK;
        } else {
            return Code.of(21041, "密码错误");
        }
    }

    @ApiBinMsg("/binTest")
    public byte[] binTest(byte[] bin) {
        System.out.println(NumberTools.bytes2HexString(bin));

        return new byte[]{1,2,3,4,5,6};
    }

    @ApiLongMsg("/longTest")
    public boolean longTest(LongMsgStream lms) {
        new Thread(() -> {
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(lms.getInputStream()));
            String string;
            try {
                while ((string = inputStream.readLine()).length() != 0) {
                    byte[] read = string.getBytes(StandardCharsets.UTF_8);
                    for (int i = 0; i < read.length; i++) {
                        read[i] ++;
                    }
                    Thread.sleep(1000);
                    lms.sendData(new String(read) + "\n");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        return true;

    }

}
