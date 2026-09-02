package com.velstrong.bookstore.infrastructure.config;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The server runs in UTC and readers do not. A timestamp sent without a zone is
 * read by the browser as its own local time, which is how a payment created
 * seconds earlier once reported itself hours expired.
 */
@SpringBootTest
class JsonTimeConfigTest {

    private record Moment(LocalDateTime at) {}

    private record Day(LocalDate on) {}

    private record Both(LocalDateTime at, LocalDate on) {}

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void aMomentGoesOutWithAZone() {
        LocalDateTime at = LocalDateTime.of(2026, 7, 27, 5, 42);

        String json = objectMapper.writeValueAsString(new Moment(at));

        assertThat(json).contains(at.atZone(ZoneId.systemDefault()).toInstant().toString());
        assertThat(json).endsWith("Z\"}");
    }

    @Test
    void aCalendarDayIsLeftAlone() {
        // A due date is a day, not an instant. Pinning a zone to it would move it
        // to a different day for anyone in a different offset.
        String json = objectMapper.writeValueAsString(new Day(LocalDate.of(2026, 7, 27)));

        assertThat(json).isEqualTo("{\"on\":\"2026-07-27\"}");
        assertThat(json).doesNotContain("T").doesNotContain("Z");
    }

    @Test
    void bothKindsKeepTheirOwnTreatmentInOnePayload() {
        String json = objectMapper.writeValueAsString(
                new Both(LocalDateTime.of(2026, 7, 27, 5, 42), LocalDate.of(2026, 7, 27)));

        assertThat(json).contains("\"on\":\"2026-07-27\"");
        assertThat(json).contains("Z\"");
    }

    @Test
    void readingATimestampWithoutAZoneStillWorks() {
        // Only serialisation changed; requests that still send a naive timestamp
        // — voucher validity, for one — must keep parsing.
        Moment parsed = objectMapper.readValue("{\"at\":\"2026-07-27T05:42:00\"}", Moment.class);

        assertThat(parsed.at()).isEqualTo(LocalDateTime.of(2026, 7, 27, 5, 42));
    }
}
