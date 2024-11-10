package com.saudi.tourism.core.models.components.contentfragment.utils;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.saudi.tourism.core.models.components.informationlistwidget.v1.OpeningHoursValue;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Timings Aware Model.
 */
public interface TimingsAwareModel {

  default List<OpeningHoursValue> buildTimings(String[] timingsObjects) {
    final Logger logger = LoggerFactory.getLogger(TimingsAwareModel.class);
    List<OpeningHoursValue> openingHoursValues = new ArrayList<>();

    for (String timingObject : timingsObjects) {
      if (StringUtils.isNotEmpty(timingObject)) {
        try {
          final var jsonObject = JsonParser.parseString(timingObject).getAsJsonObject();
          if (null != jsonObject) {
            String dayLabel = ObjectUtils.defaultIfNull(jsonObject.get("dayLabel").getAsString(), null);
            String startTimeLabel = ObjectUtils.defaultIfNull(jsonObject.get("startTimeLabel").getAsString(), null);
            String endTimeLabel = ObjectUtils.defaultIfNull(jsonObject.get("endTimeLabel").getAsString(), null);
            openingHoursValues.add(OpeningHoursValue.builder()
                                                    .dayLabel(dayLabel)
                                                    .startTimeLabel(startTimeLabel)
                                                    .endTimeLabel(endTimeLabel)
                                                    .build());
          }
        } catch (JsonParseException e) {
          logger.error("Failed to parse JSON object: {}", timingObject, e);
        }
      }
    }
    return openingHoursValues;
  }
}
