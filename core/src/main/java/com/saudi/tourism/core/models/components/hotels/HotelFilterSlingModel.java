package com.saudi.tourism.core.models.components.hotels;

import com.saudi.tourism.core.models.components.events.FilterModel;
import com.saudi.tourism.core.models.components.packages.NoResultSlingModel;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * The Class HotelListSlingModel.
 */
@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class HotelFilterSlingModel implements Serializable {

  /**
   * The constant HOTEL_COUNT.
   */
  private static final int HOTELS_DEFAULT_RESULTS_PER_PAGE = 20;
  /**
   * Hotels default ornament.
   */
  private static final String HOTELS_DEFAULT_ORNAMENT = "05A";
  /**
   * The stateLabel.
   */
  @ChildResource
  @Getter
  private List<FilterModel> filters;
  /**
   * The title.
   */
  @ValueMapValue
  @Getter
  private String title;
  /**
   * The noResults.
   */
  @ChildResource
  @Getter
  private NoResultSlingModel noResultsMessage;
  /**
   * The filterButtonLabel.
   */
  @Getter
  @Setter
  private String filterButtonLabel;
  /**
   * The multipleLocationsCopy.
   */
  @Getter
  @Setter
  private String multipleLocationsCopy;
  /**
   * The hotelsEndpoint.
   */
  @Getter
  @Setter
  private String endpoint;
  /**
   * The clearFiltersButtonLabel.
   */
  @Getter
  @Setter
  private String clearFiltersButtonLabel;
  /**
   * The loadMoreButtonLabel.
   */
  @Getter
  @Setter
  private String loadMoreButtonLabel;
  /**
   * The applyFiltersButtonLabel.
   */
  @Getter
  @Setter
  private String applyFiltersButtonLabel;
  /**
   * The resultsTitle.
   */
  @Getter
  @Setter
  private String resultsTitle;
  /**
   * The ornament Id.
   */
  @Getter
  @Setter
  private String ornamentId;
  /**
   * The filtersTitle.
   */
  @Getter
  @Setter
  private String filtersTitle;
  /**
   * The resultsPerPage.
   */
  @Getter
  @ValueMapValue
  private String resultsPerPageAuth;
  /**
   * The resultsPerPage.
   */
  @Getter
  private int resultsPerPage;
  /**
   * The dataLayerName.
   */
  @Getter
  @Setter
  private String dataLayerName;
  /**
   * currentResource.
   */
  @Self
  private transient Resource currentResource;
  /**
   * ResourceBundleProvider.
   */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private transient ResourceBundleProvider i18nProvider;

  /**
   * The init method.
   */
  @PostConstruct protected void init() {
    try {
      String path = Optional.ofNullable(currentResource.getParent()).map(Resource::getPath)
          .orElse(StringUtils.EMPTY);
      // language needed for tag translation title
      String language =
          CommonUtils.getPageNameByIndex(path, Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION);
      resultsPerPage = Optional.of(Integer.parseInt(resultsPerPageAuth)).
              orElse(HOTELS_DEFAULT_RESULTS_PER_PAGE);
      endpoint = "/bin/api/v1/hotels?locale=" + language;
      ornamentId = HOTELS_DEFAULT_ORNAMENT;
      if (Objects.nonNull(i18nProvider)) { // update city to i18n value
        ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(language));

        setFiltersTitle(CommonUtils.getI18nString(i18nBundle, "filters"));
        setFilterButtonLabel(CommonUtils.getI18nString(i18nBundle, "filterlabel"));
        setClearFiltersButtonLabel(CommonUtils.getI18nString(i18nBundle, "clearall"));
        setLoadMoreButtonLabel(CommonUtils.getI18nString(i18nBundle, "loadmore"));
        setResultsTitle(CommonUtils.getI18nString(i18nBundle, "hotelsfound"));
        setApplyFiltersButtonLabel(CommonUtils.getI18nString(i18nBundle, "apply"));
        setMultipleLocationsCopy(
            CommonUtils.getI18nString(i18nBundle, Constants.I18N_MULTIPLE_LOCATION_KEY));
        setDataLayerName("HotelFiltersForm");
      }
    } catch (Exception e) {
      LOGGER.error("Error in HotelFilterSlingModel");
    }

  }
}
