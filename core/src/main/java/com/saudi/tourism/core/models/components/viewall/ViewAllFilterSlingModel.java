package com.saudi.tourism.core.models.components.viewall;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.components.events.FilterModel;
import com.saudi.tourism.core.models.components.favorites.v1.FavoritesApiEndpointsModel;
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

import static com.saudi.tourism.core.utils.I18nConstants.SAVE_TO_FAV_TEXT;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_SAVED_TO_FAV;

/**
 * The Class ViewAllFilterSlingModel.
 */
@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class ViewAllFilterSlingModel implements Serializable {

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
  @Expose
  private List<FilterModel> filters;
  /**
   * The title.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String title;

  /**
   * The templateType.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String templateType;
  /**
   * The noResults.
   */
  @ChildResource
  @Getter
  @Expose
  private NoResultSlingModel noResultsMessage;
  /**
   * The filterButtonLabel.
   */
  @Getter
  @Setter
  @Expose
  private String filterButtonLabel;
  /**
   * The multipleLocationsCopy.
   */
  @Getter
  @Setter
  @Expose
  private String multipleLocationsCopy;
  /**
   * The hotelsEndpoint.
   */
  @Getter
  @Setter
  @Expose
  private String endpoint;
  /**
   * The clearFiltersButtonLabel.
   */
  @Getter
  @Setter
  @Expose
  private String clearFiltersButtonLabel;
  /**
   * The loadMoreButtonLabel.
   */
  @Getter
  @Setter
  @Expose
  private String loadMoreButtonLabel;
  /**
   * The applyFiltersButtonLabel.
   */
  @Getter
  @Setter
  @Expose
  private String applyFiltersButtonLabel;
  /**
   * The resultsTitle.
   */
  @ValueMapValue
  @Getter
  @Setter
  @Expose
  private String resultsTitle;
  /**
   * The ornament Id.
   */
  @Getter
  @Setter
  @Expose
  private String ornamentId;
  /**
   * The filtersTitle.
   */
  @Getter
  @Setter
  @Expose
  private String filtersTitle;
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
   * The dataLayerName.
   */
  @Getter
  @Setter
  @Expose
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
  /** Favorites Endpoint Model. */
  @Self
  @Getter
  private transient FavoritesApiEndpointsModel favoritesApiEndpointsModel;
  /**
   * Variable to hold list of slides.
   */
  @Expose
  @Getter
  private String updateFavUrl;
  /**
   * Variable to hold list of slides.
   */
  @Expose
  @Getter
  private String deleteFavUrl;

  /**
   * Save To Favorite Text.
   */
  @Expose
  private String saveToFavoritesText;

  /**
   * Saved To Favorite Text.
   */
  @Expose
  private String savedToFavoritesText;

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
      updateFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getUpdateFavUrl();
      deleteFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getDeleteFavUrl();
      if (templateType.equals("where-to-stay")) {
        endpoint = "/bin/api/v1/hotels?locale=" + language;
      } else if (templateType.equals("where-to-eat")) {
        endpoint = "/bin/api/v1/cards?locale=" + language + "&type=whereToEat";
      } else if (templateType.equals("browse-deals")) {
        endpoint = "/bin/api/v1/cards?locale=" + language + "&type=deals";
      } else if (templateType.equals("top-attractions")) {
        endpoint = "/bin/api/v2/activities?locale=" + language;
      }
      if (Objects.nonNull(i18nProvider)) { // update city to i18n value
        ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(language));
        setFiltersTitle(CommonUtils.getI18nString(i18nBundle, "filters"));
        setFilterButtonLabel(CommonUtils.getI18nString(i18nBundle, "filterlabel"));
        setClearFiltersButtonLabel(CommonUtils.getI18nString(i18nBundle, "clearall"));
        setLoadMoreButtonLabel(CommonUtils.getI18nString(i18nBundle, "loadmore"));
        if (StringUtils.isBlank(resultsTitle)) {
          setResultsTitle(CommonUtils.getI18nString(i18nBundle, "hotelsfound"));
        }
        setApplyFiltersButtonLabel(CommonUtils.getI18nString(i18nBundle, "apply"));
        setMultipleLocationsCopy(
            CommonUtils.getI18nString(i18nBundle, Constants.I18N_MULTIPLE_LOCATION_KEY));
        setDataLayerName("HotelFiltersForm");

        saveToFavoritesText = i18nBundle.getString(SAVE_TO_FAV_TEXT);
        savedToFavoritesText = i18nBundle.getString(I18_KEY_SAVED_TO_FAV);
      }
    } catch (Exception e) {
      LOGGER.error("Error in HotelFilterSlingModel");
    }

  }
}
