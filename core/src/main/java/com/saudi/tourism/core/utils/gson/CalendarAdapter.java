package com.saudi.tourism.core.utils.gson;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Calendar;

@Slf4j
public class CalendarAdapter extends TypeAdapter<Calendar> {
  /**
   * Year field.
   */
  private static final String YEAR_FIELD = "year";

  /**
   * Month field.
   */
  private static final String MONTH_FIELD = "month";

  /**
   * Day of month field.
   */
  private static final String DAY_OF_MONTH_FIELD = "dayOfMonth";

  /**
   * Hour of Day field.
   */
  private static final String HOUR_OF_DAY_FIELD = "hourOfDay";

  /**
   * Minute field.
   */
  private static final String MINUTE_FIELD = "minute";

  /**
   * Second field.
   */
  private static final String SECOND_FIELD = "second";

  @Override
  public void write(JsonWriter jsonWriter, Calendar calendar) throws IOException {
    if (calendar != null) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.add(YEAR_FIELD, new JsonPrimitive(calendar.get(Calendar.YEAR)));
      jsonObject.add(MONTH_FIELD, new JsonPrimitive(calendar.get(Calendar.MONTH) + 1));
      jsonObject.add(DAY_OF_MONTH_FIELD, new JsonPrimitive(calendar.get(Calendar.DAY_OF_MONTH)));
      jsonObject.add(HOUR_OF_DAY_FIELD, new JsonPrimitive(calendar.get(Calendar.HOUR_OF_DAY)));
      jsonObject.add(MINUTE_FIELD, new JsonPrimitive(calendar.get(Calendar.MINUTE)));
      jsonObject.add(SECOND_FIELD, new JsonPrimitive(calendar.get(Calendar.SECOND)));

      jsonWriter.jsonValue(jsonObject.toString());
    }

    if (calendar == null) {
      jsonWriter.jsonValue(null);
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

      if (YEAR_FIELD.equals(fieldname)) {
        // move to next token
        jsonReader.peek();
        calendar.set(Calendar.YEAR, jsonReader.nextInt());
      }

      if (MONTH_FIELD.equals(fieldname)) {
        // move to next token
        jsonReader.peek();
        calendar.set(Calendar.MONTH, jsonReader.nextInt() - 1);
      }

      if (DAY_OF_MONTH_FIELD.equals(fieldname)) {
        // move to next token
        jsonReader.peek();
        calendar.set(Calendar.DAY_OF_MONTH, jsonReader.nextInt());
      }

      if (HOUR_OF_DAY_FIELD.equals(fieldname)) {
        // move to next token
        jsonReader.peek();
        calendar.set(Calendar.HOUR_OF_DAY, jsonReader.nextInt());
      }

      if (MINUTE_FIELD.equals(fieldname)) {
        // move to next token
        jsonReader.peek();
        calendar.set(Calendar.MINUTE, jsonReader.nextInt());
      }

      if (SECOND_FIELD.equals(fieldname)) {
        // move to next token
        jsonReader.peek();
        calendar.set(Calendar.SECOND, jsonReader.nextInt());
      }
    }
    jsonReader.endObject();
    return calendar;
  }
}
