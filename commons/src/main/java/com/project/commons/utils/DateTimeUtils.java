package com.project.commons.utils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * Created by user on 13:20 05/07/2025, 2025
 */
public class DateTimeUtils {

    // Default ZoneId for operations that require a time zone
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Asia/Jakarta"); // Tajur Halang, West Java, Indonesia

    // --- Common DateTimeFormatter Options ---

    /** Formatter for "yyyy-MM-dd HH:mm:ss" (e.g., 2025-07-05 14:30:00) */
    public static final DateTimeFormatter FORMATTER_YYYY_MM_DD_HH_MM_SS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Formatter for "yyyy-MM-dd" (e.g., 2025-07-05) */
    public static final DateTimeFormatter FORMATTER_YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /** Formatter for "HH:mm:ss" (e.g., 14:30:00) */
    public static final DateTimeFormatter FORMATTER_HH_MM_SS = DateTimeFormatter.ofPattern("HH:mm:ss");

    /** Formatter for "dd/MM/yyyy HH:mm:ss" (e.g., 05/07/2025 14:30:00) */
    public static final DateTimeFormatter FORMATTER_DD_MM_YYYY_HH_MM_SS = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /** Formatter for "MM-dd-yyyy HH:mm:ss" (e.g., 07-05-2025 14:30:00) */
    public static final DateTimeFormatter FORMATTER_MM_DD_YYYY_HH_MM_SS = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");

    /** Formatter for "yyyyMMddHHmmss" (compact, e.g., 20250705143000) */
    public static final DateTimeFormatter FORMATTER_COMPACT_DATETIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /** Formatter for "yyyy-MM-dd'T'HH:mm:ss" (ISO-like, without zone, e.g., 2025-07-05T14:30:00) */
    public static final DateTimeFormatter FORMATTER_ISO_LOCAL_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /** Formatter for "E, MMM dd yyyy HH:mm:ss z" (RFC 1123 / HTTP Date header format, e.g., Sat, Jul 05 2025 14:30:00 +0700)
     * Note: 'z' will use short time zone name, 'Z' for offset, 'XXX' for ISO offset.
     * For full RFC 1123, consider using DateTimeFormatter.RFC_1123_DATE_TIME.
     * This example uses a custom pattern for demonstration.
     */
    public static final DateTimeFormatter FORMATTER_RFC_1123_LIKE = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z");

    /** Formatter for "hh:mm:ss a" (12-hour format with AM/PM, e.g., 02:30:00 PM) */
    public static final DateTimeFormatter FORMATTER_HH_MM_SS_AMPM = DateTimeFormatter.ofPattern("hh:mm:ss a");

    /** Formatter for "MMMM dd, yyyy" (e.g., July 05, 2025) */
    public static final DateTimeFormatter FORMATTER_MONTH_DAY_YEAR = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

    /** Formatter for "dd-MMM-yyyy" (e.g., 05-Jul-2025) */
    public static final DateTimeFormatter FORMATTER_DD_MMM_YYYY = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    // --- Common DateTimeFormatter Options for Instant (ISO 8601 formats) ---
    /** Formatter for ISO 8601 instant with milliseconds (e.g., 2025-07-05T07:30:00.123Z) */
    public static final DateTimeFormatter FORMATTER_ISO_INSTANT_MILLIS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    /** Formatter for ISO 8601 instant (standard, e.g., 2025-07-05T07:30:00Z) */
    public static final DateTimeFormatter FORMATTER_ISO_INSTANT = DateTimeFormatter.ISO_INSTANT;

    // -------------------------------------------------------------------------
    // --- Difference Calculation Methods (for epoch milliseconds) ---
    // -------------------------------------------------------------------------
    public static long differenceInMinutes(long epochMilliStart, long epochMilliEnd) {
        return Duration.ofMillis(epochMilliEnd - epochMilliStart).toMinutes();
    }

    public static long differenceInHours(long epochMilliStart, long epochMilliEnd) {
        return Duration.ofMillis(epochMilliEnd - epochMilliStart).toHours();
    }

    public static long differenceInDays(long epochMilliStart, long epochMilliEnd) {
        return Duration.ofMillis(epochMilliEnd - epochMilliStart).toDays();
    }

    // -------------------------------------------------------------------------
    // --- Difference Calculation Methods (for epoch Instant) ---
    // -------------------------------------------------------------------------
    public static long differenceInMinutes(Instant startInstant, Instant endInstant) {
        Objects.requireNonNull(startInstant, "Start Instant cannot be null");
        Objects.requireNonNull(endInstant, "End Instant cannot be null");
        return Duration.between(startInstant, endInstant).toMinutes();
    }

    public static long differenceInHours(Instant startInstant, Instant endInstant) {
        Objects.requireNonNull(startInstant, "Start Instant cannot be null");
        Objects.requireNonNull(endInstant, "End Instant cannot be null");
        return Duration.between(startInstant, endInstant).toHours();
    }

    public static long differenceInDays(Instant startInstant, Instant endInstant) {
        Objects.requireNonNull(startInstant, "Start Instant cannot be null");
        Objects.requireNonNull(endInstant, "End Instant cannot be null");
        return Duration.between(startInstant, endInstant).toDays();
    }
    // -------------------------------------------------------------------------
    // --- Formatting Methods (Instant) ---
    // -------------------------------------------------------------------------

    public static String formatInstant(Instant instant, DateTimeFormatter formatter) {
        Objects.requireNonNull(instant, "Instant cannot be null");
        Objects.requireNonNull(formatter, "Formatter cannot be null");
        // Instant is inherently UTC. When formatting with a pattern, it implicitly uses UTC.
        // For custom formatters that require a ZoneId, Instant.atZone() would be needed first.
        // However, for typical Instant formatting, ISO_INSTANT is preferred.
        return formatter.format(instant);
    }
    // -------------------------------------------------------------------------
    // --- Parsing Methods (Instant) ---
    // -------------------------------------------------------------------------

    /**
     * Parses an instant string into an Instant object using a specified formatter.
     *
     * @param instantString The instant string to parse.
     * @param formatter     The DateTimeFormatter to use.
     * @return The parsed Instant object.
     * @throws DateTimeParseException if the string cannot be parsed.
     * @throws NullPointerException   if instantString or formatter is null.
     */
    public static Instant parseInstant(String instantString, DateTimeFormatter formatter) {
        Objects.requireNonNull(instantString, "Instant string cannot be null");
        Objects.requireNonNull(formatter, "Formatter cannot be null");
        return Instant.from(formatter.parse(instantString));
    }

    /**
     * Parses an instant string into an Instant object using the default ISO 8601 instant formatter.
     *
     * @param instantString The instant string to parse.
     * @return The parsed Instant object.
     * @throws DateTimeParseException if the string cannot be parsed.
     * @throws NullPointerException   if instantString is null.
     */
    public static Instant parseInstant(String instantString) {
        return parseInstant(instantString, FORMATTER_ISO_INSTANT);
    }

    /**
     * Formats a LocalDateTime object into a string using a specified formatter.
     *
     * @param dateTime  The LocalDateTime object to format.
     * @param formatter The DateTimeFormatter to use.
     * @return The formatted date-time string.
     * @throws NullPointerException if dateTime or formatter is null.
     */
    public static String formatLocalDateTime(LocalDateTime dateTime, DateTimeFormatter formatter) {
        Objects.requireNonNull(dateTime, "LocalDateTime cannot be null");
        Objects.requireNonNull(formatter, "Formatter cannot be null");
        return dateTime.format(formatter);
    }

    /**
     * Formats a LocalDateTime object into a string using the default "yyyy-MM-dd HH:mm:ss" formatter.
     *
     * @param dateTime The LocalDateTime object to format.
     * @return The formatted date-time string.
     * @throws NullPointerException if dateTime is null.
     */
    public static String formatLocalDateTime(LocalDateTime dateTime) {
        return formatLocalDateTime(dateTime, FORMATTER_YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * Converts an epoch millisecond timestamp to a LocalDateTime object in the default time zone.
     *
     * @param epochMilli The timestamp in milliseconds since the epoch.
     * @return A LocalDateTime object representing the timestamp in the default time zone.
     */
    public static LocalDateTime toLocalDateTime(long epochMilli) {
        return Instant.ofEpochMilli(epochMilli).atZone(DEFAULT_ZONE_ID).toLocalDateTime();
    }

    /**
     * Converts a LocalDateTime object to an epoch millisecond timestamp in the default time zone.
     *
     * @param dateTime The LocalDateTime object to convert.
     * @return The timestamp in milliseconds since the epoch.
     * @throws NullPointerException if dateTime is null.
     */
    public static long toEpochMilli(LocalDateTime dateTime) {
        Objects.requireNonNull(dateTime, "LocalDateTime cannot be null");
        return dateTime.atZone(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
    }

    /**
     * Parses a date-time string into a LocalDateTime object using a specified formatter.
     *
     * @param dateTimeString The date-time string to parse.
     * @param formatter      The DateTimeFormatter to use.
     * @return The parsed LocalDateTime object.
     * @throws DateTimeParseException if the string cannot be parsed.
     * @throws NullPointerException   if dateTimeString or formatter is null.
     */
    public static LocalDateTime parseLocalDateTime(String dateTimeString, DateTimeFormatter formatter) {
        Objects.requireNonNull(dateTimeString, "Date-time string cannot be null");
        Objects.requireNonNull(formatter, "Formatter cannot be null");
        return LocalDateTime.parse(dateTimeString, formatter);
    }

    /**
     * Parses a date-time string into a LocalDateTime object using the default "yyyy-MM-dd HH:mm:ss" formatter.
     *
     * @param dateTimeString The date-time string to parse.
     * @return The parsed LocalDateTime object.
     * @throws DateTimeParseException if the string cannot be parsed.
     * @throws NullPointerException   if dateTimeString is null.
     */
    public static LocalDateTime parseLocalDateTime(String dateTimeString) {
        return parseLocalDateTime(dateTimeString, FORMATTER_YYYY_MM_DD_HH_MM_SS);
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(DEFAULT_ZONE_ID);
    }
    public static Instant nowInstant() {
        return Instant.now();
    }
    public static long nowEpochMilli() {
        return Instant.now().toEpochMilli();
    }

}
