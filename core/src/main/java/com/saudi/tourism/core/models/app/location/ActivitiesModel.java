package com.saudi.tourism.core.models.app.location;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.AppUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This model is used to handle one location data.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class ActivitiesModel {

  /**
   * ID.
   */
  @Setter
  private String id;

  /**
   * Title.
   */
  @ValueMapValue
  @JsonIgnore
  private String titleEnglish;

  /**
   * Title.
   */
  @ValueMapValue
  private String title;

  /**
   * Image reference.
   */
  @ValueMapValue
  private String image;

  /**
   * Thumbnail.
   */
  @ValueMapValue
  private String thumbnail;

  /**
   * introductionTitle.
   */
  @ValueMapValue
  private String introductionTitle;

  /**
   * introductionSubTitle.
   */
  @ValueMapValue
  private String introductionSubtitle;

  /**
   * introduction.
   */
  @ValueMapValue
  private String introduction;

  /**
   * The User service.
   */
  @JsonIgnore
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private transient UserService userService;
  /**
   * All locations for this page.
   */
  private List<String> cities;
  /**
   * All locations for this page.
   */
  @JsonIgnore
  @ChildResource
  private List<CityItem> cityPaths;

  /**
   * All locations for this page.
   */
  @ChildResource
  private ActivitiesDetailsModel details;

  /**
   * init method for slingModel.
   */
  @PostConstruct private void initialize() {
    List<AppCity> cityItems = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(cityPaths)) {
      try (ResourceResolver resourceResolver = userService.getResourceResolver()) {
        for (CityItem cityPath : cityPaths) {
          Resource resource = resourceResolver.getResource(cityPath.getCityPath()
              + "/jcr:content");
          if (Objects.nonNull(resource)) {
            cityItems.add(resource.adaptTo(AppCity.class));
          }
        }
      } catch (Exception e) {
        LOGGER.error("Error in getting ResourceResolver", e);
      }
    }

    if (CollectionUtils.isNotEmpty(cityItems)) {
      cities = cityItems.stream().map(AppCity::getId).collect(Collectors.toList());
    }

    if (Objects.nonNull(titleEnglish)) {
      setId(AppUtils.stringToID(titleEnglish, false));
    }
  }
}
