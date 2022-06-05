
import club.hsspace.whypps.framework.manage.XMLConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @ClassName: TastXMLConfiguration
 * @CreateTime: 2022/6/5
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class TastXMLConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(TastXMLConfiguration.class);

    XMLConfiguration xmlConfiguration;

    @Before
    public void init() throws ParserConfigurationException, IOException, SAXException {
        xmlConfiguration = new XMLConfiguration(new File("F:\\project\\WhyPPS\\run\\whypps_config.xml"));
    }

    @Test
    public void testGetString() {
        String port = xmlConfiguration.getString("localServer.server.port.%");
        assertEquals(port, "2683");

        String enable = xmlConfiguration.getString("localServer.server.enable");
        assertEquals(enable, "true");
    }
    
}
