package club.hsspace.whypps;

import club.hsspace.whypps.framework.manage.EventListener;
import club.hsspace.whypps.framework.manage.event.BeforeRequestHandleEvent;
import club.hsspace.whypps.framework.plugin.ScanPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ScanPlugin
public class Plugin {

    private static final Logger logger = LoggerFactory.getLogger(Plugin.class);

    @EventListener
    public void listenAfterRequestHandleEvent(BeforeRequestHandleEvent event) {
        logger.info("listener: {}-{}-{}", event.getRunMethod(), event.getObject().toString(), event.getObjects());
    }

}