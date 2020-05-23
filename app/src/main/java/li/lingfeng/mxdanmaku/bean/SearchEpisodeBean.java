package li.lingfeng.mxdanmaku.bean;

import java.util.List;

public class SearchEpisodeBean {

    public boolean hasMore;
    public List<Anime> animes;
    public int errorCode;
    public boolean success;
    public String errorMessage;

    @Override
    public String toString() {
        if (errorCode == 0) {
            return "SearchEpisodeBean@" + hashCode() + " hasMore " + hasMore + ", animes " + animes.size();
        } else {
            return "SearchEpisodeBean@" + hashCode() + " errorCode " + errorCode + ", errorMessage " + errorMessage;
        }
    }

    public static class Anime {

        public int animeId;
        public String animeTitle;
        public String type;
        public String typeDescription;
        public List<Episode> episodes;

        @Override
        public String toString() {
            return "Anime@" + hashCode() + " animeId " + animeId + ", animeTitle " + animeTitle;
        }
    }

    public static class Episode {

        public int episodeId;
        public String episodeTitle;

        @Override
        public String toString() {
            return "Episode@" + hashCode() + " episodeId " + episodeId + ", episodeTitle " + episodeTitle;
        }
    }
}
