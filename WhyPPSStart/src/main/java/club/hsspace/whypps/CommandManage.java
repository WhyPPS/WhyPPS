package club.hsspace.whypps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName: CommandManage
 * @CreateTime: 2022/8/11
 * @Comment: 命令管理器 架构不接入ContainerManage
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class CommandManage extends Thread implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(CommandManage.class);

    private static CommandManage commandManage = new CommandManage();

    private BufferedReader br;

    private CommandManage() {
        br = new BufferedReader(new InputStreamReader(System.in));
        setName("cmd");
    }

    public static CommandManage getCommandManage() {
        return commandManage;
    }

    private Map<String, List<CommandCallback>> commandHandles = new HashMap<>();

    {
        registerCommandHandle("help", new CommandCallback() {
            @Override
            public void handleCommand(String[] command) {
                System.out.println("===========" + "命令帮助面板" + "===========");
                AtomicInteger cmdCount = new AtomicInteger(1);
                commandHandles.keySet().stream()
                        .flatMap(key -> commandHandles.get(key).stream()
                                .map(commandCallback -> Map.entry(key, commandCallback)))
                        .map(n -> String.format("%-2d.%6s - %s", cmdCount.getAndIncrement(), n.getKey(), n.getValue().commandHelper()))
                        .forEach(System.out::println);
                System.out.println("===========" + "共计" + cmdCount.get() + "指令" + "===========");
            }

            @Override
            public String commandHelper() {
                return "提示系统注册的所有指令";
            }
        });
    }

    public void registerCommandHandle(String command, CommandCallback commandCallback) {
        List<CommandCallback> cc = commandHandles.get(command);
        if (cc == null) {
            cc = new ArrayList<>();
            commandHandles.put(command, cc);
        }
        cc.add(commandCallback);
    }

    @Override
    public void run() {
        String line;
        try {
            while ((line = br.readLine()) != null) {
                String[] s = line.trim().split(" ");
                if (s.length > 0) {
                    List<CommandCallback> commandCallbacks = commandHandles.get(s[0]);
                    if (commandCallbacks != null) {
                        for (CommandCallback commandCallback : commandCallbacks) {
                            commandCallback.handleCommand(s);
                        }
                    } else {
                        System.out.println("未知命令，请确认指令正确且已注册，您可以键入help查看指令集");
                    }
                }
            }
        } catch (IOException e) {
            logger.info("命令管理监控已关闭");
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() throws IOException {
        System.in.close();
    }
}
