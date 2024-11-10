package com.saudi.tourism.core.models.app.eventpackage;

import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.tagging.TagManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.models.app.common.AppBaseModel;
import com.saudi.tourism.core.models.app.page.Search;
import com.saudi.tourism.core.models.common.ImageCaption;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * App package page model.
 */
@SuppressWarnings("squid:S2065")
@Model(adaptables = Resource.class,
       resourceType = Constants.APP_PACKAGE_RESOURCE_TYPE,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(
    selector = "search",
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION,
    options = {@ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS",
                               value = "true")})
@Data
public class EventPackageModel extends AppBaseModel {
  /**
   * Id.
   */
  private String id;

  /**
   * Title.
   */
  @ValueMapValue
  @Named(com.day.cq.commons.jcr.JcrConstants.JCR_TITLE)
  private String title;

  /**
   * City.
   */
  @ValueMapValue
  private String city;

  /**
   * Price.
   */
  @ValueMapValue
  private String price;

  /**
   * Item halfDay.
   */
  @ValueMapValue
  private boolean halfDay;

  /**
   * Short description.
   */
  @ValueMapValue
  private String shortDescription;

  /**
   * Long description.
   */
  @ValueMapValue
  private String description;

  /**
   * Featured Image.
   */
  @ValueMapValue
  private String featuredImage;

  /**
   * Featured Image Caption.
   */
  @ChildResource
  private ImageCaption featuredImageCaption;

  /**
   * Preview Image.
   */
  @ValueMapValue
  private String previewImage;

  /**
   * Preview Image Caption.
   */
  @ChildResource
  private ImageCaption previewImageCaption;

  /**
   * Category.
   */
  @ValueMapValue
  private String category;

  /**
   * List of vendors.
   */
  @ChildResource
  private List<VendorModel> vendors;

  /**
   * List of days.
   */
  @ChildResource
  @JsonIgnore
  private transient List<PackageDayModel> days;

  /**
   * List of package day events.
   */
  private List<PackageDayDetail> details = new ArrayList<>();

  /**
   * The User service.
   */
  @Inject
  @JsonIgnore
  private transient UserService userService;

  /**
   * ResourceResolver.
   */
  @Self
  @JsonIgnore
  private transient Resource currentResource;

  /**
   * Search Tags.
   */
  @Getter
  @Setter
  @ChildResource
  private Search search;

  /**
   * ResourceBundleProvider.
   */
  @JsonIgnore
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private transient ResourceBundleProvider i18nProvider;

  /**
   * init method of sling model.
   */
  @PostConstruct protected void init() {
    String path = Optional.ofNullable(currentResource.getParent()).map(Resource::getPath)
        .orElse(StringUtils.EMPTY);
    String language =
        CommonUtils.getPageNameByIndex(path, Constants.AEM_APP_LANGAUAGE_PAGE_PATH_POSITION);

    Locale locale = new Locale(language);

    ResourceBundle i18nBundle = i18nProvider.getResourceBundle(locale);
    if (city != null) {
      city = i18nBundle.getString(city);
    }
    setWebMappingPath(LinkUtils.getFavoritePath(getWebMappingPath()));
    try (ResourceResolver resolver = userService.getResourceResolver()) {
      TagManager tagManager = resolver.adaptTo(TagManager.class);
      if (days != null) {
        days.stream().filter(model -> CollectionUtils.isNotEmpty(model.getDayEvents()))
            .forEach(packageDayModel -> {
              PackageDayDetail packageDayDetail = new PackageDayDetail();

              packageDayModel.getDayEvents().stream().forEach(packageItemModel -> {
                if (packageItemModel.getCategory() != null) {
                  packageItemModel.setCategory(
                      CommonUtils.getTagName(packageItemModel.getCategory(), tagManager, locale));
                }

                packageDayDetail.addItem(packageItemModel);
              });
              details.add(packageDayDetail);
            });
      }
    }
    this.id = path;
  }
}
