package club.hsspace.whypps.handle;

import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.exception.NotLocalCertificate;
import club.hsspace.whypps.manage.Authentication;
import club.hsspace.whypps.manage.FrameManage;
import club.hsspace.whypps.manage.LocalHost;
import club.hsspace.whypps.manage.TaskManage;
import club.hsspace.whypps.model.*;
import club.hsspace.whypps.util.AESTools;
import club.hsspace.whypps.util.CiphertextTools;
import club.hsspace.whypps.util.NumberTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @ClassName: TcpHandle
 * @CreateTime: 2022/3/12
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class TcpHandle implements Runnable, Closeable {

    private static final Logger logger = LoggerFactory.getLogger(TcpHandle.class);

    private Socket socket;

    public TcpHandle(Socket socket) {
        this.socket = socket;
        try {
            this.input = this.socket.getInputStream();
            this.output = this.socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private InputStream input;

    private OutputStream output;

    public Socket getSocket() {
        return socket;
    }

    public boolean alive() {
        return socket.isConnected();
    }

    @Injection
    private Authentication authentication;

    @Injection
    private LocalHost localHost;

    @Injection
    private TaskManage taskManage;

    @Injection
    private FrameManage frameManage;

    @Override
    public void run() {
        try {
            int read = 0;
            while (true) {
                read = input.read();
                if(read == -1)
                    break;

                boolean processor = true;
                /** PPS????????????  */
                if (read == 'P' && input.read() == 'P' && input.read() == 'S') {
                    byte[] bytes = input.readNBytes(16);
                    String sign = NumberTools.bytes2HexString(bytes);
                    logger.debug("??????{}?????????????????????{}", socket.getRemoteSocketAddress(), sign);
                    byte[] certificateBytes = authentication.getCertificateBytes(sign);
                    int version = input.read();
                    long time = NumberTools.bytes2Long(input.readNBytes(8), 0);
                    if (certificateBytes != null) {
                        send(NumberTools.bytesMerger(new byte[]{'R'}, certificateBytes));
                    } else {
                        //?????????????????????tcp???????????????????????????????????????????????????
                        TcpHandle tcpHandle = frameManage.getDataStream(sign).getTcpHandle();
                        if (tcpHandle != null) {
                            //?????????????????????
                            tcpHandle.getCertificate(ce -> {
                                try {
                                    if (ce == null) {
                                        send(NumberTools.bytesMerger(new byte[]{'R'}, bytes, new byte[]{'\0', '\0'}));
                                    } else {
                                        send(NumberTools.bytesMerger(new byte[]{'R'}, ce.getCertificateBytes()));
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }, bytes);
                        }
                    }
                    /** ??????????????? */
                } else if (read == 'F') {
                    DataFrame dataFrame = DataFrame.buildDataFrame(input);
                    String sign = NumberTools.bytes2HexString(dataFrame.getSrcSign());
                    DataStream dataStream = frameManage.getDataStream(sign);
                    Callback<DataStream> run = buildConnectionPool.get(sign);
                    AtomicReference<DataStream> syn = buildConnectionSyn.get(sign);
                    if (run != null) {
                        buildConnectionPool.remove(sign);
                        taskManage.submit(run, dataStream);
                    } else if (syn != null) {
                        buildConnectionSyn.remove(sign);
                        synchronized (syn) {
                            syn.set(dataStream);
                            syn.notify();
                        }
                    }

                    dataStream.putDataFrame(dataFrame);

                    /** AES???????????? */
                } else if (read == 'K') {
                    byte[] src = input.readNBytes(16);
                    byte[] tar = input.readNBytes(16);
                    byte[] aes = input.readNBytes(256);
                    if (Arrays.equals(tar, authentication.getLocalCertificate().getSignBytes())) {
                        Certificate certificate = authentication.getCertificate(src);

                        if (certificate != null) {
                            try {
                                DataStream dataStream = frameManage.newLink(certificate, this, CiphertextTools.privateDecryptRSA(authentication.getPrivateKey(), aes));
                                dataStream.sendData(new byte[0]);
                            } catch (GeneralSecurityException e) {
                                e.printStackTrace();
                            }
                        } else {
                            //TODO: ??????????????????
                            getCertificate(cer -> {
                                try {
                                    DataStream dataStream = frameManage.newLink(cer, this, CiphertextTools.privateDecryptRSA(authentication.getPrivateKey(), aes));
                                    dataStream.sendData(new byte[0]);
                                } catch (GeneralSecurityException e) {
                                    e.printStackTrace();
                                }
                            }, src);
                        }
                        logger.info("??????{}??????AES????????????", NumberTools.bytes2HexString(src));
                    } else {
                        //TODO: ????????????????????????????????????
                    }

                    /** ???????????? */
                } else if (read == 'R') {
                    byte[] bytes = input.readNBytes(16);
                    byte[] l = input.readNBytes(2);
                    int len = NumberTools.bytes2Short(l, 0);
                    //????????????????????????
                    byte[] certificate = NumberTools.bytesMerger(bytes, l, input.readNBytes(len));
                    Certificate ce = authentication.decryptCertificate(certificate);
                    String sign = ce.getSign();
                    //????????????????????????????????????
                    Callback<Certificate> run = getCertificateMap.get(sign);
                    if (run != null) {
                        taskManage.submit(run, ce);
                    } else {
                        for (AtomicReference<byte[]> atomic : getCertificateList) {
                            if (Arrays.equals(ce.getSignBytes(), atomic.get())) {
                                atomic.set(certificate);
                                synchronized (atomic) {
                                    atomic.notify();
                                }
                                break;
                            }
                        }
                    }
                    /** ???????????? */
                } else if (read == 'G') {
                    byte[] sign = input.readNBytes(16);
                    String s = NumberTools.bytes2HexString(sign);
                    logger.debug("??????{}?????????????????????{}", socket.getRemoteSocketAddress(), s);
                    //???????????? ????????????
                    String area = new String(input.readNBytes(2));
                    byte[] ipv4 = input.readNBytes(4);
                    byte[] ipv6 = input.readNBytes(16);
                    byte[] key = input.readNBytes(256);
                    int len = NumberTools.bytes2SignInt(input.readNBytes(4), 0);
                    byte[] data = input.readNBytes(len);
                    if (Arrays.equals(sign, authentication.getLocalCertificate().getSignBytes())) {
                        Certificate certificate = null;
                        try {
                            certificate = authentication.authorizeCertificate(area, ipv4, ipv6, key, data);
                        } catch (GeneralSecurityException e) {
                            //??????????????????
                            e.printStackTrace();
                        }
                        if (certificate != null) {
                            send(NumberTools.bytesMerger(new byte[]{'C', 0}, certificate.getCertificateBytes()));
                        } else {
                            send(new byte[]{'C', 1});
                        }
                    }
                    /** ???????????? */
                } else if (read == 'C') {
                    int success = input.read();
                    if (success == 0) {
                        byte[] bytes = input.readNBytes(16);
                        byte[] l = input.readNBytes(2);
                        int len = NumberTools.bytes2Short(l, 0);
                        //????????????????????????
                        byte[] certificate = NumberTools.bytesMerger(bytes, l, input.readNBytes(len));
                        Certificate ce = authentication.decryptCertificate(certificate);
                        logger.info("???????????????????????????{}", ce.getSign());
                        if (apply != null) {
                            authentication.setPrivateKey(apply.key());
                            authentication.setLocalCertificate(ce);
                            authentication.putCertificate(ce);
                            taskManage.submit(apply.value(), ce);
                            apply = null;
                        } else if (applyAtomic.get() == null) {
                            authentication.setPrivateKey(privateKey);
                            authentication.setLocalCertificate(ce);
                            authentication.putCertificate(ce);
                            privateKey = null;
                            applyAtomic.set(ce);
                            synchronized (applyAtomic) {
                                applyAtomic.notify();
                            }
                        }
                    }
                } else {
                    processor = false;
                }

                if(processor) {
                    logger.debug("TCP??????{}?????????????????????'{}'", socket.getRemoteSocketAddress(), (char) read);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                this.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        logger.info("TCP\"{}\"????????????", socket.getRemoteSocketAddress());
    }

    public synchronized void send(byte[] data) throws IOException {
        output.write(data);
        output.flush();
    }

    @Override
    public void close() throws IOException {
        localHost.closeTcp(this);

        socket.shutdownInput();

        input.close();
        output.close();
        socket.close();
    }

    //?????????????????????
    private List<AtomicReference<byte[]>> getCertificateList = new ArrayList<>();

    //???????????? ??????????????????
    public Certificate getCertificate(byte[] sign) {
        AtomicReference<byte[]> s = new AtomicReference<>(sign);
        getCertificateList.add(s);
        synchronized (s) {
            try {
                send(NumberTools.bytesMerger("PPS".getBytes(), sign, new byte[1], NumberTools.long2bytes(System.currentTimeMillis())));
                s.wait(5000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        synchronized (this) {
            getCertificateList.remove(s);
        }
        byte[] bytes = s.get();
        return authentication.decryptCertificate(bytes);
    }

    //?????????????????????
    private Map<String, Callback<Certificate>> getCertificateMap = new HashMap<>();

    //???????????? ??????????????????
    public void getCertificate(Callback<Certificate> run, byte[] sign) {
        getCertificateMap.put(NumberTools.bytes2HexString(sign), run);
        try {
            send(NumberTools.bytesMerger("PPS".getBytes(), sign, new byte[1], NumberTools.long2bytes(System.currentTimeMillis())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //???????????????????????????
    private Map<String, Callback<DataStream>> buildConnectionPool = new HashMap<>();

    /**
     * ???????????? ????????????(AES??????)
     *
     * @param run         ????????????
     * @param certificate ??????????????????
     */
    public void buildConnection(Callback<DataStream> run, Certificate certificate) {
        buildConnectionPool.put(NumberTools.bytes2HexString(certificate.getSignBytes()), run);
        try {
            byte[] aes = AESTools.generateKey();
            DataStream dataStream = frameManage.newLink(certificate, this, aes);
            send(NumberTools.bytesMerger("K".getBytes(),
                    authentication.getLocalCertificate().getSignBytes(),
                    certificate.getSignBytes(),
                    CiphertextTools.publicEncryptRSA(certificate.getPublicKey(), aes)));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    //?????????????????????????????????
    private Map<String, AtomicReference<DataStream>> buildConnectionSyn = new HashMap<>();

    //???????????? ????????????
    public DataStream buildConnection(Certificate certificate) {
        AtomicReference<DataStream> getter = new AtomicReference<>();
        buildConnectionSyn.put(NumberTools.bytes2HexString(certificate.getSignBytes()), getter);
        byte[] aes = AESTools.generateKey();
        synchronized (getter) {
            try {
                DataStream dataStream = frameManage.newLink(certificate, this, aes);
                send(NumberTools.bytesMerger("K".getBytes(),
                        authentication.getLocalCertificate().getSignBytes(),
                        certificate.getSignBytes(),
                        CiphertextTools.publicEncryptRSA(certificate.getPublicKey(), aes)));
                getter.wait(5000);
            } catch (InterruptedException | GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
        }
        DataStream dataStream = getter.get();
        return dataStream;
    }

    //???????????????????????????(??????)
    private byte[] privateKey = null;
    private AtomicReference<Certificate> applyAtomic = new AtomicReference<>(null);

    //???????????? ??????????????????
    public Certificate applyCertificate(Certificate certificate, byte[] data) {
        if (certificate.isLocal()) {
            try {
                byte[] address = InetAddress.getLocalHost().getAddress();
                byte[] key = AESTools.generateKey();

                KeyPair keyPair = CiphertextTools.generateRSAKeyPair();
                byte[] aes = NumberTools.bytesMerger(new byte[]{11},
                        NumberTools.int2bytes(keyPair.getPublic().getEncoded().length),
                        keyPair.getPublic().getEncoded(),
                        data
                );

                privateKey = keyPair.getPrivate().getEncoded();

                byte[] encrypt = AESTools.encrypt(key, aes);

                synchronized (applyAtomic) {
                    send(NumberTools.bytesMerger(
                            "G".getBytes(),
                            certificate.getSignBytes(),
                            "CN".getBytes(),
                            address.length == 4 ? address : new byte[4],
                            address.length == 16 ? address : new byte[16],
                            CiphertextTools.publicEncryptRSA(certificate.getPublicKey(), key),
                            NumberTools.int2bytes(encrypt.length),
                            encrypt
                    ));
                    applyAtomic.wait(5000);
                    Certificate result = applyAtomic.get();
                    applyAtomic.set(null);
                    return result;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    //????????????????????????
    private ObjectPair<byte[], Callback<Certificate>> apply;

    /**
     * ???????????? ??????????????????
     *
     * @param run         ????????????
     * @param certificate ????????????????????????(????????????????????????)
     * @param data        ???????????????
     */
    public void applyCertificate(Callback<Certificate> run, Certificate certificate, byte[] data) {
        if (certificate.isLocal()) {
            try {
                byte[] address = InetAddress.getLocalHost().getAddress();
                byte[] key = AESTools.generateKey();

                KeyPair keyPair = CiphertextTools.generateRSAKeyPair();
                byte[] aes = NumberTools.bytesMerger(new byte[]{11},
                        NumberTools.int2bytes(keyPair.getPublic().getEncoded().length),
                        keyPair.getPublic().getEncoded(),
                        data
                );

                apply = new ObjectPair<>(keyPair.getPrivate().getEncoded(), run);
                byte[] encrypt = AESTools.encrypt(key, aes);
                send(NumberTools.bytesMerger(
                        "G".getBytes(),
                        certificate.getSignBytes(),
                        "CN".getBytes(),
                        address.length == 4 ? address : new byte[4],
                        address.length == 16 ? address : new byte[16],
                        CiphertextTools.publicEncryptRSA(certificate.getPublicKey(), key),
                        NumberTools.int2bytes(encrypt.length),
                        encrypt
                ));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        } else {
            throw new NotLocalCertificate("?????????????????????????????????");
        }
    }
}
