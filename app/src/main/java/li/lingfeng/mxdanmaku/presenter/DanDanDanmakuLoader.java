package li.lingfeng.mxdanmaku.presenter;

import java.io.InputStream;

import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.parser.IDataSource;

public class DanDanDanmakuLoader implements ILoader {
    @Override
    public IDataSource<?> getDataSource() {
        return null;
    }

    @Override
    public void load(String uri) throws IllegalDataException {

    }

    @Override
    public void load(InputStream in) throws IllegalDataException {

    }
}
