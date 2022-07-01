package club.hsspace.whypps;

import club.hsspace.whypps.framework.app.annotation.*;
import club.hsspace.whypps.manage.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: TestSpreadApp
 * @CreateTime: 2022/6/15
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@AppInterface
public class TestSpreadApp {

    private static final Logger logger = LoggerFactory.getLogger(TestSpreadApp.class);

    @ApiRadioMsg("/radio")
    public void radio(@DataParam("msg") String msg) {
        System.out.println(msg);
    }

    @ApiSwapMsg("/swap")
    public String swap(Authentication authentication) {
        return authentication.getLocalCertificate().getSign();
    }

}
