package li.lingfeng.mxdanmaku.bean;

import java.util.List;

public class DanDanCommentBean {

    public int count;
    public List<Comment> comments;
    public int errorCode = 0;
    public String errorMessage;

    @Override
    public String toString() {
        return "DanDanCommentBean@" + hashCode() + " count " + count;
    }

    public static class Comment {

        public int cid;
        public String p;
        public String m;

        @Override
        public String toString() {
            return "Comment@" + hashCode() + " cid " + cid + ", p " + p + ", m " + m;
        }
    }
}
