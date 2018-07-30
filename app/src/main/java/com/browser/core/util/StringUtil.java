package com.browser.core.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    public static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static String findContent(String data, String key) {
        Matcher matcher = Pattern.compile(key).matcher(data);
        return matcher.find() ? matcher.group(1) : "";
    }


    public static ArrayList<String> findListContent(String data, String key) {
        Matcher matcher = Pattern.compile(key).matcher(data);
        ArrayList<String> arrayList = new ArrayList();
        while (matcher.find()) {
            arrayList.add(matcher.group(1));
        }
        return arrayList;
    }

}
