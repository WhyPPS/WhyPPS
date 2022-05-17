package test;

import club.hsspace.whypps.manage.Authentication;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @ClassName: GenerateCertificate
 * @CreateTime: 2022/3/9
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class GenerateCertificate {

    /**
     * 测试生成PBE密文文件、PBEKey文件、证书文件
     * @throws GeneralSecurityException
     * @throws IOException
     */
    @Test
    public void generateCertificate() throws GeneralSecurityException, IOException {
        File file = new File("C:\\Users\\Administrator\\Desktop\\key");

        //重置系统输入流，避免手动输入。
        System.setIn(new ByteArrayInputStream("admin\n".getBytes()));
        Authentication.generateAuthorityCertificate(file);

        assertTrue(new File(file, "PBEKey.key").exists());
        assertTrue(new File(file, "RSAPrivateCiphertext.txt").exists());
        assertTrue(new File(file, "Certificate.ce").exists());
    }

}
