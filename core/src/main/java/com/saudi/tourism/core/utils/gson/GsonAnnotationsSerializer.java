package com.saudi.tourism.core.utils.gson;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Custom class for being able to create custom Gson Json Serializers that process annotations.
 *
 * @param <T> class to process
 */
public class GsonAnnotationsSerializer<T> implements JsonSerializer<T> {

  /**
   * Configured GsonBuilder instance.
   */
  @Getter
  private final GsonBuilder gsonBuilder;

  /**
   * Creates this instance with some pre-configured Gson Builder.
   *
   * @param gsonBuilder GsonBuilder instance
   */
  public GsonAnnotationsSerializer(@NotNull final GsonBuilder gsonBuilder) {
    this.gsonBuilder = gsonBuilder;
  }

  /**
   * Serializes instance with unwrapping some fields according to fields annotations.
   *
   * @param object                   an object to serialize
   * @param type                     class of the serializing object
   * @param jsonSerializationContext Gson serialization context
   * @return json element
   */
  public JsonElement serialize(final T object, final Type type,
      final JsonSerializationContext jsonSerializationContext) {

    final Gson gson = getGsonBuilder().create();

    final JsonObject result = new JsonObject();

    final JsonElement orig = gson.toJsonTree(object);

    processFields((Class<?>) type, orig, result);

    return result;

  }

  /**
   * Iterates entries of source json element and copies them into destination json object if
   * annotations for corresponding fields match necessary rules.
   *
   * @param c        class being processed
   * @param sourceEl source json element
   * @param dest     result json object
   */
  public void processFields(final Class<?> c, final JsonElement sourceEl, final JsonObject dest) {

    for (Map.Entry<String, JsonElement> entry : sourceEl.getAsJsonObject().entrySet()) {
      final String key = entry.getKey();
      final JsonElement el = entry.getValue();

      Field field;
      try {
        field = c.getDeclaredField(key);
      } catch (NoSuchFieldException ignored) {
        field = null;
      }

      if (field == null) {
        // Do default for null or unknown fields
        dest.add(key, el);
      }

      if (field != null && !field.isAnnotationPresent(JsonIgnore.class)) {
        // Apply only for fields that are known and not ignored

        // Check if needs to be unwrapped
        if (field.isAnnotationPresent(JsonUnwrapped.class)) {
          // Unwrap contents of the field
          unwrapInnerElement(dest, el);

        } else {
          // Do default for other fields
          dest.add(key, el);
        }
      }
    }
  }

  /**
   * Unwraps inner element by copying its data into the destination element.
   *
   * @param dest   copy from
   * @param source copy to
   */
  public void unwrapInnerElement(final JsonObject dest, final JsonElement source) {
    if (source == null || source instanceof JsonNull) {
      // Do nothing if field that needs to be unwrapped is null
      return;
    }

    for (Map.Entry<String, JsonElement> inner : source.getAsJsonObject().entrySet()) {
      dest.add(inner.getKey(), inner.getValue());
    }
  }
}
