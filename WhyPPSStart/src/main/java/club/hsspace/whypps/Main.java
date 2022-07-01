package club.hsspace.whypps;

import java.io.InputStream;

public class Main {

    public static void main(String[] args) throws WhyPPSFrameworkNotFoundException {

        ArgsManage argsManage = new ArgsManage(args);

        LibManage libManage = new LibManage(argsManage);

    }

}