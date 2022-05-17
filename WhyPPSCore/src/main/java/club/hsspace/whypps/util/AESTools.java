package club.hsspace.whypps.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * @ClassName: AESTools
 * @CreateTime: 2022/3/19
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class AESTools {

    private static final Logger logger = LoggerFactory.getLogger(AESTools.class);

    private static KeyGenerator keyGenerator;

    private static Cipher cipher;

    static {
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public static byte[] generateKey() {
        return keyGenerator.generateKey().getEncoded();
    }

    public static byte[] encrypt(byte[] key, byte[] contain) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Key convertSecretKey = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, convertSecretKey);
        byte[] enBytes = cipher.doFinal(contain);
        return enBytes;
    }

    public static byte[] decrypt(byte[] key, byte[] contain) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Key convertSecretKey = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, convertSecretKey);
        byte[] deBytes = cipher.doFinal(contain);
        return deBytes;
    }

}
