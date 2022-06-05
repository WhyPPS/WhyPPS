package club.hsspace.whypps.framework.manage;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class XMLConfiguration {

    private Document doc;

    private Element root;

    private FileManage fileManage;

    public XMLConfiguration(FileManage fileManage) throws ParserConfigurationException, IOException, SAXException {
        this.fileManage = fileManage;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        doc = db.parse(fileManage.getFile("whypps_config.xml"));
        root = doc.getDocumentElement();
    }

    /* 测试方法，方便测试使用 */
    public XMLConfiguration(File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        doc = db.parse(file);
        root = doc.getDocumentElement();
    }

    /**
     * 读取配置，key填入到叶子节点的路径，以'.'分割，若最后节点为“%”占位符，则最终读取文本，否则读取属性
     * 路径需唯一，不可出现重复标签，出现则以第一次出现为准
     * eg1：localServer.server.port.%
     * eg2: localServer.server.enable
     *
     * @param key
     * @return
     */
    public String getString(String key) {
        List<String> result = getStrings(key);
        if (result.size() != 0)
            return result.get(0);
        return null;
    }

    public List<String> getStrings(String key) {
        String[] split = key.split("\\.");
        Deque<String> task = new LinkedList<>();
        for (String s : split) {
            task.addLast(s);
        }
        List<String> result = new ArrayList<>();
        taskAnalysis(result, root, task);
        return result;
    }

    public String getString(Configuration configuration) {
        String string = getString(configuration.key);
        if (string == null)
            return configuration.defaultValue;
        return string;
    }

    public List<String> getStrings(Configuration configuration) {
        return getStrings(configuration.key);
    }

    /**
     * 取内容：'%'
     * 只处理首次标签：'s'
     * 扁平化处理标签：'s*'
     *
     * @param result
     * @param root
     * @param task
     */
    public void taskAnalysis(List<String> result, Element root, Deque<String> task) {
        String t = task.removeFirst();

        if (t.endsWith("%")) {
            String con = root.getTextContent();
            result.add(contextReplace(con));
        } else if (t.endsWith("*")) {
            NodeList nodeList = root.getElementsByTagName(t.replace("*", ""));
            String son = task.removeFirst();
            for (int i = 0; i < nodeList.getLength(); i++) {
                task.addFirst(son);
                taskAnalysis(result, (Element) nodeList.item(i), task);
            }
        } else {
            NodeList node = root.getElementsByTagName(t);
            if (node.getLength() != 0) {
                taskAnalysis(result, (Element) node.item(0), task);
            } else if (task.size() == 0) {
                String con = root.getAttribute(t);
                result.add(contextReplace(con));
            }
        }

    }

    private String contextReplace(String context) {
        return context.replace("${runPath}", fileManage.getRunPath());
    }

    public enum Configuration {
        LOCALSERVER_SERVER_PORT("localServer.server.port.%", "2683"),
        LOCALSERVER_SERVER_ENABLE("localServer.server.enable", "true"),
        LOCALSERVER_SERVER_AUTODISTRIBUTION("localServer.server.autoDistribution", "true"),

        CERTIFICATES_DIRPATH("certificates.dir*.path"),

        AUTHORITY_PRIVATEKEY_ENABLE("authority.privateKey.enable", "false"),
        AUTHORITY_PRIVATEKEY_CIPHER("authority.privateKey.cipher.file"),
        AUTHORITY_PRIVATEKEY_PASSWORD("authority.privateKey.password.file"),

        AUTHORITY_SETTING_EFFECTIVETIME("authority.setting.effectiveTime", "1800"),

        DEBUG_DEBUGKEY("debug.debugkey.%");


        public String key;

        public String defaultValue;

        Configuration(String key) {
            this.key = key;
        }

        Configuration(String key, String defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }
    }
}
