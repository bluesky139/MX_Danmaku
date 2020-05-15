package li.lingfeng.globaldanmakudroid.bean;

import java.util.List;

public class DanDanMatchBean {

    public boolean isMatched;
    public List<Match> matches;
    public int errorCode;
    public boolean success;
    public String errorMessage;

    public static class Match {

        public int episodeId;
        public int animeId;
        public String animeTitle;
        public String episodeTitle;
        public String type;
        public String typeDescription;
        public int shift;
    }
}
