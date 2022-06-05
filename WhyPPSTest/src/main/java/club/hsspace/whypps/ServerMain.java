package club.hsspace.whypps;

import club.hsspace.whypps.action.Container;
import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.action.Scan;
import club.hsspace.whypps.debug.DebugKey;
import club.hsspace.whypps.manage.LocalHost;
import club.hsspace.whypps.run.WhyPPSApplication;

import java.util.List;

@Scan("club.hsspace.whypps.ServerMain")
@DebugKey(password = "admin")
@Container(register = false)
public class ServerMain {

    public static void main(String[] args) {
        WhyPPSApplication.run(ServerMain.class);
    }

    public static List<String> getCertificates() {
        return List.of("/whypps/Certificate.ce");
    }

    public static String getDefaultConfiguration() {
        return "/whypps/server.properties";
    }

}
