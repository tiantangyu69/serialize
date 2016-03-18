package cc.lee.serialize.test;

import javax.xml.transform.sax.SAXSource;

class Super {
    static {
        System.out.println("===");
    }

    {
        System.out.println("+++++");
    }

    Super() {
        System.out.println("______");

    }

}

public class StaticTest {
//    public static final String aa = "aaaa";
    public static String bb = "bbbb";

    static {
        System.out.println("class init");
        System.out.println();
    }

    {
        System.out.println("bbbb");
    }
    /*static StaticTest st = new StaticTest();
    int a = 110;
    static int b = 112;

    static {
        System.out.println("1");
    }

    {
        System.out.println("2");
    }

    StaticTest() {
        System.out.println("3");
        System.out.println("a=" + a + ",b=" + b);
    }

    public static void staticFunction() {
        System.out.println("4");
    }*/


    public static void main(String[] args) {
//        System.out.println(StaticTest.bb);
//        staticFunction();
    }
}