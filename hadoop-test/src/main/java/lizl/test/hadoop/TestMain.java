package lizl.test.hadoop;

import lizl.test.hadoop.ip.IpMap;

public class TestMain {
    public static void main(String[] args) {
        IpMap.testPatern("a -dubbo:check:182.186.126.133:PK");
        IpMap.testPatern("2017-08-31 00:00:00.062 9301 [DubboServerHandler-10.160.0.5:19301-thread-200] INFO  application -dubbo:check:182.186.126.133:PK");
    }
}
