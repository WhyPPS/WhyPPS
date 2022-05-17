package club.hsspace.whypps.handle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

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

    public LongMsgStream() {
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

    @Override
    public void close() throws IOException {
        inputStream.close();
        outputStream.close();
    }
}
