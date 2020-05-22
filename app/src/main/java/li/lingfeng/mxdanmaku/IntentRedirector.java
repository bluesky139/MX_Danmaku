package li.lingfeng.mxdanmaku;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import li.lingfeng.mxdanmaku.util.Logger;
import li.lingfeng.mxdanmaku.util.PackageUtils;

public class IntentRedirector extends Activity {

    private static String sFilePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.v("Redirect intent");
        Logger.intent(getIntent());
        sFilePath = getIntent().getDataString();

        Intent intent = (Intent) getIntent().clone();
        intent.setPackage(PackageUtils.isPackageInstalled(this, PackageNames.MX_PLAYER_PRO)
                ? PackageNames.MX_PLAYER_PRO : PackageNames.MX_PLAYER_FREE);
        intent.setComponent(null);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    public static String popFilePath() {
        String filePath = sFilePath;
        sFilePath = null;
        return filePath;
    }
}
