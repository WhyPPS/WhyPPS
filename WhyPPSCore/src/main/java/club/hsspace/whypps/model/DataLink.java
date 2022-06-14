package club.hsspace.whypps.model;

import club.hsspace.whypps.listener.DataLabel;
import club.hsspace.whypps.model.senior.SeniorBaseModel;
import club.hsspace.whypps.util.NumberTools;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

/**
 * @ClassName: DataLink
 * @CreateTime: 2022/4/25
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class DataLink<T extends SeniorBaseModel> {

    private static final Logger logger = LoggerFactory.getLogger(DataLink.class);

    private DataLabel dataLabel;
    private SeniorBaseModel data;
    private byte[] extraData;

    public DataLink(DataLabel dataLabel, JSONObject data, byte[] extraData) {
        this.dataLabel = dataLabel;
        this.data = JSON.toJavaObject(data, dataLabel.mapClass);
        this.extraData = extraData;
    }

    public DataLink(DataLabel dataLabel, T data, byte[] extraData) {
        this.dataLabel = dataLabel;
        this.data = data;
        this.extraData = extraData;
    }

    public static DataLink of(DataLabel dataLabel, SeniorBaseModel data, byte[] extraData) {
        return new DataLink(dataLabel, data, extraData);
    }

    public DataLink(byte[] dataArray) {
        int action = 0;
        for (int i = 0; i < dataArray.length; i++) {
            if (dataArray[i] == '\n') {
                if (action == 0) {
                    this.dataLabel = Enum.valueOf(DataLabel.class, new String(Arrays.copyOfRange(dataArray, 0, i)));
                    action = i + 1;
                } else {
                    this.data = JSON.toJavaObject(JSONObject.parseObject(new String(Arrays.copyOfRange(dataArray, action, i))), dataLabel.mapClass);
                    this.extraData = Arrays.copyOfRange(dataArray, i + 1, dataArray.length);
                    break;
                }
            }
        }
    }

    public byte[] toBytes() {
        return NumberTools.bytesMerger(dataLabel.name().getBytes(StandardCharsets.UTF_8), new byte[]{'\n'}, data.toBytes(), new byte[]{'\n'}, extraData);
    }

    public DataLabel getDataLabel() {
        return dataLabel;
    }

    public T getData() {
        return (T)data;
    }

    public <C extends SeniorBaseModel> C getData(Class<C> tClass) {
        return tClass.cast(data) ;
    }

    public byte[] getExtraData() {
        return extraData;
    }
}
