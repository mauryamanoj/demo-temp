package com.saudi.tourism.core.models.components.nav.v4;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.components.DestinationPageModel;
import com.saudi.tourism.core.models.components.listcomponent.v1.ListTableItem;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Model class of link related data.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class NavMap {

  /**
   * City pages.
   */
  @Expose
  private final List<DestinationPageModel> cities;

  /**
   * Cities label.
   */
  @ValueMapValue
  @Expose
  private String citiesLabel;

  /**
   * Best for label.
   */
  @ValueMapValue
  @Expose
  private String bestForLabel;

  /**
   * City label.
   */
  @ValueMapValue
  @Expose
  private String cityLabel;

  /**
   * Options.
   */
  @ChildResource
  @Expose
  private List<ListTableItem> countryInfo;

  /**
   * Constructor.
   * @param cityPages path to city pages
   * @param currentResource current resource
   */
  @Inject
  public NavMap(
      @ValueMapValue @Named("cityPages") String[] cityPages,
      @SlingObject @Named("currentResource") Resource currentResource
  ) {
    this.cities = getPages(cityPages, currentResource);
  }

  /**
   * Adapt paths to pages.
   *
   * @param paths page paths
   * @param resource resource
   * @return list of pages
   */
  private static List<DestinationPageModel> getPages(String[] paths,
      Resource resource) {

    if (ArrayUtils.isEmpty(paths) || resource == null) {
      return Collections.emptyList();
    }

    return Arrays.stream(paths).map(resource.getResourceResolver()::getResource)
        .filter(Objects::nonNull).map(page -> {
          final DestinationPageModel destinationPageModel =
              page.adaptTo(DestinationPageModel.class);
          if (destinationPageModel == null) {
            LOGGER.error("DestinationPageModel adapting error, resource: {}", page.getPath());
          }
          return destinationPageModel;
        }).collect(Collectors.toList());
  }
}
