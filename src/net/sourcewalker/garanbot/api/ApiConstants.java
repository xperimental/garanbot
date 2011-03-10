package net.sourcewalker.garanbot.api;

import java.text.SimpleDateFormat;
import java.util.Locale;

final class ApiConstants {

    public static final String BASE = "https://www.garanbo.de/rest/v1";

    public static final String KEY = "";

    public static final SimpleDateFormat HTTP_DATE_FORMAT = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);

}
