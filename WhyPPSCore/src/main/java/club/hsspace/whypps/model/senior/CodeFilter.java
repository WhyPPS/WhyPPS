package club.hsspace.whypps.model.senior;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @ClassName: CodeFilter
 * @CreateTime: 2022/4/26
 * @Comment:
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class CodeFilter implements ObjectSerializer, ObjectDeserializer {

    @Override
    public Code deserialze(DefaultJSONParser defaultJSONParser, Type type, Object o) {
        int i = defaultJSONParser.lexer.intValue();
        return Code.of(i);
    }

    @Override
    public int getFastMatchToken() {
        return JSONToken.LITERAL_INT;
    }

    @Override
    public void write(JSONSerializer jsonSerializer, Object o, Object o1, Type type, int i) throws IOException {
        if(o instanceof Code code)
            jsonSerializer.write(code.code());
    }
}
