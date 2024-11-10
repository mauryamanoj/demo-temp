package com.saudi.tourism.core.utils;

import lombok.Getter;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.Map;

/**
 * This defines all methods to convert value.
 *
 * @param <T> the serializable object.
 */
public class Convert<T> {

  /**
   * Properties in a map.
   */
  @Getter
  private final Map<String, Object> mapData;

  /**
   * This generic's type class.
   */
  private final Class<T> typeClass;

  /**
   * Constructor that processes request and stores properties into map.
   *
   * @param request   sling request
   * @param typeClass this generic's type class
   */
  public Convert(final SlingHttpServletRequest request, final Class<T> typeClass) {
    // Gets all parameter and values.
    mapData = RestHelper.getParameters(request);

    // Gets all necessary header information
    CommonUtils.populateHeaders(request, mapData);

    // Gets body if had
    final Map<String, Object> jsonMap = CommonUtils.getBody(request);
    if (null != jsonMap) {
      mapData.putAll(jsonMap);
    }

    this.typeClass = typeClass;
  }

  /**
   * Sets the object.
   *
   * @return a list of data of the request.
   */
  public T getRequestData() {
    return RestHelper.getObjectMapper().convertValue(mapData, typeClass);
  }
}
