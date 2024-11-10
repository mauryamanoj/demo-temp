package com.saudi.tourism.core.services.seasons.v1;

import com.saudi.tourism.core.models.components.contentfragment.season.SeasonCFModel;
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

import static com.saudi.tourism.core.services.seasons.v1.SeasonsCFServiceImpl.SERVICE_DESCRIPTION;


@Component(
    service = SeasonsCFService.class,
    immediate = true,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + SERVICE_DESCRIPTION})
@Slf4j
public class SeasonsCFServiceImpl implements SeasonsCFService {

  /**
   * This Service description for OSGi.
   */
  static final String SERVICE_DESCRIPTION = "Seasons CF Service";

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
  public SeasonCFModel getSeasonById(String locale, String seasonId) {

    final var seasonsCFPath =
        MessageFormat.format(saudiTourismConfigs.getSeasonsCFPath(), locale);

    try (ResourceResolver resourceResolver = userService.getResourceResolver()) {

      final var seasonCF =
          resourceResolver.getResource(seasonsCFPath + Constants.FORWARD_SLASH_CHARACTER + seasonId);
      if (seasonCF == null) {
        LOGGER.error("SeasonCF node not found under %s",
            seasonsCFPath + Constants.FORWARD_SLASH_CHARACTER + seasonId);
        return null;
      }
      final var seasonCFModel = seasonCF.adaptTo(SeasonCFModel.class);
      if (seasonCFModel == null) {
        return null;
      }

      return seasonCFModel;

    }



  }

  @Override
  public List<SeasonCFModel> fetchAllSeasons(@NonNull String locale) {
    final var seasonsCFPath =
        MessageFormat.format(saudiTourismConfigs.getSeasonsCFPath(), locale);

    try (ResourceResolver resourceResolver = userService.getResourceResolver()) {
      final var destinationsCFRoot = resourceResolver.getResource(seasonsCFPath);
      if (destinationsCFRoot == null) {
        LOGGER.error("Seasons CF Root node not found under %s", seasonsCFPath);
        return null;
      }

      final var childResources = destinationsCFRoot.listChildren();
      if (!childResources.hasNext()) {
        return null;
      }

      return Stream.generate(() -> null)
          .takeWhile(x -> childResources.hasNext())
          .map(n -> childResources.next())
          .filter(Objects::nonNull)
          .map(r -> r.adaptTo(SeasonCFModel.class))
          .filter(Objects::nonNull)
          .filter(d -> StringUtils.isNotEmpty(d.getTitle()))
          .collect(Collectors.toList());
    }
  }
}
