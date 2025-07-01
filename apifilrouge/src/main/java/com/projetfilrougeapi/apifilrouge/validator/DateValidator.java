package com.projetfilrougeapi.apifilrouge.validator;

import java.time.LocalDateTime;
import java.util.Date;

public final class DateValidator {
    /**
     * Validates if the given date is in the past.
     *
     * @param date the date to validate
     * @return true if the date is in the past, false otherwise
     */
    public static boolean isAfterToday(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        } else if (date.before(new Date()))  {
            return false; // The date is in the past

        }else {
            return true; // The date is today or in the future
        }
    }
    /**
     * Validates if the given date is in the past.
     *
     * @param date the date to validate
     * @return true if the date is in the past, false otherwise
     */
    public static boolean isAfterToday(LocalDateTime date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        } else if (date.isBefore(LocalDateTime.now()))  {
            return false; // The date is in the past
        }else {
            return true; // The date is today or in the future
        }
    }
}
