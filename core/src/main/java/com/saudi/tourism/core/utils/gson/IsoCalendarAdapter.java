package com.saudi.tourism.core.utils.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class IsoCalendarAdapter extends TypeAdapter<Calendar> {
  /**
   * ISO Date format.
   */
  private static final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

  @Override
  public void write(JsonWriter jsonWriter, Calendar calendar) throws IOException {
    final var formatter = DateTimeFormatter.ofPattern(ISO_DATE_FORMAT);
    if (calendar != null) {
      final var zonedDateTime = ZonedDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault());
      jsonWriter.value(formatter.format(zonedDateTime));
    }

    if (calendar == null) {
      jsonWriter.nullValue();
    }
  }

  @Override
  public Calendar read(JsonReader jsonReader) throws IOException {
    if (jsonReader.peek() == JsonToken.NULL) {
      jsonReader.nextNull();
      return null;
    }

    final var s = jsonReader.nextString();
    if (StringUtils.isEmpty(s)) {
      return null;
    }

    final var formatter = DateTimeFormatter.ofPattern(ISO_DATE_FORMAT);

    // Parse the string into a LocalDateTime
    final var localDateTime = LocalDateTime.parse(s, formatter);

    // Convert LocalDateTime to Date
    final var date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

    // Create a Calendar and set its time to the parsed Date
    final var calendar = Calendar.getInstance();
    calendar.setTime(date);

    return calendar;
  }
}
