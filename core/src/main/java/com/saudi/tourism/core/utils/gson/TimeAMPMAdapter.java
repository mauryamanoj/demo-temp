package com.saudi.tourism.core.utils.gson;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Calendar;

/**
 * A GSON adapter to serialize/deserialize a Calendar to the following format.
 *
 * Example : {"hourOfDay":4,"minute":0,"ampm":"pm"}
 */
@Slf4j
public class TimeAMPMAdapter extends TypeAdapter<Calendar> {

  /**
   * AM / PM field.
   */
  private static final String AMPM_FIELD = "ampm";

  /**
   * Minute field.
   */
  private static final String MINUTE_FIELD = "minute";

  /**
   * Hour of Day field.
   */
  private static final String HOUR_OF_DAY_FIELD = "hourOfDay";

  /**
   * AM.
   */
  private static final String AM = "am";

  /**
   * PM.
   */
  private static final String PM = "pm";

  @Override
  public void write(JsonWriter jsonWriter, Calendar calendar) throws IOException {
    if (calendar != null) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.add(HOUR_OF_DAY_FIELD, new JsonPrimitive(calendar.get(Calendar.HOUR)));
      jsonObject.add(MINUTE_FIELD, new JsonPrimitive(calendar.get(Calendar.MINUTE)));
      final int ampm = calendar.get(Calendar.AM_PM);
      if (Calendar.AM == ampm) {
        jsonObject.add(AMPM_FIELD, new JsonPrimitive(AM));
      }

      if (Calendar.PM == ampm) {
        jsonObject.add(AMPM_FIELD, new JsonPrimitive(PM));
      }

      jsonWriter.jsonValue(jsonObject.toString());
    }
  }

  @Override
  public Calendar read(JsonReader jsonReader) throws IOException {
    if (jsonReader.peek() == JsonToken.NULL) {
      jsonReader.nextNull();
      return null;
    }

    String fieldname = null;
    final Calendar calendar = Calendar.getInstance();

    while (jsonReader.hasNext()) {
      JsonToken token = jsonReader.peek();

      if (token.equals(JsonToken.BEGIN_OBJECT)) {
        jsonReader.beginObject();
      }

      if (token.equals(JsonToken.NAME)) {
        // get the current token
        fieldname = jsonReader.nextName();
      }

      if (HOUR_OF_DAY_FIELD.equals(fieldname)) {
        // move to next token
        jsonReader.peek();
        calendar.set(Calendar.HOUR, jsonReader.nextInt());
      }

      if (MINUTE_FIELD.equals(fieldname)) {
        // move to next token
        jsonReader.peek();
        calendar.set(Calendar.MINUTE, jsonReader.nextInt());
      }

      if (AMPM_FIELD.equals(fieldname)) {
        // move to next token
        jsonReader.peek();
        final String ampm = jsonReader.nextString();
        if (StringUtils.equals(AM, ampm)) {
          calendar.set(Calendar.AM_PM, Calendar.AM);
        }

        if (StringUtils.equals(PM, ampm)) {
          calendar.set(Calendar.AM_PM, Calendar.PM);
        }
      }
    }
    jsonReader.endObject();
    return calendar;
  }
}
