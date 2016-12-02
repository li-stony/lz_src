package lz.common;

/**
 * Created by cussyou on 2016-06-07.
 */
public class LzException extends Throwable {
    private String msg = null;
    public LzException(String msg) {
        this.msg = msg;
    }
    @Override
    public String toString() {
        return msg;
    }

}
