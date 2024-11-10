package com.saudi.tourism.core.services.holidays.v1;

import com.saudi.tourism.core.models.components.contentfragment.holiday.HolidayCFModel;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.Constants;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.saudi.tourism.core.services.holidays.v1.HolidaysCFServiceImpl.SERVICE_DESCRIPTION;

@Component(
    service = HolidaysCFService.class,
    immediate = true,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + SERVICE_DESCRIPTION})
@Slf4j
public class HolidaysCFServiceImpl implements HolidaysCFService {

  /**
   * This Service description for OSGi.
   */
  static final String SERVICE_DESCRIPTION = "Holidays CF Service";

  /**
   * Saudi Tourism Config.
   */
  @Reference
  private SaudiTourismConfigs saudiTourismConfigs;

  /**
   * User Service.
   */
  @Reference
  private UserService userService;

  @Override
  public List<HolidayCFModel> fetchAllHolidays(@NonNull String locale) {
    final var holidaysCFPath =
        MessageFormat.format(saudiTourismConfigs.getHolidaysCFPath(), locale);

    try (ResourceResolver resourceResolver = userService.getResourceResolver()) {
      final var holidaysCFRoot = resourceResolver.getResource(holidaysCFPath);
      if (holidaysCFRoot == null) {
        LOGGER.error("Holidays CF Root node not found under %s", holidaysCFPath);
        return null;
      }

      final var childResources = holidaysCFRoot.listChildren();
      if (!childResources.hasNext()) {
        return null;
      }

      return Stream.generate(() -> null)
          .takeWhile(x -> childResources.hasNext())
          .map(n -> childResources.next())
          .filter(Objects::nonNull)
          .map(r -> r.adaptTo(HolidayCFModel.class))
          .filter(Objects::nonNull)
          .filter(d -> StringUtils.isNotEmpty(d.getTitle()))
          .collect(Collectors.toList());
    }
  }
}
