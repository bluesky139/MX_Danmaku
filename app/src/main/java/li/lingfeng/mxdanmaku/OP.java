package li.lingfeng.mxdanmaku;

import android.util.SparseArray;

import li.lingfeng.mxdanmaku.util.Utils;

public class OP {

    public static final int OP_CREATE       = 0;
    public static final int OP_SHOW_CONTROL = 1;
    public static final int OP_HIDE_CONTROL = 2;
    public static final int OP_SHOW_ALL     = 3;
    public static final int OP_HIDE_ALL     = 4;
    public static final int OP_SEEK_TO      = 5;
    public static final int OP_RESUME       = 6;
    public static final int OP_PAUSE        = 7;
    public static final int OP_DESTROY      = 8;

    public static SparseArray<String> sOpStrings = Utils.clsIntFieldsToStrings(OP.class, "OP_");
}
