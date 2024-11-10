package com.saudi.tourism.core.models.components.nav.v3;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.components.ArticleItem;
import com.saudi.tourism.core.models.components.DestinationPageModel;
import com.saudi.tourism.core.models.components.DropdownColumnsCard;
import com.saudi.tourism.core.models.components.listcomponent.v1.ListTableItem;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
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
public class NavigationLink extends Link {

  /**
   * Region pages.
   */
  @Deprecated
  @Expose
  private final List<DestinationPageModel> regions;

  /**
   * City pages.
   */
  @Deprecated
  @Expose
  private final List<DestinationPageModel> cities;

  /**
   * Regions label.
   */
  @ValueMapValue
  @Deprecated
  @Expose
  private String regionsLabel;

  /**
   * Cities label.
   */
  @ValueMapValue
  @Deprecated
  @Expose
  private String citiesLabel;

  /**
   * Best for label.
   */
  @ValueMapValue
  @Deprecated
  @Expose
  private String bestForLabel;

  /**
   * Region label.
   */
  @ValueMapValue
  @Deprecated
  @Expose
  private String regionLabel;

  /**
   * City label.
   */
  @ValueMapValue
  @Deprecated
  @Expose
  private String cityLabel;

  /**
   * Options.
   */
  @ChildResource
  @Deprecated
  @Expose
  private List<ListTableItem> countryInfo;

  /**
   * Article path.
   */
  @ValueMapValue
  @Expose
  private String articlePath;

  /**
   * Article image.
   */
  @ValueMapValue
  @Expose
  private String articleImage;

  /**
   * Article item.
   */
  @Setter
  @Expose
  private ArticleItem articleItem;

  /**
   * Card Wrapper.
   */
  @ChildResource
  @Setter
  @Expose
  private DropdownColumnsCard dropdownColumnsCard;

  /**
   * Constructor.
   * @param regionPages path to region pages
   * @param cityPages path to city pages
   * @param currentResource current resource
   */
  @Inject
  public NavigationLink(
      @ValueMapValue @Named("regionPages") String[] regionPages,
      @ValueMapValue @Named("cityPages") String[] cityPages,
      @SlingObject @Named("currentResource") Resource currentResource
  ) {
    this.regions = getPages(regionPages, currentResource);
    this.cities = getPages(cityPages, currentResource);
  }

  /**
   * Adapt paths to pages.
   *
   * @param paths page paths
   * @param resource resource
   * @return list of pages
   */
  @Deprecated
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

  /**
   * We should identify tabs that hold map data for html.
   * @return if tab holds map data
   */
  @Deprecated
  public boolean isMap() {
    return CollectionUtils.isNotEmpty(this.regions) || CollectionUtils.isNotEmpty(this.cities);
  }

  /**
   * If link has article info.
   * @return true if link has article info, and false - otherwise.
   */
  public boolean hasArticle() {
    return StringUtils.isNotEmpty(this.articleImage) && StringUtils.isNotEmpty(this.articlePath);
  }

}
