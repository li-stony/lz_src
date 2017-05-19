package lizl.common;

import lizl.BaseTest;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cussyou on 17-1-9.
 */
public class CommonTest extends BaseTest {
    private void testDate() {
        Date d = new Date();
        //d.setTime(System.currentTimeMillis());
        d.setHours(23);
        d.setMinutes(59);
        SimpleDateFormat fmt1 = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
        System.out.println(fmt1.format(d));

        SimpleDateFormat fmt2 = new SimpleDateFormat("yyyy-MM-DD hh:mm");
        System.out.println(fmt2.format(d));
    }
    public void test() {
        testDate();
    }
}
