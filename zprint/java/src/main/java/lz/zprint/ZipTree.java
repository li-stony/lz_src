package lz.zprint;

import java.io.IOException;
import java.util.zip.ZipFile;

/**
 * Created by cussyou on 20160727.
 */
public class ZipTree {
    ZipItem root;
    public ZipTree() {

    }
    public boolean parse(String zipfile, int level) {
        try {
            ZipFile zipFile = new ZipFile(zipfile);
            root = new ZipItem(null, "/");
            zipFile.entries();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
