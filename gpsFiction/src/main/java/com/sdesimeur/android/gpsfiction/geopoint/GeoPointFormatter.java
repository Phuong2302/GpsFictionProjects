package com.sdesimeur.android.gpsfiction.geopoint;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Formatting of GeoPoint.
 */
public class GeoPointFormatter {
    private static final Pattern pattern = Pattern.compile("%([yx])(\\d)?([ndms])");
    private final String format;
    private final Format enumFormat;

    /**
     * Creates a new formatter with given format-string.
     *
     * @param format the format-string
     * @see format()
     */
    public GeoPointFormatter(final String format) {
        enumFormat = null;
        this.format = format;
    }

    /**
     * Creates a new formatter with given default-format.
     *
     * @param format one of the default formats
     * @see GeoPointFormatter.Format
     */
    public GeoPointFormatter(final Format format) {
        enumFormat = format;
        this.format = format.toString();
    }

    /**
     * Formats a GeoPoint.
     * <p/>
     * Syntax:
     * %[dir][precision][value]
     * <p/>
     * [dir]
     * y = latitude
     * x = longitude
     * <p/>
     * [precision] (optional)
     * 0..9, number of digits after the decimal point
     * <p/>
     * [value]
     * n = direction
     * d = degree
     * m = minute
     * s = second
     * <p/>
     * Example:
     * "%yn %yd° %y3m" = "N 52° 36.123"
     * <p/>
     * All other characters are not interpreted and can be used.
     *
     * @param gp     the GeoPoint to format
     * @param format the format-string with syntax from above
     * @return the formatted coordinates
     */
    public static String format(final String format, final GeoPoint gp) {
        final Matcher matcher = pattern.matcher(format);
        final StringBuffer formattedResult = new StringBuffer();

        while (matcher.find()) {
            StringBuilder replacement = new StringBuilder();

            final double coord = (matcher.group(1).equals("y")) ? gp.getLatitude() : gp.getLongitude();

            if (matcher.group(3).equals("n")) {
                if (matcher.group(1).equals("y")) {
                    replacement.append((coord < 0) ? "S" : "N");
                } else {
                    replacement.append((coord < 0) ? "W" : "E");
                }
            } else if (matcher.group(3).equals("d")) {
                if (null == matcher.group(2)) {
                    replacement.append(String.format("%0" + ((matcher.group(1).equals("y")) ? "2." : "3.") + "0f", Math.floor(Math.abs(coord))));
                } else {
                    replacement.append(String.format("%0" + ((matcher.group(1).equals("y")) ? "2." : "3.") + Integer.parseInt(matcher.group(2)) + "f", coord));
                }
            } else if (matcher.group(3).equals("m")) {
                final double value = Math.abs(coord);
                final double minutes = (value - Math.floor(value)) * 60;
                replacement.append(String.format("%02." + ((null == matcher.group(2)) ? 0 : Integer.parseInt(matcher.group(2))) + "f", (null == matcher.group(2)) ? Math.floor(minutes) : minutes));
            } else if (matcher.group(3).equals("s")) {
                final double value = Math.abs(coord);
                final double minutes = (value - Math.floor(value)) * 60;
                replacement.append(String.format("%02." + ((null == matcher.group(2)) ? 0 : Integer.parseInt(matcher.group(2))) + "f", (minutes - Math.floor(minutes)) * 60));
            }

            matcher.appendReplacement(formattedResult, replacement.toString());
        }

        matcher.appendTail(formattedResult);

        return formattedResult.toString();
    }

    /**
     * Formats a GeoPoint.
     *
     * @param gp     the GeoPoint to format
     * @param format one of the default formats
     * @return the formatted coordinates
     * @see cgeo.geocaching.GeoPointFormatter.Format
     */
    public static String format(final Format format, final GeoPoint gp) {
        // Don't parse often used formats

        switch (format) {
            case LAT_LON_DECDEGREE:
                return String.format("%.6f %.6f", gp.getLatitude(), gp.getLongitude());

            case LAT_LON_DECMINUTE:
                final double lat = Math.abs(gp.getLatitude());
                final double lon = Math.abs(gp.getLongitude());
                final boolean latPos = (gp.getLatitude() < 0);
                final boolean lonPos = (gp.getLongitude() < 0);

                return String.format("%s %02.0f° %.3f %s %03.0f° %.3f", (latPos) ? "S" : "N",
                        Math.floor(lat),
                        (lat - Math.floor(lat)) * 60,
                        (lonPos) ? "W" : "E",
                        Math.floor(lon),
                        (lon - Math.floor(lon)) * 60);

            default:
                return format(format.toString(), gp);
        }
    }

    /**
     * Formats a GeoPoint with the format of this instance.
     *
     * @param gp the GeoPoint to format
     * @return the formatted coordinates of the GeoPoint
     */
    public String format(final GeoPoint gp) {
        if (null == enumFormat) {
            return format(format, gp);
        } else {
            return format(enumFormat, gp);
        }
    }

    /**
     * Returns the format of this instance.
     *
     * @return the format of this instance.
     */
    public String toString() {
        return format;
    }

    /**
     * A few default formats. They can be parsed hardcoded, so the use of them
     * can improve the performance.
     */
    public enum Format {
        LAT_LON_DECDEGREE("%y6d %x6d"),
        LAT_LON_DECMINUTE("%yn %yd° %y3m %xn %xd° %x3m"),
        LAT_LON_DECSECOND("%yn %yd° %ym' %ys\" %xn %xd° %xm' %xs\""),
        LAT_DECDEGREE("%y6d"),
        LAT_DECMINUTE("%yn %yd° %y3m"),
        LAT_DECSECOND("%yn %yd° %ym' %ys\""),
        LON_DECDEGREE("%x6d"),
        LON_DECMINUTE("%xn %xd° %x3m"),
        LON_DECSECOND("%xn %xd° %xm' %xs\"");

        private final String format;

        Format(String format) {
            this.format = format;
        }

        public String toString() {
            return format;
        }
    }
}
