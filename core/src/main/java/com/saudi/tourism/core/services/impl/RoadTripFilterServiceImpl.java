package com.saudi.tourism.core.services.impl;

import com.saudi.tourism.core.services.RoadTripFilterService;
import com.saudi.tourism.core.utils.AdminUtil;
import org.osgi.service.component.annotations.Component;

import java.io.IOException;

/**
 * The RoadTrip Filter Service.
 */
@Component(service = RoadTripFilterService.class, immediate = true)
public class RoadTripFilterServiceImpl implements RoadTripFilterService {

  /**
   * Get all roadtrip filters from admin page settings.
   * @param locale locale.
   * @return Object roadtrip filters data
   * @throws IOException IOException.
   */
  @Override
  public Object getRoadTripFilters(String locale) throws IOException {
    return AdminUtil.getRoadTripPageSettings(locale);
  }
}
