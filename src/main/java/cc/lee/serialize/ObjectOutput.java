package cc.lee.serialize;

import java.io.IOException;

/**
 * Created by lizhitao on 16-3-15.
 */
public interface ObjectOutput {
    /**
     * writeObject
     * @param obj
     * @throws IOException
     */
    void writeObject(Object obj) throws IOException;
}
