package com.saudi.tourism.core.services.destinations.v1;

import com.saudi.tourism.core.models.components.contentfragment.destination.DestinationCFModel;
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

import static com.saudi.tourism.core.services.destinations.v1.DestinationsCFServiceImpl.SERVICE_DESCRIPTION;

@Component(
    service = DestinationsCFService.class,
    immediate = true,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + SERVICE_DESCRIPTION})
@Slf4j
public class DestinationsCFServiceImpl implements DestinationsCFService {

  /** This Service description for OSGi. */
  static final String SERVICE_DESCRIPTION = "Destinations Service";

  /** Saudi Tourism Config. */
  @Reference private SaudiTourismConfigs saudiTourismConfigs;

  /** User Service. */
  @Reference private UserService userService;

  @Override
  public List<DestinationCFModel> fetchAllDestination(@NonNull String locale) {
    final var destinationsCFPath =
        MessageFormat.format(saudiTourismConfigs.getDestinationsCFPath(), locale);

    try (ResourceResolver resourceResolver = userService.getResourceResolver()) {
      final var destinationsCFRoot = resourceResolver.getResource(destinationsCFPath);
      if (destinationsCFRoot == null) {
        LOGGER.error("DestinationsCF Root node not found under %s", destinationsCFPath);
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
          .map(r -> r.adaptTo(DestinationCFModel.class))
          .filter(Objects::nonNull)
          .filter(d -> StringUtils.isNotEmpty(d.getTitle()))
          .collect(Collectors.toList());
    }
  }

  @Override
  public DestinationCFModel getDestinationById(String locale, String destinationId) {

    final var destinationsCFPath =
        MessageFormat.format(saudiTourismConfigs.getDestinationsCFPath(), locale);

    try (ResourceResolver resourceResolver = userService.getResourceResolver()) {

      final var destinationCF =
          resourceResolver.getResource(destinationsCFPath + Constants.FORWARD_SLASH_CHARACTER + destinationId);
      if (destinationCF == null) {
        LOGGER.error("DestinationsCF node not found under %s",
            destinationsCFPath + Constants.FORWARD_SLASH_CHARACTER + destinationId);
        return null;
      }
      final var destinationCFModel = destinationCF.adaptTo(DestinationCFModel.class);
      if (destinationCFModel == null) {
        return null;
      }

      return destinationCFModel;

    }



  }


  @Override
  public List<Destination> returnAllDestination(String locale) {
    final var destinationsCFPath =
        MessageFormat.format(saudiTourismConfigs.getDestinationsCFPath(), locale);

    try (ResourceResolver resourceResolver = userService.getResourceResolver()) {
      final var destinationsCFRoot = resourceResolver.getResource(destinationsCFPath);
      if (destinationsCFRoot == null) {
        LOGGER.error("DestinationsCF Root node not found under %s", destinationsCFPath);
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
          .map(
              child -> {
                DestinationCFModel destinationCFModel = Objects.requireNonNull(child.adaptTo(DestinationCFModel.class));
                return Destination.builder()
                    .path(child.getPath())
                    .title(destinationCFModel.getTitle())
                    .subTitle(destinationCFModel.getSubTitle())
                    .aboutDescription(destinationCFModel.getAboutDescription())
                    .aboutHeading(destinationCFModel.getAboutHeading())
                    .bannerImage(destinationCFModel.getBannerImage())
                    .popUpImage(destinationCFModel.getPopUpImage())
                    .id(destinationCFModel.getId())
                    .latitude(destinationCFModel.getLatitude())
                    .longitude(destinationCFModel.getLongitude())
                    .categories(destinationCFModel.getCategories())
                    .tagline(destinationCFModel.getTagline())
                    .build();
              })
          .filter(Objects::nonNull)
          .collect(Collectors.toList());
    }
  }
}
