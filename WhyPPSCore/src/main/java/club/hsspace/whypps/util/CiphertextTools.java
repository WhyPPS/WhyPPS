package club.hsspace.whypps.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

/**
 * @ClassName: CiphertextTools
 * @CreateTime: 2022/3/8
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class CiphertextTools {

    private static final Logger logger = LoggerFactory.getLogger(CiphertextTools.class);

    private static int bouncyCastle = Security.addProvider(new BouncyCastleProvider());

    public static KeyPair generateRSAKeyPair() {
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    // PBE加密:
    public static byte[] encryptPBE(String password, byte[] salt, byte[] input) throws GeneralSecurityException {
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory skeyFactory = SecretKeyFactory.getInstance("PBEwithSHA1and128bitAES-CBC-BC");
        SecretKey skey = skeyFactory.generateSecret(keySpec);
        PBEParameterSpec pbeps = new PBEParameterSpec(salt, 1024);
        Cipher cipher = Cipher.getInstance("PBEwithSHA1and128bitAES-CBC-BC");
        cipher.init(Cipher.ENCRYPT_MODE, skey, pbeps);
        return cipher.doFinal(input);
    }

    // PBE解密:
    public static byte[] decryptPBE(String password, byte[] salt, byte[] input) throws GeneralSecurityException {
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory skeyFactory = SecretKeyFactory.getInstance("PBEwithSHA1and128bitAES-CBC-BC");
        SecretKey skey = skeyFactory.generateSecret(keySpec);
        PBEParameterSpec pbeps = new PBEParameterSpec(salt, 1024);
        Cipher cipher = Cipher.getInstance("PBEwithSHA1and128bitAES-CBC-BC");
        cipher.init(Cipher.DECRYPT_MODE, skey, pbeps);
        return cipher.doFinal(input);
    }

    //RSA私钥加密(字串限长)
    public static byte[] privateEncryptRSA(byte[] privateKeyBytes, byte[] contain) throws GeneralSecurityException {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] result = cipher.doFinal(contain);
        return result;
    }

    //RSA私钥加密不限长，拼接完成
    public static byte[] privateEncryptRSASection(byte[] privateKeyBytes, byte[] contain) throws GeneralSecurityException {
        List<byte[]> bytes = NumberTools.subBytesByCount(contain, 245);
        byte[] result = new byte[256 * bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            byte[] ans = privateEncryptRSA(privateKeyBytes, bytes.get(i));
            NumberTools.bytes2bytes(ans, 0, ans.length, result, i * 256);
        }
        return result;
    }

    //RSA公钥解密(字串限长)
    private static byte[] publicDecryptRSA(byte[] publicKeyBytes, byte[] contain) throws GeneralSecurityException {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Cipher cipher1 = Cipher.getInstance("RSA");
        cipher1.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] result = cipher1.doFinal(contain);
        return result;
    }

    //RSA公钥加密
    public static byte[] publicEncryptRSA(byte[] publicKeyBytes, byte[] contain) throws GeneralSecurityException {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] result = cipher.doFinal(contain);
        return result;
    }

    //RSA私钥解密
    public static byte[] privateDecryptRSA(byte[] privateKeyBytes, byte[] contain) throws GeneralSecurityException {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] result = cipher.doFinal(contain);
        return result;
    }

    //RSA公钥解密
    public static byte[] publicDecryptRSASection(byte[] publicKeyBytes, byte[] contain) throws GeneralSecurityException {
        List<byte[]> bytes = NumberTools.subBytesByCount(contain, 256);
        ArrayList<byte[]> result = new ArrayList<>();
        for (byte[] aByte : bytes) {
            result.add(publicDecryptRSA(publicKeyBytes, aByte));
        }
        return NumberTools.bytesMerger(result);
    }

    //MD5加密
    public static byte[] getMD5(byte[] data) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return messageDigest.digest(data);
    }

}
