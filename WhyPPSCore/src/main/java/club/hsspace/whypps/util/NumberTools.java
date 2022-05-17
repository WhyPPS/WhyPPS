package club.hsspace.whypps.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName: NumberTools
 * @CreateTime: 2022/3/9
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class NumberTools {

    private static final Logger logger = LoggerFactory.getLogger(NumberTools.class);

    public static long bytes2Long(byte[] src, int offset) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value |= ((src[offset + i] & 0xFF) << (8 * i));
        }
        return value;
    }

    public static long bytes2Int(byte[] src, int offset) {
        long value = (src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16)
                | ((src[offset + 3] & 0xFF) << 24);
        return value;
    }

    public static int bytes2SignInt(byte[] src, int offset) {
        int value = (src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16)
                | ((src[offset + 3] & 0xFF) << 24);
        return value;
    }

    public static int bytes2Short(byte[] src, int offset) {
        int value = (src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8);
        return value;
    }

    public static short bytes2SignShort(byte[] src, int offset) {
        short value = (short) (src[offset]
                        | (src[offset + 1] << 8));
        return value;
    }

    public static byte[] long2bytes(long num) {
        byte[] b = new byte[8];
        for (int i = 0; i < 8; i++) {
            b[i] = (byte) (num >>> (56 - (i * 8)));
        }
        return b;
    }

    public static void long2bytes(long num, byte[] arr, int start) {
        for (int i = 0; i < 8; i++) {
            arr[start + i] = (byte) (num >>> (56 - (i * 8)));
        }
    }

    public static byte[] int2bytes(int num) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (num >>> (i * 8));
        }
        return b;
    }

    public static void bytes2bytes(byte[] src, int start, int len, byte[] tar, int offset) {
        if (src != null) {
            for (int i = 0; i < len; i++) {
                tar[offset + i] = src[start + i];
            }
        }
    }

    public static byte[] bytesMerger(byte[]... byteList) {
        int lengthByte = 0;
        for (byte[] b : byteList) {
            if (b != null)
                lengthByte += b.length;
        }
        byte[] allByte = new byte[lengthByte];
        int countLength = 0;
        for (byte[] b : byteList) {
            if (b != null) {
                bytes2bytes(b, 0, b.length, allByte, countLength);
                countLength += b.length;
            }
        }
        return allByte;
    }

    public static byte[] bytesMerger(List<byte[]> byteList) {
        return bytesMerger(byteList.toArray(new byte[byteList.size()][]));
    }

    public static void short2bytes(int i, byte[] arr, int start) {
        arr[start] = (byte) (0xff & i);
        arr[start + 1] = (byte) ((0xff00 & i) >> 8);
    }

    public static String bytes2HexString(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte aByte : bytes) {
            result.append(String.format("%02X", aByte));
        }
        return result.toString();
    }

    public static String bytes2HexString(byte[] bytes, int from, int to) {
        StringBuilder result = new StringBuilder();
        for (int i = from; i < to; i++) {
            result.append(String.format("%02X", bytes[i]));
        }
        return result.toString();
    }

    public static byte[] hexString2Bytes(String hexString) {
        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() >> 1];
        int index = 0;
        for (int i = 0; i < hexString.length(); i++) {
            if (index > hexString.length() - 1) {
                return byteArray;
            }
            byte highDit = (byte) (Character.digit(hexString.charAt(index), 16) & 0xFF);
            byte lowDit = (byte) (Character.digit(hexString.charAt(index + 1), 16) & 0xFF);
            byteArray[i] = (byte) (highDit << 4 | lowDit);
            index += 2;
        }
        return byteArray;
    }

    /**
     * 分隔数组 根据每段数量分段
     *
     * @param data  被分隔的数组
     * @param count 每段数量
     * @return
     */
    public static List<byte[]> subBytesByCount(byte[] data, int count) {

        List<byte[]> result = new ArrayList<>();
        int size = data.length;

        if (size > 0 && count > 0) {

            int segments = size / count;
            segments = size % count == 0 ? segments : segments + 1;

            byte[] cutList = null;

            for (int i = 0; i < segments; i++) {
                if (i == segments - 1) {
                    cutList = Arrays.copyOfRange(data, count * i, size);
                } else {
                    cutList = Arrays.copyOfRange(data, count * i, count * (i + 1));
                }
                result.add(cutList);
            }
        } else {
            result.add(data);
        }
        return result;
    }

}
