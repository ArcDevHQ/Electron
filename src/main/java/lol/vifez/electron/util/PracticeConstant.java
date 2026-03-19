package lol.vifez.electron.util;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.util.TimeZone;

public final class PracticeConstant {

    public static final TimeZone TIME_ZONE = TimeZone.getTimeZone("GMT");
    public static final JsonParser JSON_PARSER = new JsonParser();
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();
    public static final Gson PLAIN_GSON = new GsonBuilder().create();
    public static final Joiner SPACE_JOINER = Joiner.on(" ");
    public static final Joiner COMMA_JOINER = Joiner.on(", ");

    private PracticeConstant() {
        throw new AssertionError("No instance");
    }
}
