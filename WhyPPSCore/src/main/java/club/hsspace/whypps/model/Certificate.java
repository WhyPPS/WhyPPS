package club.hsspace.whypps.model;

import club.hsspace.whypps.util.CiphertextTools;
import club.hsspace.whypps.util.NumberTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.security.GeneralSecurityException;
import java.util.Arrays;

/**
 * @ClassName: Certificate
 * @CreateTime: 2022/3/8
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class Certificate {

    private static final Logger logger = LoggerFactory.getLogger(Certificate.class);

    private long startTime;
    private long endTime;
    private String area;

    private byte[] ipv4;
    private byte[] ipv6;

    private int trust;

    private byte[] publicKey;

    private byte[] features;

    //授权方许可标志
    private byte[] permitSign;

    //证书RSA加密内容
    private byte[] contain;

    //证书标识码
    private String sign;

    //是本地根证书
    private boolean isLocal = false;

    //参数生成证书
    public static Certificate getCertificate(byte[] privateKey, byte[] permitSign, long duration, String area, byte[] ipv4, byte[] ipv6, short trust, byte[] publicKey, byte[] features) throws GeneralSecurityException {
        Certificate certificate = new Certificate();
        certificate.startTime = System.currentTimeMillis();
        if (duration != -1)
            certificate.endTime = certificate.startTime + duration;
        certificate.area = area;
        certificate.trust = trust;
        certificate.publicKey = publicKey;
        certificate.features = features == null ? new byte[0] : features;
        certificate.permitSign = permitSign == null ? new byte[16] : permitSign;

        certificate.ipv4 = ipv4;
        certificate.ipv6 = ipv6;
        certificate.contain = certificate.generateContain(privateKey);

        certificate.sign = NumberTools.bytes2HexString(certificate.getSignBytes());
        return certificate;
    }

    //证书加载
    public static Certificate getCertificate(byte[] publicKey, byte[] certificate) throws GeneralSecurityException {
        Certificate result = new Certificate();
        result.permitSign = Arrays.copyOfRange(certificate, 0, 16);

        result.contain = Arrays.copyOfRange(certificate, 18, certificate.length);
        byte[] deContainContain = CiphertextTools.publicDecryptRSASection(publicKey, result.contain);
        result.startTime = NumberTools.bytes2Long(deContainContain, 0);
        result.endTime = NumberTools.bytes2Long(deContainContain, 8);
        result.area = new String(Arrays.copyOfRange(deContainContain, 16, 18));
        result.ipv4 = Arrays.copyOfRange(deContainContain, 18, 22);
        result.ipv6 = Arrays.copyOfRange(deContainContain, 22, 38);
        result.trust = NumberTools.bytes2Short(deContainContain, 38);
        int len = NumberTools.bytes2Short(deContainContain, 41);
        result.publicKey = Arrays.copyOfRange(deContainContain, 43, 43 + len);
        result.features = Arrays.copyOfRange(deContainContain, 43 + len, deContainContain.length);
        result.sign = NumberTools.bytes2HexString(result.getSignBytes());
        return result;
    }

    //本地证书格式加载
    public static Certificate getEmbedCertificate(byte[] certificate) throws GeneralSecurityException {
        int len = NumberTools.bytes2Short(certificate, 16);
        return getCertificate(Arrays.copyOfRange(certificate, len + 18, certificate.length), Arrays.copyOfRange(certificate, 0, len + 18));
    }

    //生成证书完整字节数据
    public byte[] getCertificateBytes() {
        byte[] certificateBytes = new byte[18 + contain.length];
        NumberTools.bytes2bytes(permitSign, 0, 16, certificateBytes, 0);
        NumberTools.short2bytes((short) contain.length, certificateBytes, 16);
        NumberTools.bytes2bytes(contain, 0, contain.length, certificateBytes, 18);
        return certificateBytes;
    }

    private byte[] generateContain(byte[] privateKey) throws GeneralSecurityException {
        byte[] contain = new byte[43 + publicKey.length + features.length];
        NumberTools.long2bytes(startTime, contain, 0);
        NumberTools.long2bytes(endTime, contain, 8);
        NumberTools.bytes2bytes(area.getBytes(), 0, 2, contain, 16);
        NumberTools.bytes2bytes(ipv4, 0, 4, contain, 18);
        NumberTools.bytes2bytes(ipv6, 0, 16, contain, 22);
        NumberTools.short2bytes(trust, contain, 38);
        contain[40] = 11;
        NumberTools.short2bytes(publicKey.length, contain, 41);
        NumberTools.bytes2bytes(publicKey, 0, publicKey.length, contain, 43);
        NumberTools.bytes2bytes(features, 0, features.length, contain, 43 + publicKey.length);
        return CiphertextTools.privateEncryptRSASection(privateKey, contain);
    }

    public byte[] getSignBytes() {
        return CiphertextTools.getMD5(getCertificateBytes());
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public String getArea() {
        return area;
    }

    public byte[] getIpv4() {
        return ipv4;
    }

    public byte[] getIpv6() {
        return ipv6;
    }

    public int getTrust() {
        return trust;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public byte[] getFeatures() {
        return features;
    }

    public byte[] getPermitSign() {
        return permitSign;
    }

    public byte[] getContain() {
        return contain;
    }

    public String getSign() {
        return sign;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal() {
        this.isLocal = true;
    }
}
