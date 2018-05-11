package com.dev.base;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test() {
        String ip = "http://116.77.75.196:80/2Q2W7219F806F8455D59D0ED9BCB3CC9FDAE39225A22_unknown_06CC8FB6AA3F1543DF371AC83E1BA507BA5CA61A_7/ucan.25pp.com/Wandoujia_jyxx_hl.apk";
//        String ip = "http://down2.uc.cn/wandj/down.php?id=211&pub=jyxx_hl";
        Pattern p1 = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+)\\:(\\d+)");
        Matcher m1 = p1.matcher(ip);
        if (m1.find()) {
            ip = ip.replace(":" + m1.group(2), "");
        }
        System.out.println(ip);
    }

    @Test
    public void test2() {
        String str = "?hello,baby.-";
        String dot = "";
//        String ip = "http://down2.uc.cn/wandj/down.php?id=211&pub=jyxx_hl";
        Pattern pattern = Pattern.compile("\\pP");
        char c[] = str.toCharArray();
        for (int i = 0; i < c.length; i++) {
            Matcher matcher = pattern.matcher(String.valueOf(c[i]));
            if (matcher.matches() && c[i] != ',') {
                dot = dot + c[i];
            }
        }
        System.out.println(dot);
    }
}