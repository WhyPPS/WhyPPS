package club.hsspace.whypps;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

public class Main {

    public static void main(String[] args) throws WhyPPSFrameworkNotFoundException {

        ArgsManage argsManage = new ArgsManage(args);

        LibManage libManage = new LibManage(argsManage);

        String command = argsManage.getArgs("command");
        if ("true".equals(command)) {
            CommandManage cm = CommandManage.getCommandManage();

            cm.registerCommandHandle("stop", new CommandCallback() {
                @Override
                public void handleCommand(String[] command) {

                    try {
                        libManage.stop();
                        cm.close();
                    } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                             IllegalAccessException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public String commandHelper() {
                    return "关闭系统服务";
                }
            });

            cm.start();
        }

    }

}