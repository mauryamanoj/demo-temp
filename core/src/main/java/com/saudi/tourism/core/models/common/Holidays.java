package com.saudi.tourism.core.models.common;

import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.saudi.tourism.core.utils.Constants;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Model for the map of all holidays for the admin page. It's cached, so don't leave here
 * request or resource references after adapting.
 */
@Exporter(selector = Constants.MODEL_EXPORTER_SELECTOR,
          name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
          extensions = ExporterConstants.SLING_MODEL_EXTENSION,
          options = {@ExporterOption(name = "SerializationFeature.WRAP_ROOT_VALUE",
                                     value = Constants.STR_TRUE)})
@JsonRootName("data")
@Model(adaptables = {Resource.class},
       adapters = Holidays.class,
       cache = true,
       resourceType = Constants.RT_ADMIN_HOLIDAYS)
@NoArgsConstructor
@Slf4j
public class Holidays extends HashMap<String, String> implements Serializable {

  /**
   * Node name where all holidays are stored.
   */
  private static final String NN_HOLIDAY_ITEMS = "items";

  /**
   * Using resource constructor.
   *
   * @param adminResource jcr:content resource of admins holidays page
   */
  public Holidays(final Resource adminResource) {
    // Workaround for unit test when resource type is not checked in adaptTo
    if (!adminResource.getResourceResolver()
        .isResourceType(adminResource, Constants.RT_ADMIN_HOLIDAYS)) {
      throw new IllegalStateException("Wrong resource type: " + adminResource.getResourceType());
    }

    final Resource itemsResource = adminResource.getChild(NN_HOLIDAY_ITEMS);
    if (itemsResource == null || !itemsResource.hasChildren()) {
      LOGGER.error("Holidays list is not configured {}", adminResource.getPath());
      return;
    }

    // Store all holidays into this HashMap as date -> name
    final Iterable<Resource> children = itemsResource.getChildren();
    for (final Resource holidayItemResource : children) {
      final ValueMap valueMap = holidayItemResource.getValueMap();
      final String holidayDate = valueMap.get("date", String.class);
      final String holidayName = valueMap.get(Constants.CONST_NAME, String.class);

      if (StringUtils.isNoneBlank(holidayDate, holidayName)) {
        this.put(holidayDate, holidayName);
      }
    }
  }
}
