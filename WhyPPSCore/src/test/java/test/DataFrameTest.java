package test;

import club.hsspace.whypps.model.DataFrame;
import club.hsspace.whypps.model.DataLink;
import club.hsspace.whypps.util.AESTools;
import club.hsspace.whypps.util.NumberTools;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @ClassName: DataStreamTest
 * @CreateTime: 2022/3/22
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class DataFrameTest {

    private static final Logger logger = LoggerFactory.getLogger(DataFrameTest.class);

    private static final Random random = new Random();

    /**
     * 数据帧拼接检测2
     * 检测随机长度帧数是否匹配
     * 检测随机净数据是否正确
     * 检测包计数区是否正确
     * 检测连续标记区是否正确
     */
    @Test
    public void testPackData() {
        byte[] key = AESTools.generateKey();
        byte[] data = new byte[100];
        random.nextBytes(data);

        byte[] srcSign = {1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2};
        byte[] tarSign = {3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4};

        List<byte[]> byteList = DataFrame.packFrame2Stream(key, srcSign, tarSign, DataFrame.DataSign.data, data)
                .map(DataFrame::toBytes)
                .map(n -> Arrays.copyOfRange(n, 36, n.length))
                .map(n -> aesDecrypt(key, n))
                .map(n -> Arrays.copyOfRange(n, 8, n.length))
                .toList();

        int size = data.length / 0x1FF00 + (data.length % 0x1FF00 == 0 ? 0 : 1);
        assertEquals(size, byteList.size());

        for (int i = 0; i < byteList.size(); i++) {
            byte[] bytes = byteList.get(i);
            byte b = bytes[bytes.length - 1];
            assertEquals((byte) ((b >>> 1) & 0x7F), (byte) (i % 0x80));
            assertEquals(i == byteList.size() - 1, (b & 1) == 1);
        }
        //assertTrue(Arrays.equals(data, NumberTools.bytesMerger(byteList.stream().map(n -> Arrays.copyOfRange(n, 0, n.length - 1)).toList())));
    }

    @Test
    public void testDesData() {
        byte[] key = AESTools.generateKey();
        byte[] data = new byte[80000];
        random.nextBytes(data);

        byte[] srcSign = {1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2};
        byte[] tarSign = {3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4};

        List<byte[]> byteList = DataFrame.packFrame2Stream(key, srcSign, tarSign, DataFrame.DataSign.data, data)
                .map(DataFrame::toBytes)
                .toList();

        List<byte[]> result = byteList.stream()
                .map(ByteArrayInputStream::new)
                .filter(n -> n.read() == 'F')
                .map(DataFrameTest::buildDataFrame)
                .map(dataFrame -> {
                    try {
                        return dataFrame.decrypt(key);
                    } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(DataFrame.EncryptData::data)
                .collect(Collectors.toList());


        assertTrue(Arrays.equals(data, NumberTools.bytesMerger(result)));
    }

    @Test
    public void testDesLen() throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        byte[] key = AESTools.generateKey();
        byte[] data = new byte[800000];
        random.nextBytes(data);

        System.out.println(data.length);
        System.out.println(AESTools.encrypt(key, data).length);
    }

    public static DataFrame buildDataFrame(InputStream inputStream) {
        try {
            return DataFrame.buildDataFrame(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //byteList.stream()
    //        .map(n -> Arrays.copyOfRange(n, n.length-1, n.length)[0])
    //        //.map(n -> (byte)(n>>>1))
    //        //.map((n -> (byte)(n&0x7F)))
    //        .map(n -> (byte)(n&1))
    //        .toList()

    private static byte[] aesDecrypt(byte[] key, byte[] contain) {
        try {
            return AESTools.decrypt(key, contain);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
