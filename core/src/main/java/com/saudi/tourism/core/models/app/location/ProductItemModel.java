package com.saudi.tourism.core.models.app.location;

import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.models.common.CTA;
import com.saudi.tourism.core.services.PackageService;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.servlets.app.AppCityServlet;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This model is used to handle one location data.
 */
@Model(adaptables = Resource.class,
       resourceType = Constants.APP_PRODUCT_PAGE_RESOURCE_TYPE,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(selector = "search",
          name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
          extensions = ExporterConstants.SLING_MODEL_EXTENSION,
          options = {@ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS",
                                     value = "true")})
@Getter
@Slf4j
public class ProductItemModel implements Serializable {

  /**
   * ID.
   */
  @Setter
  private String id;

  /**
   * Strapline.
   */
  @ValueMapValue
  private String strapline;

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
   * Related Packages for JSON.
   */
  private List<String> relatedPackages;

  /**
   * Empty Package Banner detail.
   */
  @ChildResource
  private EmptyPackageBannerModel emptyPackagesBanner;

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
   * Guides - list of CTA.
   */
  @ChildResource
  private List<CTA> guides;

  /**
   * Activities Section Title.
   */
  @ValueMapValue
  private String activitiesTitle;

  /**
   * Activities.
   */
  @ChildResource
  private List<ActivitiesModel> activities;

  /**
   * Articles.
   */
  @ChildResource
  private List<ArticlesModel> articles;

  /**
   * The User service.
   */
  @JsonIgnore
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private transient UserService userService;

  /**
   * The current component resource.
   */
  @JsonIgnore
  @Self
  private transient Resource currentResource;

  /**
   * The cache.
   */
  @JsonIgnore
  @OSGiService
  private transient PackageService packageService;


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

    final String locale = CommonUtils.getLanguageForPath(currentResource.getPath());
    AppCityServlet.fillCityPackages(locale, cityItems, packageService);

    if (CollectionUtils.isNotEmpty(cityItems)) {
      cities = cityItems.stream().map(AppCity::getId).collect(Collectors.toList());

      relatedPackages = cityItems.stream().map(AppCity::getPackages).flatMap(List::stream)
          .distinct().collect(Collectors.toList());
    }

    final PageManager pageManager =
        currentResource.getResourceResolver().adaptTo(PageManager.class);
    String path = pageManager.getContainingPage(currentResource).getPath();
    if (StringUtils.isNotBlank(path)) {
      setId(AppUtils.pathToID(path));
    }
  }
}
