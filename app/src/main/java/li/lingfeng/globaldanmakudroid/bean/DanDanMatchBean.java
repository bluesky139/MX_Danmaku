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

        @Override
        public String toString() {
            return "Match@" + hashCode() + " episodeId " + episodeId + ", animeId " + animeId
                    + ", animeTitle " + animeTitle + ", episodeTitle " + episodeTitle;
        }
    }

    @Override
    public String toString() {
        if (errorCode == 0) {
            return "DanDanMatchBean@" + hashCode() + " isMatched " + isMatched + ", matches " + matches.size()
                    + (matches.size() > 0 ? "(" + matches.get(0) + ")" : "");
        } else {
            return "DanDanMatchBean@" + hashCode() + " errorCode " + errorCode + ", errorMessage " + errorMessage;
        }
    }
}
