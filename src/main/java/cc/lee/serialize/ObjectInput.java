package cc.lee.serialize;

import java.io.IOException;

/**
 * Created by lizhitao on 16-3-15.
 */
public interface ObjectInput {
    /**
     * readObject
     *
     * @return Object
     * @throws IOException
     * @throws ClassNotFoundException
     */
    Object readObject() throws IOException, ClassNotFoundException;

    /**
     * readObject
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    <T> T readObject(Class<T> clazz) throws IOException, ClassNotFoundException;
}
