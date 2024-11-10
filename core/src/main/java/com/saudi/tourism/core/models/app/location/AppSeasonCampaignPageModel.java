package com.saudi.tourism.core.models.app.location;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.saudi.tourism.core.models.common.CTA;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.Constants;

import org.apache.commons.collections.CollectionUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * App location detail page model.
 */
@Model(adaptables = Resource.class,
       resourceType = {Constants.RT_APP_SEASON_CAMPAIGN_PAGE},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(selector = Constants.MODEL_EXPORTER_SELECTOR,
          name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
          extensions = ExporterConstants.SLING_MODEL_EXTENSION,
          options = {@ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS",
                                     value = Constants.STR_TRUE)})
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Slf4j
public class AppSeasonCampaignPageModel extends AppLocationPageModel {
  /**
   * Guides - list of CTA.
   */
  @ChildResource
  private List<CTA> guides;
  /**
   * The User service.
   */
  @JsonIgnore
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private transient UserService userService;
  /**
   * All locations for this page.
   */
  private List<AppCity> cities;
  /**
   * All locations for this page.
   */
  @JsonIgnore
  @ChildResource
  private List<CityItem> cityPaths;
  /**
   * Empty Package Banner detail.
   */
  @ChildResource
  private EmptyPackageBannerModel emptyPackagesBanner;

  /**
   * Products for Json representation.
   */
  private List<ProductItemModel> products;

  /**
   * productPaths authored.
   */
  @ValueMapValue
  @JsonIgnore
  private String[] productPaths;

  /**
   * Sponsors Model.
   */
  @ChildResource
  private SponsorsModel sponsors;

  /**
   * Resource Object.
   */
  @JsonIgnore
  @SlingObject
  private transient Resource resource;

  /**
   * init method for slingModel.
   */
  @PostConstruct private void initialize() {
    setCtaText(null);
    setCtaUrl(null);
    cities = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(cityPaths)) {
      for (CityItem cityPath : cityPaths) {
        try (ResourceResolver resourceResolver = userService.getResourceResolver()) {
          Resource cityResource = resourceResolver.getResource(cityPath.getCityPath() + "/jcr:content");
          if (null != cityResource) {
            cities.add(cityResource.adaptTo(AppCity.class));
          }
        } catch (Exception e) {
          LOGGER.error("Error in Adapting Cities for {}", cityPath.getCityPath(), e);
        }
      }
    }

    //Load Products info from Products pages
    final ResourceResolver resolver = resource.getResourceResolver();
    if (Objects.nonNull(productPaths)) {
      products = Arrays.stream(productPaths).map(p ->
          resolver.getResource(p + "/jcr:content/productsBanner"))
          .map(child -> child.adaptTo(ProductItemModel.class))
          .filter(Objects::nonNull).collect(Collectors.toList());
    }
  }
}
