package cc.lee.serialize.test;

import cc.lee.serialize.ObjectOutput;
import cc.lee.serialize.Serialization;
import cc.lee.serialize.java.JavaSerialization;
import cc.lee.serialize.json.FastJsonSerialization;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lizhitao on 16-3-15.
 */
public class MsgpackTest {

    public static void main(String[] args) throws Exception {


        List<User> list = new ArrayList<User>();
        list.add(new User("aaa", 11));
        list.add(new User("bbbb", 12));

        Serialization serialization = new JavaSerialization();
        File file = new File("/home/lizhitao/serailization");
        OutputStream out = new FileOutputStream(file);
        ObjectOutput objectOutput = serialization.serialize(out);
        objectOutput.writeObject(list);

        List<User> deList = (List<User>) serialization.deserialize(new FileInputStream(file)).readObject();
        for (User de : deList)
            System.out.println(de.getName() + ";" + de.getAge());
    }
}


class User implements Serializable{
    String name;
    Integer age;

    public User(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
