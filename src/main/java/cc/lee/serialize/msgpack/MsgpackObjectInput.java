package cc.lee.serialize.msgpack;

import cc.lee.serialize.ObjectInput;
import org.msgpack.MessagePack;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lizhitao on 16-3-15.
 */
public class MsgpackObjectInput implements ObjectInput {
    private MessagePack messagePack;
    private InputStream in;

    public MsgpackObjectInput(InputStream in) {
        messagePack = new MessagePack();
        this.in = in;
    }

    public Object readObject() throws IOException, ClassNotFoundException {
        return messagePack.read(in);
    }

    @SuppressWarnings("unchecked")
    public <T> T readObject(Class<T> clazz) throws IOException, ClassNotFoundException {
        return (T) readObject();
    }
}
