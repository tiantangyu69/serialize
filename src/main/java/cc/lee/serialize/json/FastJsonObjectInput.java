package cc.lee.serialize.json;

import cc.lee.serialize.ObjectInput;
import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by lizhitao on 16-3-15.
 */
public class FastJsonObjectInput implements ObjectInput {
    private BufferedReader reader;

    public FastJsonObjectInput(InputStream in){
        reader = new BufferedReader(new InputStreamReader(in));
    }

    public Object readObject() throws IOException, ClassNotFoundException {
        String json = reader.readLine();
        return JSON.parseObject(json);
    }

    public <T> T readObject(Class<T> clazz) throws IOException, ClassNotFoundException {
        String json = reader.readLine();
        return JSON.parseObject(json, clazz);
    }
}
