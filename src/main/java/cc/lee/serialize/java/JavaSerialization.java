package cc.lee.serialize.java;

import cc.lee.serialize.ObjectInput;
import cc.lee.serialize.ObjectOutput;
import cc.lee.serialize.Serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by lizhitao on 16-3-15.
 * 使用Java原生API序列化反序列化对象
 */
public class JavaSerialization implements Serialization {
    public ObjectInput deserialize(InputStream in) throws IOException {
        return new JavaObjectInput(in);
    }

    public ObjectOutput serialize(OutputStream out) throws IOException {
        return new JavaObjectOutput(out);
    }
}
