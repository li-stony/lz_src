package lizl.me;

import java.util.HashMap;

public class UrlUtil {
    public static HashMap<String, String> parseParams(String url) {
        HashMap<String,String> result = new HashMap<String, String>();
        int start = url.indexOf('?');
        if (start == -1) {
            return result;
        }

        String param = url.substring(start+1);
        int sep = 0;
        sep = param.indexOf('&');
        if (sep == -1) {
            sep = param.length();
        }
        while(sep > 0){
            String tmp = param.substring(0, sep);

            int eq = tmp.indexOf('=');
            if (eq != -1) {
                String key = tmp.substring(0, eq);
                String value = tmp.substring(eq+1);
                result.put(key, value);
            } else {
                String key = tmp;
                result.put(key, "");
            }
            if (sep == param.length()) {
                break;
            }
            param = param.substring(sep+1);
            sep = param.indexOf('&');
            if (sep == -1) {
                sep = param.length();
            }
        }

        return result;
    }
}
