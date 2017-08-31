package lizl.test.hadoop.ip;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IpMap extends Mapper<Object,Text, Text, IntWritable> {
    private final static IntWritable one = new IntWritable(1);
    private final static Pattern p = Pattern.compile(".+-dubbo:check:(.+):(.+)");
    @Override
    public void map(Object key, Text value, Context contxt) throws IOException, InterruptedException {
        Matcher m = p.matcher(value.toString());
        if (m.matches()) {
            Text outKey = new Text();
            outKey.set(m.group(1));
            contxt.write(outKey, one);
        }
    }

    public static void testPatern(String str) {
        Matcher m = p.matcher(str);
        if(m.matches()) {
            System.out.println(m.group(2) + " " + m.group(1));
        } else {
            System.out.println("not match");
        }
    }
}
