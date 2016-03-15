package cc.lee.serialize.java;

import cc.lee.serialize.ObjectInput;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;

/**
 * Created by lizhitao on 16-3-15.
 */
public class JavaObjectInput implements ObjectInput {
    ObjectInputStream inputStream;

    public JavaObjectInput(InputStream in) throws IOException {
        inputStream = new ObjectInputStream(in);
    }

    public Object readObject() throws IOException, ClassNotFoundException {
        return inputStream.readObject();
    }

    @SuppressWarnings("unchecked")
    public <T> T readObject(Class<T> clazz) throws IOException, ClassNotFoundException {
        return (T) readObject();
    }

    @SuppressWarnings("unchecked")
    public <T> T readObject(Class<T> clazz, Type type) throws IOException, ClassNotFoundException {
        Object value = readObject(clazz);
        return (T) readObject();
    }
}
