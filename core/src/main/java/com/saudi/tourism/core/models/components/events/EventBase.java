package com.saudi.tourism.core.models.components.events;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.RegionCity;
import com.saudi.tourism.core.models.components.title.v1.TitleModel;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.I18nConstants;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jsoup.Jsoup;

import javax.annotation.PostConstruct;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * The Class EventBase.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class EventBase {

  /**
   * The title.
   */
  @ValueMapValue
  @Getter
  @Setter
  @Expose
  private String title;

  /**
   * The analytics title.
   */
  @ValueMapValue
  @Getter
  private String analyticsTitle;

  /**
   * The subtitle.
   */
  @ValueMapValue(name = "eventSubtitle")
  @Getter
  private String subtitle;

  /**
   * The start date.
   */
  @ValueMapValue(name = "calendarStartDate")
  @Getter
  private String startDate;

  /**
   * The end date.
   */
  @ValueMapValue(name = "calendarEndDate")
  @Getter
  private String endDate;

  /**
   * Category tags.
   */
  @ValueMapValue
  @Getter
  @Setter
  private String[] categoryTags;
  /**
   * venue tags.
   */
  @ValueMapValue
  @Getter
  @Setter
  private String[] venueTags;

  /**
   * Target group tags.
   */
  @ValueMapValue
  @Getter
  private String[] targetGroupTags;

  /**
   * freePaid.
   */
  @ValueMapValue
  @Getter
  @Setter
  private String freePaid;

  /**
   * season.
   */
  @ValueMapValue
  @Getter
  @Setter
  private String season;

  /**
   * The copy.
   */
  @ValueMapValue
  @Getter
  private String copy;

  /**
   * The city.
   */
  @ValueMapValue
  @Getter
  @Setter
  @Expose
  private String city;

  /**
   * The city id.
   */
  @ValueMapValue(name = Constants.CITY)
  @Getter
  @Setter
  private String cityId;

  /**
   * City of the event.
   */
  @Getter
  @Setter
  private RegionCity cityObj;

  /**
   * The region.
   */
  @ValueMapValue
  @Setter
  @Getter
  private String region;
  /**
   * The priority.
   */
  @ValueMapValue
  @Getter
  private String priority;

  /**
   * Property tripPlan answering always true to be able to filter events in favorites.
   */
  @JsonProperty("tripPlan")
  private boolean tripPlan = true;

  /**
   * Featured event.
   */
  @ValueMapValue
  @Getter
  private boolean featured;

  /**
   * Copy Link Text.
   */
  @Getter
  @Expose
  private String copyLinkText;

  /**
   * Copy Clipboard Tooltip Text.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String copyClipboardTooltipText;

  /**
   * Add to Calendar Text.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String addToCalendarText;

  /**
   * Share button text.
   */
  @Getter
  @Expose
  private String shareButtonText;

  /**
   * currentResource.
   */
  @Self
  private transient Resource currentResource;

  /**
   * Title.
   */
  @Self
  @Getter
  @Expose
  private TitleModel titleDetails;

  /**
   * i18nProvider.
   */
  @OSGiService(filter = Constants.I18N_PROVIDER_SERVICE_TARGET)
  @JsonIgnore
  private transient ResourceBundleProvider i18nProvider;

  @PostConstruct
  protected void initEventBase() {
    PageManager pageManager = currentResource.getResourceResolver().adaptTo(PageManager.class);
    Page page = pageManager.getContainingPage(currentResource);
    Resource jcrContent = page.getContentResource();
    String lang = CommonUtils.getLanguageForPath(currentResource.getPath());
    ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(lang));
    if (Objects.nonNull(jcrContent)) {
      ValueMap valueMap = jcrContent.getValueMap();
      setCategoryTags(valueMap.get("categoryTags", String[].class));
      String eventDescription = valueMap.get("copy", String.class);

      if (!StringUtils.startsWith(eventDescription, "<p>")) {
        titleDetails.setIntroDescription("<p>" + eventDescription + "</p>");
      } else {
        titleDetails.setIntroDescription(eventDescription);
      }

      if (StringUtils.isNotEmpty(eventDescription)) {
        titleDetails.setEventDescription(Jsoup.parse(eventDescription).text());
      }
    }
    titleDetails.setMainTitle(CommonUtils.getI18nString(i18nBundle, "About this event"));
    shareButtonText = CommonUtils.getI18nString(i18nBundle, I18nConstants.I18_KEY_SHARE_EVENT);
    copyLinkText = CommonUtils.getI18nString(i18nBundle, I18nConstants.I18_KEY_COPY_LINK);
  }
}
