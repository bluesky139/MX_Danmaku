package li.lingfeng.globaldanmakudroid.bean;

import java.util.List;

public class DanDanSearchEpisodeBean {

    public boolean hasMore;
    public List<Anime> animes;
    public int errorCode;
    public boolean success;
    public String errorMessage;

    public static class Anime {

        public int animeId;
        public String animeTitle;
        public String type;
        public String typeDescription;
        public List<Episode> episodes;
    }

    public static class Episode {

        public int episodeId;
        public String episodeTitle;
    }
}
