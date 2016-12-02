package lz.zprint;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by cussyou on 20160727.
 */
public class ZipItem  {
    WeakReference<ZipItem> parent;
    ArrayList<ZipItem> children ;
    long size;
    long compressSize;
    String name;

    public ZipItem(ZipItem parent, String name) {
        this.name = name;
        this.parent = new WeakReference<ZipItem>(parent);
        this.children = new ArrayList<ZipItem>();
    }

    public void addSize(long size, long csize) {
        this.size += size;
        this.compressSize += csize;
        if(parent != null && parent.get() != null) {
            parent.get().addSize(size, csize);
        }
    }

    public boolean addChild(ZipItem item) {
        int index = item.name.indexOf(this.name);
        if(index > 0 ){
            int lastSlash = item.name.lastIndexOf("/");
            if(lastSlash == index) {
                children.add(item);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }
}
