package rw.companyz.useraccountms.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateUtility {

    public static LocalDate getTodayDateInKigaliTimezone() {
        return LocalDate.now(ZoneId.of("Africa/Kigali"));
    }

    public static LocalDateTime getTodayDateTimeInKigaliTimezone() {return LocalDateTime.now(ZoneId.of("Africa/Kigali"));}

}

