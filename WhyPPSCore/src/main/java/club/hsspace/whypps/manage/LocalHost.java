package club.hsspace.whypps.manage;

import club.hsspace.whypps.action.Container;
import club.hsspace.whypps.action.Init;
import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.handle.TcpHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

/**
 * @ClassName: LocalHost
 * @CreateTime: 2022/3/10
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@Container(sort = -50)
public class LocalHost implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(LocalHost.class);

    @Injection
    private Configuration configuration;

    @Injection
    private ContainerManage containerManage;

    //特殊线程池、监听阻塞线程池
    private ExecutorService es;

    private Thread listen;

    private ServerSocket serverSocket;

    private Map<String, TcpHandle> sign2TcpHandle = new HashMap<>();

    private LocalHost() {
        es = Executors.newCachedThreadPool();
    }

    @Init
    public void listenLocalHost() {
        int i = 0;
        int port = configuration.getPort();
        Random random = new Random();
        do {
            try {
                serverSocket = new ServerSocket(port);
            } catch (BindException e) {
                logger.error("端口 {} 被占用，{}尝试使用随机端口号端口", port, configuration.autoDistribution() ? "允许" : "不允许");
                i++;
                port = random.nextInt(20000) + 1024;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (serverSocket == null && i < 5 && configuration.autoDistribution());

        int localPort = serverSocket.getLocalPort();
        logger.info("监听本地端口：{}", localPort);

        listen = new Thread(this);
        listen.setName("listen");
        listen.start();
    }

    public TcpHandle connect(String host, int port) throws IOException {
        Socket sock = new Socket(host, port);
        logger.info("发起连接: {}", sock.getRemoteSocketAddress());
        TcpHandle tcpHandle = new TcpHandle(sock);
        try {
            containerManage.injection(tcpHandle);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        es.submit(tcpHandle);
        return tcpHandle;
    }

    public TcpHandle getTcpHandle(String sign) {
        return sign2TcpHandle.get(sign);
    }

    public void putTcpHandle(String sign, TcpHandle connect) {
        sign2TcpHandle.put(sign, connect);
    }

    @Override
    public void run() {
        logger.info("本地服务器监听线程已启动，线程号：{}", listen.getId());
        while (true) {
            try {
                Socket sock = serverSocket.accept();
                logger.info("监听到来自{}的连接。", sock.getRemoteSocketAddress());
                TcpHandle tcpHandle = new TcpHandle(sock);
                containerManage.injection(tcpHandle);
                es.submit(tcpHandle);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }


}
