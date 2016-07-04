package lz.coord;

import com.lbt05.EvilTransform.GCJPointer;
import com.lbt05.EvilTransform.WGSPointer;

/**
 * Created by cussyou on 2016-07-04.
 */
public class CoordTrans {
    private static void printHelp() {
        System.out.println("CoordTrans -srcType [gcj02|wgs] -dstType[gcj02|wgs] -location lat lon");
    }
    public static void main(String[] args) {
        ArgsData argsData = new ArgsData();
        if(!argsData.parse(args)) {
            printHelp();
        }
        if(argsData.srcType.equals("gcj02")){
            GCJPointer pointer = new GCJPointer(argsData.lat, argsData.lon);
            WGSPointer dst = pointer.toExactWGSPointer();
            System.out.println(String.format("%f,%f", dst.getLatitude(), dst.getLongitude()));
        } else if(argsData.srcType.equals("wgs")){
            WGSPointer pointer = new WGSPointer(argsData.lat, argsData.lon);
            GCJPointer dst = pointer.toGCJPointer();
            System.out.println(String.format("%f,%f", dst.getLatitude(), dst.getLongitude()));
        }
    }
}
