package lizl.me;

import java.util.HashMap;

public class TestMain {
    public static void main(String[] args) {
        HashMap<String, String> re = UrlUtil.parseParams("jdbc:mysql://10.10.1.15:3306/fastooth?user=dewmobile&password=dewmobile");
        for(String k : re.keySet()) {
            System.out.println(k+"="+re.get(k));
        }
    }
}
