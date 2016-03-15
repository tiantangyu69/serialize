package cc.lee.serialize;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by lizhitao on 16-3-15.
 * 序列化接口
 */
public interface Serialization {
    /**
     * 反序列化
     * @param in
     * @return
     */
    ObjectInput deserialize(InputStream in) throws IOException;

    /**
     * 序列化
     * @param out
     */
    ObjectOutput serialize(OutputStream out) throws IOException;
}
