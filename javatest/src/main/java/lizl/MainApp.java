package lizl;

import lizl.common.CommonTest;

/**
 * Created by cussyou on 17-1-9.
 */
public class MainApp {
    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<args.length;i++){
            sb.append(args[i]).append(" ");
        }
        System.out.println(sb.toString());
        System.out.println("hello, world");

        // new
        new CommonTest().test();
    }
}
