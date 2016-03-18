package cc.lee.serialize.test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by lizhitao on 16-3-16.
 */
public class OtherTest {
    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException {
        /*System.out.println(System.getProperty("java.version"));
        System.out.println(System.getProperty("java.vendor"));
        System.out.println(System.getProperty("java.vendor.url"));
        System.out.println(System.getProperty("java.home"));
        System.out.println(System.getProperty("java.class.path"));
        System.out.println(System.getProperty("java.io.tmpdir"));
        System.out.println(System.getProperty("java.compiler"));
        System.out.println(System.getProperty("java.ext.dirs"));
        System.out.println(System.getProperty("os.name"));
        System.out.println(System.getProperty("os.arch"));
        System.out.println(System.getProperty("os.version"));
        System.out.println(System.getProperty("file.separator"));
        System.out.println(System.getProperty("path.separator"));
        System.out.println(System.getProperty("line.separator"));
        System.out.println(System.getProperty("user.name"));
        System.out.println(System.getProperty("user.home"));
        System.out.println(System.getProperty("user.dir"));

        System.out.println(System.getProperties());
        String[] array = new String[]{"a", "b"};*/
//        System.out.println(Thread.currentThread().getContextClassLoader());
//        String a = String.class.getConstructor(String.class).newInstance("aaa");
//        List<String> list = new ArrayList<String>();
//        System.out.println(int.class.isPrimitive());


        ClassLoader myClassLoader = new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                try {
                    String fileName = name.substring(name.lastIndexOf(".") + 1) + ".class";
                    InputStream in = getClass().getResourceAsStream(fileName);
                    if (null == in)
                        return super.loadClass(name);
                    byte[] b = new byte[in.available()];
                    in.read(b);
                    return defineClass(name, b, 0, b.length);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new ClassNotFoundException(name);
                }
            }
        };


        Object test = myClassLoader.loadClass("cc.lee.serialize.test.OtherTest").newInstance();
        System.out.println(test.getClass());
        System.out.println(test instanceof OtherTest);
    }
}
