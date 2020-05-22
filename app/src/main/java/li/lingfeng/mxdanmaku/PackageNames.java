package li.lingfeng.mxdanmaku;

public class PackageNames {

    public static final String MX_PLAYER_PRO = "com.mxtech.videoplayer.pro";
    public static final String MX_PLAYER_FREE = "com.mxtech.videoplayer.ad";

    public static boolean isMXPlayer(CharSequence _packageName) {
        if (_packageName == null) {
            return false;
        }
        String packageName = _packageName.toString();
        return MX_PLAYER_PRO.equals(packageName) || MX_PLAYER_FREE.equals(packageName);
    }
}
