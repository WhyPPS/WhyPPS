package club.hsspace.whypps.listener;

import club.hsspace.whypps.handle.DataStream;
import club.hsspace.whypps.model.DataLink;
import com.alibaba.fastjson.JSONObject;

public interface LinkListener {

    boolean listener(DataStream dataStream, DataLink dataLink);

}
