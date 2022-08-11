package club.hsspace.whypps.model;

import club.hsspace.whypps.util.AESTools;
import club.hsspace.whypps.util.NumberTools;
import org.bouncycastle.jcajce.provider.symmetric.AES;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @ClassName: DataFrame
 * @CreateTime: 2022/3/20
 * @Comment: 数据帧实体类
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class DataFrame {

    private static final Logger logger = LoggerFactory.getLogger(DataFrame.class);

    private byte[] srcSign;

    private byte[] tarSign;

    private byte version = 0;

    private DataSign dataSign;

    private int len;

    private byte[] data;

    private DataFrame(byte[] srcSign, byte[] tarSign, DataSign dataSign, byte[] data) {
        this.srcSign = srcSign;
        this.tarSign = tarSign;
        this.dataSign = dataSign;
        this.data = data;
        this.len = data.length;
    }

    /**
     * 构造数据帧
     * @param inputStream   输入流(不中断，从源地址标记开始计算)
     * @return
     */
    public static DataFrame buildDataFrame(InputStream inputStream) throws IOException {
        byte[] srcSign = inputStream.readNBytes(16);
        byte[] tarSign = inputStream.readNBytes(16);
        byte[] sg = inputStream.readNBytes(3);
        DataSign dataSign = DataSign.code2DataSign((sg[0] >> 1) & 7);
        int len = (sg[0] & 0x1) | ((sg[1] & 0xFF) << 1) | ((sg[2] & 0xFF) << 9);
        byte[] data = inputStream.readNBytes(len);
        return new DataFrame(srcSign, tarSign, dataSign, data);
    }

    public byte[] toBytes() {
        byte[] sign = {(byte) (len & 1), (byte) ((len >> 1) & 0xFF), (byte) ((len >> 9) & 0xFF)};
        return NumberTools.bytesMerger("F".getBytes(), srcSign, tarSign, sign, data);
    }

    public EncryptData decrypt(byte[] key) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        return EncryptData.generateEncryptData(AESTools.decrypt(key, data));
    }

    public EncryptData decryptNoException(byte[] key) {
        try {
            return decrypt(key);
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public record EncryptData(byte[] retain, short sign, byte[] data, boolean end, byte count) {
        public static EncryptData generateEncryptData(byte[] data) {
            return new EncryptData(Arrays.copyOfRange(data, 0, 8),
                    NumberTools.bytes2SignShort(data, 8),
                    Arrays.copyOfRange(data, 10, data.length - 1),
                    (data[data.length - 1] & 1) == 1,
                    (byte) ((data[data.length - 1] >>> 1) & 0x7F));
        }
    }

    /**
     * 懒计算式 数据帧流
     * 适合中等大小的数据流，不适合大量数据发送(如大文件)！！！
     * 大量数据请使用动态流
     * 仅仅提供懒加载优化
     *
     * @param key
     * @param srcSign
     * @param tarSign
     * @param dataSign
     * @param data
     * @return
     */
    public static Stream<DataFrame> packFrame2Stream(byte[] key, byte[] srcSign, byte[] tarSign, DataSign dataSign, byte[] data) {
        int dLen = data.length;
        int size = Integer.max(1, dLen / 0x1FF00 + (dLen % 0x1FF00 == 0 ? 0 : 1));
        Stream<DataFrame> result = IntStream.iterate(0, n -> n + 1)
                .mapToObj(n -> packData(key, Arrays.copyOfRange(data, n * 0x1FF00, Integer.min(n * 0x1FF00 + 0x1FF00, dLen)), n == size - 1, (byte) (n % 0x80)))
                .map(n -> new DataFrame(srcSign, tarSign, dataSign, n))
                .limit(size);
        return result;
    }

    public static ObjectPair<DataSign, byte[]> Frames2data(Collection<DataFrame> frames, byte[] key) {
        byte[] bytes = NumberTools.bytesMerger(frames.stream()
                .map(n -> n.decryptNoException(key))
                .map(n -> n.data()).toList());
        DataFrame next = frames.iterator().next();
        return new ObjectPair<>(next.dataSign, bytes);
    }

    public static ObjectPair<DataSign, byte[]> Frames2data(Collection<DataFrame.EncryptData> frames, DataFrame.DataSign dataSign) {
        byte[] bytes = NumberTools.bytesMerger(frames.stream().map(n -> n.data).toList());
        return new ObjectPair<>(dataSign, bytes);
    }

    /**
     * 同上，即时计算式，适合几帧到小几十帧之内的少量数据流，不允许使用大量数据发送，会造成瞬时计算压力过大
     */
    public static List<DataFrame> packFrame2List(byte[] key, byte[] srcSign, byte[] tarSign, DataSign dataSign, byte[] data) {
        return packFrame2Stream(key, srcSign, tarSign, dataSign, data).toList(); //(假)
    }

    private static byte[] packData(byte[] key, byte[] data, boolean end, byte count) {
        try {
            //TODO: {33,44} 此处添加组合帧标记(自增)
            return AESTools.encrypt(key, NumberTools.bytesMerger(new byte[8], new byte[]{33,44},data, new byte[]{(byte) (((count << 1) & 0xFE) + (end ? 1 : 0))}));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public enum DataSign {

        data((byte) 0);

        public static DataSign code2DataSign(int code) {
            return switch (code){
                case 0 -> data;
                default -> throw new IllegalStateException("Unexpected value: " + code);
            };
        }

        public final byte code;

        DataSign(byte code) {
            this.code = code;
        }
    }

    public byte[] getSrcSign() {
        return srcSign;
    }

    public byte[] getTarSign() {
        return tarSign;
    }

    public byte getVersion() {
        return version;
    }

    public DataSign getDataSign() {
        return dataSign;
    }

    public int getLen() {
        return len;
    }

    public byte[] getData() {
        return data;
    }
}
