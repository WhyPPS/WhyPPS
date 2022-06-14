package club.hsspace.whypps.manage;

import club.hsspace.whypps.action.Container;
import club.hsspace.whypps.debug.DebugKey;
import club.hsspace.whypps.action.Init;
import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.exception.CertificateNotFoundError;
import club.hsspace.whypps.model.Certificate;
import club.hsspace.whypps.util.AESTools;
import club.hsspace.whypps.util.CiphertextTools;
import club.hsspace.whypps.util.NumberTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;

/**
 * @ClassName: Authentication
 * @CreateTime: 2022/3/8
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@Container(sort = -60)
public class Authentication {

    private static final Logger logger = LoggerFactory.getLogger(Authentication.class);

    @Injection
    private Configuration configuration;

    @Injection(name = "runClass")
    private Class<?> runClass;

    private byte[] privateKey;

    private Certificate localCertificate;

    private Map<String, Certificate> certificates = new HashMap<>();

    private Authentication() {

    }

    @Init(sort = 10)
    private void initPrivateKey() throws IOException, GeneralSecurityException, CertificateNotFoundError {
        byte[] security = configuration.getSecurity();
        if (security.length > 0) {
            byte[] md5 = Arrays.copyOfRange(security, 0, 16);
            security = Arrays.copyOfRange(security, 16, security.length);
            DebugKey debugKey = runClass.getAnnotation(DebugKey.class);
            if (debugKey != null) {
                privateKey = CiphertextTools.decryptPBE(debugKey.password(), configuration.getPBEKey(), security);
                logger.debug("初始化永久密钥成功，口令来自于程序内嵌调试模式。");
            } else if (configuration.getDebugKey() != null) {
                privateKey = CiphertextTools.decryptPBE(configuration.getDebugKey(), configuration.getPBEKey(), security);
                logger.debug("初始化永久密钥成功，口令来自于配置文件调试模式。");
            } else {
                logger.info("请输入密钥初始化口令：");
                Scanner scanner = new Scanner(System.in);
                String password = scanner.nextLine();
                privateKey = CiphertextTools.decryptPBE(password, configuration.getPBEKey(), security);
                logger.info("验证权威服务器口令成功，初始化永久密钥成功。");
            }
            localCertificate = certificates.get(NumberTools.bytes2HexString(md5));
            if (localCertificate == null) {
                throw new CertificateNotFoundError("密钥证书未找到");
            }
            logger.info("成功找到获取私钥证书，证书标识码：{}", localCertificate.getSign());
        } else {
            logger.info("未检测到程序内嵌永久证书密钥。");
        }
    }

    @Init
    private void initEmbedCertificate(@Injection(name = "runObject") Object object) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException, GeneralSecurityException {
        Method getCertificates = runClass.getMethod("getCertificates");
        List<String> certificates = (List<String>) getCertificates.invoke(object);
        for (String certificate : certificates) {
            InputStream ceIs;
            if (certificate.startsWith("cl:"))
                ceIs = getClass().getResourceAsStream(certificate.replace("cl:", ""));
            else if (certificate.startsWith("fi:"))
                ceIs = new FileInputStream(certificate.replace("fi:", ""));
            else
                continue;

            byte[] bytes = ceIs.readAllBytes();
            Certificate ce = Certificate.getEmbedCertificate(bytes);
            ce.setLocal();
            this.certificates.put(ce.getSign(), ce);
            logger.info("成功加载本地证书：{}", ce.getSign());
        }
        logger.info("本地证书初始化成功");
    }

    /**
     * @param area
     * @param ipv4
     * @param ipv6
     * @param data 用户侧数据
     * @return
     * @throws GeneralSecurityException
     */
    public Certificate authorizeCertificate(byte[] publicKey, String area, byte[] ipv4, byte[] ipv6, byte[] data) throws GeneralSecurityException {
        //TODO: 此处应该加入用户自定义授权过滤证书许可。
        //提示，这里根据自定义函数的返回值做区分，byte[]直接赋值证书features，byte为增加特征，short则为更改可信度，String更改地区，long更改终止时间，其余证书信息不允许更改。
        short trust = Short.MAX_VALUE;
        byte[] features = new byte[0];
        Certificate certificate = Certificate.getCertificate(privateKey, localCertificate.getSignBytes(), configuration.getEffectiveTime(), area, ipv4, ipv6, trust, publicKey, features);
        return certificate;
    }

    public Certificate authorizeCertificate(String area, byte[] ipv4, byte[] ipv6, byte[] key, byte[] data) throws GeneralSecurityException {
        byte[] AESKey = CiphertextTools.privateDecryptRSA(privateKey, key);
        byte[] AESData = AESTools.decrypt(AESKey, data);
        int len = NumberTools.bytes2Short(AESData, 1);
        byte[] publicKey = Arrays.copyOfRange(AESData, 3, 3 + len);
        byte[] userData = Arrays.copyOfRange(AESData, 3 + len, AESData.length);
        return authorizeCertificate(publicKey, area, ipv4, ipv6, userData);
    }


    /**
     * 解密证书
     *
     * @param certificate 完整证书
     * @return 返回解密后证书实例，因任何因素导致失败均返回null
     */
    public Certificate decryptCertificate(byte[] certificate) {
        Certificate result = getCertificate(NumberTools.bytes2HexString(CiphertextTools.getMD5(certificate)));
        if (result != null) {
            return result;
        }
        //获取本地授权方证书
        Certificate cer = certificates.get(NumberTools.bytes2HexString(certificate, 0, 16));
        if (cer.isLocal()) {
            try {
                result = Certificate.getCertificate(cer.getPublicKey(), certificate);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public Collection<Certificate> getCertificates() {
        return certificates.values();
    }

    public byte[] getCertificateBytes(String sign) {
        return certificates.get(sign).getCertificateBytes();
    }

    public Certificate getCertificate(String sign) {
        return certificates.get(sign);
    }

    public Certificate getCertificate(byte[] sign) {
        return certificates.get(NumberTools.bytes2HexString(sign));
    }

    public void putCertificate(Certificate certificate) {
        this.certificates.put(certificate.getSign(), certificate);
    }

    public Certificate getLocalCertificate() {
        return localCertificate;
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(byte[] privateKey) {
        this.privateKey = privateKey;
    }

    public void setLocalCertificate(Certificate localCertificate) {
        this.localCertificate = localCertificate;
    }


    /**
     * 独立方法，生成权威服务器证书
     * 需要用户密码同时生成PBE密文文件、PBEKey文件、证书文件
     */
    public static void generateAuthorityCertificate(File file) throws GeneralSecurityException, IOException {
        KeyPair keyPair = CiphertextTools.generateRSAKeyPair();

        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();

        byte[] rsaPrivate = rsaPrivateKey.getEncoded();
        byte[] pbeKey = SecureRandom.getInstanceStrong().generateSeed(1024);

        logger.info("密钥对生成成功，接下来请您输入您的PBE口令并牢记：");

        Scanner scanner = new Scanner(System.in);
        String password = scanner.nextLine();
        byte[] rsaPrivateCiphertext = CiphertextTools.encryptPBE(password, pbeKey, rsaPrivate);
        File f1 = new File(file, "PBEKey.key");
        writeFile(f1, pbeKey);
        logger.info("已生成PBEKey文件{}", f1);

        byte[] rsaPublic = rsaPublicKey.getEncoded();
        InetAddress localHost = InetAddress.getLocalHost();
        byte[] ipv4 = null;
        byte[] ipv6 = null;
        if (localHost instanceof Inet4Address)
            ipv4 = localHost.getAddress();
        if (localHost instanceof Inet6Address)
            ipv6 = localHost.getAddress();
        Certificate ce = Certificate.getCertificate(rsaPrivate, null, -1, "CN", ipv4, ipv6, Short.MAX_VALUE, rsaPublic, null);
        File f3 = new File(file, "Certificate.ce");
        writeFile(f3, ce.getCertificateBytes(), rsaPublic);
        logger.info("已生成权威服务器证书文件{}", f3);

        File f2 = new File(file, "RSAPrivateCiphertext.txt");
        writeFile(f2, (ce.getSign() + NumberTools.bytes2HexString(rsaPrivateCiphertext)).getBytes());
        logger.info("已生成PBE密文文件{}", f2);

        logger.info("权威证书任务生成已执行成功，请您妥善保存相关文件");
    }

    private static void writeFile(File file, byte[]... data) throws IOException {
        OutputStream pbeKeyOs = new FileOutputStream(file);
        for (byte[] datum : data) {
            pbeKeyOs.write(datum);
        }
        pbeKeyOs.close();
    }

}
