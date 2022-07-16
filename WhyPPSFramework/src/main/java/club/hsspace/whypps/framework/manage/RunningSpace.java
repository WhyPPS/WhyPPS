package club.hsspace.whypps.framework.manage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @ClassName: RunningSpace
 * @CreateTime: 2022/7/15
 * @Comment: 运行目录空间
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class RunningSpace {

    private static final Logger logger = LoggerFactory.getLogger(RunningSpace.class);

    private final Path runningSpace;

    public RunningSpace(String runningSpace) {
        this.runningSpace = Path.of(runningSpace);
    }

    public String getRunningSpace() {
        return runningSpace.toString();
    }

    public File getFile(String path) {
        return Path.of(runningSpace.toString(), path).toFile();
    }

    public InputStream getInputStream(String path) throws FileNotFoundException {
        return new FileInputStream(Path.of(runningSpace.toString(), path).toFile());
    }

    public OutputStream getOutputStream(String path) throws FileNotFoundException {
        return new FileOutputStream(Path.of(runningSpace.toString(), path).toFile());
    }

}
