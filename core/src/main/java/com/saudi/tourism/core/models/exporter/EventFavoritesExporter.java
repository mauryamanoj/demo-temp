package com.saudi.tourism.core.models.exporter;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.services.SaudiModeConfig;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;
import com.saudi.tourism.core.utils.SearchUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * The Event Sling Model Exporter.
 */
@SuppressWarnings("squid:S2065")
@Model(adaptables = Resource.class,
       resourceType = {"sauditourism/components/structure/event-detail-page"},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = "jackson",
          extensions = "json",
          options = {@ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS",
                                     value = "true")})
@Slf4j
public class EventFavoritesExporter {

  /**
   * The cardImage.
   */
  @Getter
  @ValueMapValue
  private String cardImage;

  /**
   * The cardImage.
   */
  @Getter
  @ValueMapValue
  private String s7cardImage;

  /**
   * The altImage.
   */
  @Getter
  @ValueMapValue
  private String altImage;
  /**
   * The title.
   */
  @Getter
  @ValueMapValue
  private String title;
  /**
   * The calendarStartDate.
   */
  @Getter
  @ValueMapValue
  private String calendarStartDate;
  /**
   * The calendarEndDate.
   */
  @Getter
  @ValueMapValue
  private String calendarEndDate;
  /**
   * The city.
   */
  @Getter
  @ValueMapValue
  private String city;
  /**
   * The region.
   */
  @Getter
  @ValueMapValue
  private String region;
  /**
   * The path.
   */
  @Getter
  private String path;
  /**
   * The link.
   */
  @Getter
  private String link;
  /**
   * The favId.
   */
  @Setter
  @Getter
  @Expose
  private String favId;

  /**
   * The favCategory.
   */
  @Setter
  @Getter
  @Expose
  private String favCategory;
  /**
   * The Resource.
   */
  @Self
  private transient Resource resource;
  /**
   * The saudiModeConfig.
   */
  @Inject
  private SaudiModeConfig saudiModeConfig;
  /**
   * ResourceBundleProvider.
   */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private transient ResourceBundleProvider i18nProvider;

  /**
   * init method of sling model.
   */
  @PostConstruct protected void init() {
    try {
      calendarStartDate = CommonUtils
          .convertDateToSTring(calendarStartDate, Constants.FORMAT_ARTICLEDATE_DAY_MONTH);


      calendarEndDate =
          CommonUtils.convertDateToSTring(calendarEndDate, Constants.FORMAT_ARTICLEDATE_DAY_MONTH);

      if (resource != null) {
        String pagePath = resource.getParent().getPath();
        favId = LinkUtils.getFavoritePath(pagePath);
        favCategory = SearchUtils.getFavCategory(resource);
        link = LinkUtils.getAuthorPublishUrl(resource.getResourceResolver(),
            pagePath, saudiModeConfig.getPublish());
        path = link;

        String language =
            CommonUtils.getPageNameByIndex(pagePath, Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION);
        if (Constants.ARABIC_LOCALE.equals(language)) {
          calendarStartDate = CommonUtils.getArabicDate(calendarStartDate, "/");
          calendarEndDate = CommonUtils.getArabicDate(calendarEndDate, "/");
        }
        if (Objects.nonNull(i18nProvider) && Objects.nonNull(getCity())) {
          ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(language));
          city = i18nBundle.getString(getCity());
        }
      }
    } catch (Exception ex) {
      LOGGER.error(" Error in EventSliderModel");
    }
  }
}
