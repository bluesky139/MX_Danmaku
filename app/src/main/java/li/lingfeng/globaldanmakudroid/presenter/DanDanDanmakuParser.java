package li.lingfeng.globaldanmakudroid.presenter;

import android.graphics.Color;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import li.lingfeng.globaldanmakudroid.bean.DanDanCommentBean.Comment;
import li.lingfeng.globaldanmakudroid.util.Logger;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.util.DanmakuUtils;

import static master.flame.danmaku.danmaku.model.IDanmakus.ST_BY_TIME;

public class DanDanDanmakuParser extends BaseDanmakuParser {
    @Override
    protected IDanmakus parse() {
        Danmakus danmakus = new Danmakus(ST_BY_TIME, false, mContext.getBaseComparator());
        List<Comment> comments = (List<Comment>) mDataSource.data();
        for (int i = 0; i < comments.size(); ++i) {
            Comment comment = comments.get(i);
            try {
                String[] pStrings = StringUtils.split(comment.p, ',');
                int type = Integer.parseInt(pStrings[1]);
                if (type != 1 && type != 4 && type != 5) {
                    Logger.w("Ignore unknown comment type " + type + ", " + comment);
                    continue;
                }
                BaseDanmaku item = mContext.mDanmakuFactory.createDanmaku(type, mContext);
                item.setTime((long) (Float.parseFloat(pStrings[0]) * 1000));
                item.textSize = 25 * (mDispDensity - 0.6f);
                item.textColor = (int) ((0x00000000ff000000 | Long.parseLong(pStrings[2])) & 0x00000000ffffffff);
                item.textShadowColor = item.textColor <= Color.BLACK ? Color.WHITE : Color.BLACK;
                DanmakuUtils.fillText(item, comment.m);
                item.index = i;
                item.setTimer(mTimer);
                item.flags = mContext.mGlobalFlagValues;
                danmakus.addItem(item);
            } catch (Throwable e) {
                Logger.w("Parse comment exception, " + comment, e);
            }
        }
        Logger.d("Parsed " + comments.size() + " danmakus.");
        return danmakus;
    }
}
