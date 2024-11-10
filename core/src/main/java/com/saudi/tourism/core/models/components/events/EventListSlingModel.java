package com.saudi.tourism.core.models.components.events;

import com.day.cq.wcm.api.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.components.nav.v1.BreadcrumbModel;
import com.saudi.tourism.core.models.components.packages.NoResultSlingModel;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.NumberConstants;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_APPLY;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_CLEAR_ALL;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_EVENTS_FOUND;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_EVENT_FOUND;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_FILTERS;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_THREE_TO_TEN_EVENTS_FOUND;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_TWO_EVENTS_FOUND;
import static com.saudi.tourism.core.utils.PrimConstants.EQUAL;
import static com.saudi.tourism.core.utils.PrimConstants.PN_LOCALE;
import static com.saudi.tourism.core.utils.PrimConstants.QUESTION_MARK;

/**
 * The Class EventListSlingModel.
 */
@Model(adaptables = SlingHttpServletRequest.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class EventListSlingModel implements Serializable {

  /**
   * The stateLabel.
   */
  @ChildResource
  @Getter
  @Expose
  private List<EventsFilterModel> filters;

  /**
   * The title.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String title;

  /**
   * The loadMoreButtonLabel.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String loadMoreButtonLabel;

  /**
   * The eventDetailsLinkLabel.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String eventDetailsLinkLabel;

  /**
   * The endpoint.
   */
  @Getter
  @Setter
  @Expose
  private String endpoint;

  /**
   * The resultsPerPage.
   */
  @Getter
  @ValueMapValue
  @Expose
  private String resultsPerPageAuth;

  /**
   * The resultsPerPage.
   */
  @Getter
  @Expose
  private int resultsPerPage;

  /**
   * The noResults.
   */
  @ChildResource
  @Getter
  @Expose
  private NoResultSlingModel noResultsMessage;

  /**
   * No Result title.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String noResultTitle;

  /**
   * Found text.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String foundText;

  /**
   * Search event placeholder.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String searchEventPlaceholder;

  /**
   * The resultsTitle.
   */
  @Getter
  @Setter
  @Expose
  private String resultsTitle;

  /**
   * The resultsTitle.
   */
  @Getter
  @Setter
  @Expose
  private String resultTitle;
  /**
   * The resultsTitle.
   */
  @Getter
  @Setter
  @Expose
  private String twoResultsTitle;
  /**
   * The resultsTitle.
   */
  @Getter
  @Setter
  @Expose
  private String threeToTenResultsTitle;

  /**
   * The filtersTitle.
   */
  @Getter
  @Setter
  @Expose
  private String filtersTitle;

  /**
   * The clearFiltersButtonLabel.
   */
  @Getter
  @Setter
  @Expose
  private String clearFiltersButtonLabel;

  /**
   * The applyFiltersButtonLabel.
   */
  @Getter
  @Setter
  @Expose
  private String applyFiltersButtonLabel;

  /**
   * The multipleLocationsCopy.
   */
  @Getter
  @Setter
  @Expose
  private String multipleLocationsCopy;

  /**
   * The dataLayerName.
   */
  @Getter
  @Setter
  @Expose
  private String dataLayerName;

  /**
   * Breadcrumb.
   */
  @Getter
  @Expose
  @Self
  private BreadcrumbModel breadcrumbs;

  /**
   * Current page.
   */
  @ScriptVariable
  @JsonIgnore
  private transient Page currentPage;

  /**
   * ResourceBundleProvider.
   */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private transient ResourceBundleProvider i18nProvider;

  /**
   * SaudiTourismConfigs.
   */
  @OSGiService
  private transient SaudiTourismConfigs saudiTourismConfigs;

  /**
   * getJson method for account component.
   *
   * @return json representation.
   */
  @JsonIgnore
  public String getJson() {
    return new Gson().toJson(this);
  }

  /**
   * The init method.
   */
  @PostConstruct protected void init() {
    final String language = CommonUtils.getLanguageForPath(currentPage.getPath());
    endpoint = new StringBuilder(saudiTourismConfigs.getEventsApiEndpoint())
            .append(QUESTION_MARK)
            .append(PN_LOCALE)
            .append(EQUAL)
            .append(language)
            .toString();

    if (!StringUtils.isEmpty(resultsPerPageAuth)) {
      resultsPerPage = Integer.parseInt(resultsPerPageAuth);
    } else {
      resultsPerPage = NumberConstants.CONST_TWENTY;
    }

    if (Objects.nonNull(i18nProvider)) {

      ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(language));

      setResultsTitle(CommonUtils.getI18nString(i18nBundle, I18_KEY_EVENTS_FOUND));
      setResultTitle(CommonUtils.getI18nString(i18nBundle, I18_KEY_EVENT_FOUND));
      setTwoResultsTitle(CommonUtils.getI18nString(i18nBundle, I18_KEY_TWO_EVENTS_FOUND));
      setThreeToTenResultsTitle(CommonUtils.getI18nString(i18nBundle, I18_KEY_THREE_TO_TEN_EVENTS_FOUND));
      setFiltersTitle(CommonUtils.getI18nString(i18nBundle, I18_KEY_FILTERS));
      setClearFiltersButtonLabel(CommonUtils.getI18nString(i18nBundle, I18_KEY_CLEAR_ALL));
      setApplyFiltersButtonLabel(CommonUtils.getI18nString(i18nBundle, I18_KEY_APPLY));
      setMultipleLocationsCopy(
          CommonUtils.getI18nString(i18nBundle, Constants.I18N_MULTIPLE_DESTINATION_KEY));
      setDataLayerName("EventsFiltersForm");
    }
  }
}
