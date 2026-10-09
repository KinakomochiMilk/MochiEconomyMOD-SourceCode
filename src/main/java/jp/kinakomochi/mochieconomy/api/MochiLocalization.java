package jp.kinakomochi.mochieconomy.api;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class MochiLocalization {

    private static final String RESOURCE_BASE =
            "/assets/mochieconomy/lang/";

    private static final String DEFAULT_LOCALE = "en_us";

    private static final Gson GSON = new Gson();

    private static final Map<String, String> TRANSLATIONS =
            new HashMap<>();

    private static String currentLocale = DEFAULT_LOCALE;

    private static boolean initialized = false;

    private MochiLocalization() {
    }

    /**
     * Localization APIを初期化します。
     */
    public static void initialize(String locale) {
        currentLocale = normalizeLocale(locale);

        TRANSLATIONS.clear();

        // 現在の言語を読み込む
        loadLocale(currentLocale, TRANSLATIONS);

        // 見つからなかったキーを英語で補完する
        if (!DEFAULT_LOCALE.equals(currentLocale)) {
            loadLocale(DEFAULT_LOCALE, TRANSLATIONS, false);
        }

        initialized = true;
    }

    /**
     * 指定されたキーから文字列を取得します。
     */
    public static String get(String key) {
        if (!initialized) {
            initialize(DEFAULT_LOCALE);
        }

        String value = TRANSLATIONS.get(key);

        if (value == null) {
            return key;
        }

        return value;
    }

    /**
     * 現在使用しているロケールを取得します。
     */
    public static String getCurrentLocale() {
        return currentLocale;
    }

    /**
     * 指定されたロケールを設定します。
     */
    public static void setLocale(String locale) {
        initialize(locale);
    }

    private static void loadLocale(
            String locale,
            Map<String, String> target
    ) {
        loadLocale(locale, target, true);
    }

    private static void loadLocale(
            String locale,
            Map<String, String> target,
            boolean overwrite
    ) {
        String normalized = normalizeLocale(locale);

        String resourcePath =
                RESOURCE_BASE + normalized + ".json";

        try (InputStream inputStream =
                     MochiLocalization.class
                             .getResourceAsStream(resourcePath)) {

            if (inputStream == null) {
                return;
            }

            try (Reader reader =
                         new InputStreamReader(
                                 inputStream,
                                 StandardCharsets.UTF_8
                         )) {

                JsonObject json = GSON.fromJson(
                        reader,
                        JsonObject.class
                );

                if (json == null) {
                    return;
                }

                for (Map.Entry<String, JsonElement> entry
                        : json.entrySet()) {

                    JsonElement element = entry.getValue();

                    if (!element.isJsonPrimitive()
                            || !element.getAsJsonPrimitive()
                            .isString()) {
                        continue;
                    }

                    if (overwrite || !target.containsKey(entry.getKey())) {
                        target.put(
                                entry.getKey(),
                                element.getAsString()
                        );
                    }
                }
            }

        } catch (Exception e) {
            System.err.println(
                    "[MochiLocalization] Failed to load: "
                            + resourcePath
            );

            e.printStackTrace();
        }
    }

    private static String normalizeLocale(String locale) {
        if (locale == null || locale.trim().isEmpty()) {
            return DEFAULT_LOCALE;
        }

        return locale
                .trim()
                .toLowerCase()
                .replace('-', '_');
    }
}