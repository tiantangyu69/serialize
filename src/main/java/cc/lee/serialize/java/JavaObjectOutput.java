package cc.lee.serialize.java;

import cc.lee.serialize.ObjectOutput;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Created by lizhitao on 16-3-15.
 */
public class JavaObjectOutput implements ObjectOutput {
    private ObjectOutputStream outputStream;

    public JavaObjectOutput(OutputStream out) throws IOException {
        outputStream = new ObjectOutputStream(out);
    }

    public void writeObject(Object obj) throws IOException {
        outputStream.writeObject(obj);
    }
}
