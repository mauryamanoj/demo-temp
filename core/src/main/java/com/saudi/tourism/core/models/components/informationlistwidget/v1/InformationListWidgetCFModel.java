package com.saudi.tourism.core.models.components.informationlistwidget.v1;

import com.day.cq.commons.Externalizer;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Category;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Map Widget Model.
 */
@Builder
@Getter
public class InformationListWidgetCFModel {
  /**
   * Variable Injecting resourceResolver.
   */
  @SlingObject
  private transient ResourceResolver resourceResolver;

  /**
   * setting service.
   */
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private SlingSettingsService settingsService;

  /**
   * Ideal for label.
   */
  @Expose
  private String idealForLabel;

  /**
   * languageValue.
   */
  @Expose
  @Setter
  private List<String> languageValue;


  /**
   * languageLabel.
   */
  @Expose
  private String languageLabel;

  /**
   * openNowLabel.
   */
  @Expose
  private String openNowLabel;

  /**
   * closedNowLabel.
   */
  @Expose
  private String closedNowLabel;


  /**
   * informationLabel.
   */
  @Expose
  private String informationLabel;

  /**
   * locationLabel.
   */
  @Expose
  private String locationLabel;

  /**
   * durationLabel.
   */
  @Expose
  private String durationLabel;

  /**
   * agesLabel.
   */
  @Expose
  private String agesLabel;

  /**
   * typeLabel.
   */
  @Expose
  private String typeLabel;

  /**
   * startDate.
   */
  @Expose
  private String startDate;

  /**
   * endDate.
   */
  @Expose
  private String endDate;

  /**
   * accessibilityLabel.
   */
  @Expose
  private String accessibilityLabel;

  /**
   * locationValue.
   */
  @Expose
  @Setter
  private String locationValue;

  /**
   * durationValue.
   */
  @Expose
  private String durationValue;

  /**
   * agesValueFrom.
   */
  private String agesValueFrom;

  /**
   * agesValueTo.
   */
  private String agesValueTo;

  /**
   * agesValues.
   */
  @Getter
  @Setter
  @Expose
  private String agesValue;

  /**
   * typeValue.
   */
  @Expose
  @Setter
  private String typeValue;

  /**
   * Type.
   */
  @Expose
  private String type;

  /**
   * accessibilityValue.
   */
  @Expose
  @Setter
  private List<String> accessibilityValue;

  /**
   * openingHoursLabel.
   */
  @Expose
  private String openingHoursLabel;

  /**
   * sameTimeAcrossWeek.
   */
  @Expose
  private boolean sameTimeAcrossWeek;

  /**
   * openingHoursValue.
   */
  @Expose
  private List<OpeningHoursValue> openingHoursValue;

  /**
   * to label..
   */
  @Expose
  private String toLabel;

  /**
   * Categories Icon.
   */
  @Getter
  private List<String> categoriesIcon;

  /**
   * Categories Icon.
   */
  @Getter
  @Expose
  @Setter
  private List<Category> categoriesTags;

  /**
   * poiPhoneNumberLabel.
   */
  @Expose
  private String  poiPhoneNumberLabel;
  /**
   * poiPhoneNumber.
   */
  @Expose
  private String poiPhoneNumber;
  /**
   * poiWebsiteLabel.
   */
  @Expose
  private String  poiWebsiteLabel;
  /**
   * poiWebSitePath.
   */
  @Expose
  private String  poiWebSitePath;
  /**
   * poiReservationLabel.
   */
  @Expose
  private String  poiReservationLabel;
  /**
   * poiReservationLink.
   */
  @Expose
  private String  poiReservationLink;
  /**
   * poiMenuLabel.
   */
  @Expose
  private String  poiMenuLabel;
  /**
   * poiMenuLinkLabel.
   */
  @Expose
  private String  poiMenuLinkLabel;
  /**
   * poiMenuLink.
   */
  @Expose
  private String  poiMenuLink;

  @PostConstruct
  void init() {
    if (StringUtils.isNotBlank(poiWebSitePath) && resourceResolver != null && settingsService != null) {
      poiWebSitePath = LinkUtils.getAuthorPublishUrl(resourceResolver, poiWebSitePath,
        settingsService.getRunModes().contains(Externalizer.PUBLISH));
    }
    if (StringUtils.isNotBlank(poiMenuLink) && resourceResolver != null && settingsService != null) {
      poiMenuLink = LinkUtils.getAuthorPublishUrl(resourceResolver, poiMenuLink,
        settingsService.getRunModes().contains(Externalizer.PUBLISH));
    }
  }
}
