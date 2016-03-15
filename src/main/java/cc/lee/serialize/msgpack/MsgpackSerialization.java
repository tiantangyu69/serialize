package cc.lee.serialize.msgpack;

import cc.lee.serialize.ObjectInput;
import cc.lee.serialize.ObjectOutput;
import cc.lee.serialize.Serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by lizhitao on 16-3-15.
 * 使用msgpack序列化反序列化对象
 */
public class MsgpackSerialization implements Serialization {

    public ObjectInput deserialize(InputStream in) {
        return new MsgpackObjectInput(in);
    }

    public ObjectOutput serialize(OutputStream out) throws IOException {
        return new MsgpackObjectOutput(out);
    }
}
