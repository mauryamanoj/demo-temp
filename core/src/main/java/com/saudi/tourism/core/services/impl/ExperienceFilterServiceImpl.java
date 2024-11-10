package com.saudi.tourism.core.services.impl;

import com.saudi.tourism.core.models.HyPackageFilters;
import com.saudi.tourism.core.models.components.PackagePageSettings;
import com.saudi.tourism.core.services.ExperienceFilterService;
import com.saudi.tourism.core.utils.AdminUtil;
import org.osgi.service.component.annotations.Component;

import java.io.IOException;

/**
 * The Experience Filter Service.
 */
@Component(service = ExperienceFilterService.class, immediate = true)
public class ExperienceFilterServiceImpl implements ExperienceFilterService {

  /**
   * Get all experiences filters from admin page package settings.
   * @param locale locale.
   * @return Object experience filters data
   * @throws IOException IOException.
   */
  @Override
  public Object getPackageFilters(String locale) throws IOException {
    PackagePageSettings settings = AdminUtil.getPackagePageSettings(locale);
    HyPackageFilters hyPackagefilters = new HyPackageFilters();
    hyPackagefilters.setFilters(settings.getFilters());
    return hyPackagefilters;
  }
}
