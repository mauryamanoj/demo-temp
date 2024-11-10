package com.saudi.tourism.core.services.geo.v1;

import com.saudi.tourism.core.models.components.CountryListModel;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.Constants;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.text.MessageFormat;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.saudi.tourism.core.services.geo.v1.CountriesServiceImpl.SERVICE_DESCRIPTION;

/** Countries Service. */
@Component(
    service = CountriesService.class,
    immediate = true,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + SERVICE_DESCRIPTION})
@Slf4j
public class CountriesServiceImpl implements CountriesService {

  /** This Service description for OSGi. */
  static final String SERVICE_DESCRIPTION = "Countries Service";


  /** Saudi Tourism Config. */
  @Reference
  private SaudiTourismConfigs saudiTourismConfigs;

  /** User Service. */
  @Reference
  private UserService userService;


  @Override
  public List<Country> fetchListOfCountries(@NonNull final String locale) {
    final String configPath =
        MessageFormat.format(saudiTourismConfigs.getCountriesConfigPath(), locale);

    try (ResourceResolver resourceResolver = userService.getResourceResolver()) {
      final Resource config = resourceResolver.getResource(configPath);
      if (config == null) {
        LOGGER.error("Countries Config not found under %s", configPath);
        return null;
      }

      final CountryListModel model = config.adaptTo(CountryListModel.class);

      if (model == null) {
        LOGGER.error("Countries Config not found under %s", configPath);
        return null;
      }

      if (CollectionUtils.isEmpty(model.getCountry())) {
        LOGGER.error("Countries Config not found under %s", configPath);
        return null;
      }

      return model.getCountry().stream()
          .map(
              r ->
                  Country.builder()
                      .countryName(r.getValueMap().get("countryName", String.class))
                      .flag(r.getValueMap().get("flag", String.class))
                      .visaGroup(r.getValueMap().get("visaGroup", String.class))
                      .build())
          .sorted(Comparator.comparing(c -> c.getCountryName()))
          .collect(Collectors.toList());
    }
  }
}
