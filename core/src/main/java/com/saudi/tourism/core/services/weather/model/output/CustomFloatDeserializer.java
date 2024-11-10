package com.saudi.tourism.core.services.weather.model.output;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class CustomFloatDeserializer implements JsonDeserializer<Float> {

  @Override
  public Float deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
      throws JsonParseException {
    String value = jsonElement.getAsString().replace(',', '.'); // Replace comma with dot
    return Float.parseFloat(value);
  }
}
