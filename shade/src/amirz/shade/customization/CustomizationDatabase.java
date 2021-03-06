package amirz.shade.customization;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.android.launcher3.BuildConfig;
import com.android.launcher3.R;
import com.android.launcher3.util.ComponentKey;

import java.util.HashMap;
import java.util.Map;

public class CustomizationDatabase {
    private static final String APP_ICON_PACK = BuildConfig.APPLICATION_ID + ".APP_ICON_PACK";
    private static final String APP_CATEGORY = BuildConfig.APPLICATION_ID + ".CATEGORY";

    private static final Map<String, String> CATEGORY_MAP = new HashMap<>();
    private static final Map<ComponentKey, String> CATEGORY_CACHE = new HashMap<>();

    // Icon pack
    public static String getIconPack(Context context, ComponentKey key) {
        String global = GlobalIconPackPreference.get(context);
        return getIconPackPrefs(context).getString(key.toString(), global);
    }

    public static void setIconPack(Context context, ComponentKey key, String value) {
        getIconPackPrefs(context).edit().putString(key.toString(), value).apply();
    }

    public static void clearIconPack(Context context, ComponentKey key) {
        getIconPackPrefs(context).edit().remove(key.toString()).apply();
    }

    private static SharedPreferences getIconPackPrefs(Context context) {
        return context.getSharedPreferences(APP_ICON_PACK, Context.MODE_PRIVATE);
    }

    // Category
    public static String getCategory(Context context, ComponentKey key) {
        if (CATEGORY_CACHE.containsKey(key)) {
            return CATEGORY_CACHE.get(key);
        }

        String category = getCategoryPrefs(context).getString(key.toString(), "");

        // Use automatic categorization for unknown categories
        if (category.isEmpty()) {
            category = AutoCategorize.getCategory(context, key);
            setCategory(context, key, category);
        }

        CATEGORY_CACHE.put(key, category);
        return category;
    }

    public static boolean isHidden(Context context, ComponentKey key) {
        return getCategory(context, key).equals("hidden");
    }

    public static String getCategoryString(Context context, ComponentKey key) {
        if (CATEGORY_MAP.isEmpty()) {
            Resources res = context.getResources();
            String[] names = res.getStringArray(R.array.category_entries);
            String[] values = res.getStringArray(R.array.category_entry_values);
            for (int i = 0; i < names.length; i++) {
                CATEGORY_MAP.put(values[i], names[i]);
            }
        }

        return CATEGORY_MAP.get(getCategory(context, key));
    }

    public static void setCategory(Context context, ComponentKey key, String value) {
        getCategoryPrefs(context).edit().putString(key.toString(), value).apply();
        CATEGORY_CACHE.put(key, value);
    }

    public static void clearCategory(Context context, ComponentKey key) {
        getCategoryPrefs(context).edit().remove(key.toString()).apply();
        CATEGORY_CACHE.remove(key);
    }

    private static SharedPreferences getCategoryPrefs(Context context) {
        return context.getSharedPreferences(APP_CATEGORY, Context.MODE_PRIVATE);
    }
}
