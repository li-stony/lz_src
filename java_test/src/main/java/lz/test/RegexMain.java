package lz.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cussyou on 2016-07-11.
 */
public class RegexMain {
    public static void  main(String[] args){
        Pattern CONTENT_DISPOSITION_PATTERN2 = Pattern
                .compile("attachment;\\s*filename\\s*=\\s*(.+)");
        Matcher m = CONTENT_DISPOSITION_PATTERN2.matcher("attachment;  filename=15.jpg");
        if(m.find()) {
            System.out.println(m.group(0));
            System.out.println(m.group(1));
        }
    }
}
