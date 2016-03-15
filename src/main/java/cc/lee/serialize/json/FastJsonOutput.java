package cc.lee.serialize.json;

import cc.lee.serialize.ObjectOutput;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by lizhitao on 16-3-15.
 */
public class FastJsonOutput implements ObjectOutput {
    private final PrintWriter writer;

    public FastJsonOutput(OutputStream out) {
        writer = new PrintWriter(out);
    }

    public void writeObject(Object obj) throws IOException {
        SerializeWriter out = new SerializeWriter();
        JSONSerializer serializer = new JSONSerializer(out);
        serializer.config(SerializerFeature.WriteEnumUsingToString, true);
        serializer.write(obj);
        out.writeTo(writer);
        writer.println();
        writer.flush();
    }
}
