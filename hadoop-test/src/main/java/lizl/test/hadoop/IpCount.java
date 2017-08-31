package lizl.test.hadoop;

import lizl.test.hadoop.ip.IpMap;
import lizl.test.hadoop.ip.IpReduce;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;

public class IpCount {


    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        Configuration conf = new Configuration();
        //GenericOptionsParser optionsParser = new GenericOptionsParser(conf, args);
        if(args.length != 2){
            StringBuilder sb = new StringBuilder();
            for(int i=0;i<args.length;i++) {
                sb.append(args[i]).append(" ");
            }
            System.out.println("error: "+sb.toString());
            System.err.println("usage: IpCount <in> <out>");
            System.exit(2);
        }

        Job job = Job.getInstance(conf, "ip_count");
        job.setJarByClass(IpCount.class);
        job.setMapperClass(IpMap.class);
        job.setReducerClass(IpReduce.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true)?0:1);
    }
}
