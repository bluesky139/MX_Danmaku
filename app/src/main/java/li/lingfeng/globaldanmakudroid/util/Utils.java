package li.lingfeng.globaldanmakudroid.util;

import android.util.SparseArray;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Utils {

    public static SparseArray<String> clsIntFieldsToStrings(Class cls, String prefix) {
        try {
            SparseArray<String> strings = new SparseArray<>();
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)
                        && field.getType() == int.class && field.getName().startsWith(prefix)) {
                    field.setAccessible(true);
                    strings.put(field.getInt(null), field.getName());
                }
            }
            return strings;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
