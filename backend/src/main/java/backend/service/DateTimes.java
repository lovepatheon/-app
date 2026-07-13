package backend.service;

import java.time.*;

final class DateTimes {
    static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

    private DateTimes() {}

    static OffsetDateTime offset(Instant instant) {
        return instant == null ? null : instant.atZone(ZONE).toOffsetDateTime();
    }

    static Instant startOfDay(LocalDate date) {
        return date.atStartOfDay(ZONE).toInstant();
    }
}
