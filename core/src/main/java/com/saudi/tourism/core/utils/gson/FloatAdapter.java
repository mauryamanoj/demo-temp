package com.saudi.tourism.core.utils.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class FloatAdapter extends TypeAdapter<Float> {
  @Override
  public void write(JsonWriter out, Float value) throws IOException {
    if (value == null) {
      out.nullValue();
    } else {
      out.value(String.format("%.1f", value));
    }
  }

  @Override
  public Float read(JsonReader in) throws IOException {
    if (in.peek() == JsonToken.NULL) {
      in.nextNull();
      return null;
    }

    String stringValue = in.nextString();
    try {
      return Float.parseFloat(stringValue);
    } catch (NumberFormatException e) {
      // Handle the exception if the input string is not a valid float
      // For example, return a default value or throw a custom exception
      throw new IOException("Invalid float format: " + stringValue);
    }
  }
}
