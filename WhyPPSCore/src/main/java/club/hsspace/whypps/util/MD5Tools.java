package club.hsspace.whypps.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @ClassName: MD5Tools
 * @CreateTime: 2022/5/4
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class MD5Tools {

    public static byte[] md5(byte[] data) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] enStr = messageDigest.digest(data);
        return enStr;
    }

    public static String md5String(byte[] data) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] enStr = messageDigest.digest(data);
        return NumberTools.bytes2HexString(enStr);
    }

}
