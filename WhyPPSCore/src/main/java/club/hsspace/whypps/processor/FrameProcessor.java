package club.hsspace.whypps.processor;

import club.hsspace.whypps.handle.DataStream;
import club.hsspace.whypps.model.DataFrame;

public interface FrameProcessor {

    void processorData(DataStream dataStream, DataFrame.DataSign dataSign, byte[] data);

}
