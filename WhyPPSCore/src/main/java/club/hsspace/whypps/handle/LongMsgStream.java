package club.hsspace.whypps.handle;

import club.hsspace.whypps.listener.DataLabel;
import club.hsspace.whypps.model.DataLink;
import club.hsspace.whypps.model.senior.LongM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @ClassName: LongMsgStream
 * @CreateTime: 2022/5/1
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class LongMsgStream implements AutoCloseable{

    private static final Logger logger = LoggerFactory.getLogger(LongMsgStream.class);

    private InputStream inputStream;

    private OutputStream outputStream;

    private DataStream dataStream;

    private int count;

    private String requestId;

    public LongMsgStream(String requestId, DataStream dataStream) {
        this.dataStream = dataStream;
        this.requestId = requestId;
        this.count = 0;

        PipedInputStream pipedInputStream = new PipedInputStream();
        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        try {
            pipedInputStream.connect(pipedOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        inputStream = pipedInputStream;
        outputStream = pipedOutputStream;
    }

    public void receiveData(byte[] data) throws IOException {
        outputStream.write(data);
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void sendData(byte[] data) {
        DataLink longMDataLink = DataLink.of(DataLabel.LONG_M, LongM.of(requestId, count++), data);
        dataStream.sendData(longMDataLink);
    }

    public void sendData(String data) {
        sendData(data.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
        outputStream.close();
    }
}
