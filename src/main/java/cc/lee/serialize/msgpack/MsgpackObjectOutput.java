package cc.lee.serialize.msgpack;

import cc.lee.serialize.ObjectOutput;
import org.msgpack.MessagePack;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by lizhitao on 16-3-15.
 */
public class MsgpackObjectOutput implements ObjectOutput {
    private MessagePack messagePack;
    private OutputStream out;

    public MsgpackObjectOutput(OutputStream out) {
        messagePack = new MessagePack();
        this.out = out;
    }

    public void writeObject(Object obj) throws IOException {
        messagePack.write(out, obj);
    }
}
