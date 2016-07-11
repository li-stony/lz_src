package lz.coord;

/**
 * Created by cussyou on 2016-07-04.
 */
public class ArgsData {
    String srcType;
    String dstType;
    double lat;
    double lon;

    public boolean parse(String[] args) {
        if(args == null) {
            return false;
        }
        try {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-srcType")) {
                    srcType = args[i+1];
                    i++;
                    continue;
                } else if(args[i].equals("-dstType")){
                    dstType = args[i+1];
                    i++;
                    continue;
                } else if(args[i].equals("-location")){
                    lat = Double.parseDouble(args[i+1]);
                    lon = Double.parseDouble(args[i+2]);
                    i = i+2;
                    continue;
                }

            }
        }catch(Exception ex) {
            ex.printStackTrace();
            return false;
        }
        if(srcType == null) {
            return false;
        }
        if(dstType == null) {
            return false;
        }
        if(srcType.equals(dstType)) {
            return false;
        }

        return true;
    }
}
