package com.saudi.tourism.core.models.components.packages;

import com.day.cq.wcm.api.Page;
import com.saudi.tourism.core.models.components.AdminPageOption;
import com.saudi.tourism.core.models.components.events.ButtonsModel;
import com.saudi.tourism.core.models.components.events.FilterModel;
import com.saudi.tourism.core.utils.AdminUtil;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
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
import java.util.function.Predicate;

import static com.saudi.tourism.core.utils.Constants.API_V2_BASE_PATH;

/**
 * The Class EventListSlingModel.
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class PackageFilterSlingModel implements Serializable {
  /**
   * The Current page.
   */
  @ScriptVariable
  private Page currentPage;
  /**
   * The constant PACKAGE_COUNT.
   */
  private static final int PACKAGES_DEFAULT_RESULTS_PER_PAGE = 10;

  /**
   * Predicate for price filter.
   */
  private static final Predicate<FilterModel> PRICE_FILTER =
      filter -> filter.getType().equals(PackageFilterModel.PROP_PRICE);

  /**
   * Packages default ornament.
   */
  private static final String PACKAGES_DEFAULT_ORNAMENT = "08";

  /**
   * The stateLabel.
   */
  @ChildResource
  @Getter
  private List<FilterModel> filters;
  /**
   * The noResults.
   */
  @ChildResource
  @Getter
  private NoResultSlingModel noResultsMessage;

  /**
   * The clearFiltersButtonLabel.
   */
  @Getter
  @Setter
  private String clearFiltersButtonLabel;
  /**
   * The packagesEndpoint.
   */
  @Getter
  @Setter
  private String endpoint;
  /**
   * The dataLayerName.
   */
  @Getter
  @Setter
  private String dataLayerName;
  /**
   * The applyFiltersButtonLabel.
   */
  @Getter
  @Setter
  private String applyFiltersButtonLabel;
  /**
   * The clearButtonLabel.
   */
  @Getter
  @Setter
  private String clearButtonLabel;
  /**
   * The loadMoreButtonLabel.
   */
  @Getter
  @Setter
  private String loadMoreButtonLabel;
  /**
   * The favoriteApiBasePath.
   */
  @Getter
  @Setter
  private String favoriteApiBasePath;
  /**
   * The resultsTitle.
   */
  @Getter
  @Setter
  private String resultsTitle;
  /**
   * The filtersTitle.
   */
  @Getter
  @Setter
  private String filtersTitle;
  /**
   * The packageDetailsLinkLabel.
   */
  @Getter
  @Setter
  private String packageDetailsLinkLabel;
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
   * The multipleLocationsCopy.
   */
  @Getter
  @Setter
  private String multipleLocationsCopy;

  /**
   * The ornament Id.
   */
  @Getter
  @Setter
  private String ornamentId;

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
    Resource currentResourcePath = currentPage.getContentResource();
    try {
      String path = Optional.ofNullable(currentResource.getParent()).map(Resource::getPath)
          .orElse(StringUtils.EMPTY);
      // language needed for tag translation title
      String language =
          CommonUtils.getPageNameByIndex(path, Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION);
      resultsPerPage = Optional.of(Integer.parseInt(resultsPerPageAuth)).
              orElse(PACKAGES_DEFAULT_RESULTS_PER_PAGE);
      endpoint = "/bin/api/v1/packages?locale=" + language;
      ornamentId = PACKAGES_DEFAULT_ORNAMENT;
      favoriteApiBasePath = API_V2_BASE_PATH;
      ButtonsModel showAllButton = new ButtonsModel();

      AdminPageOption adminOptions = AdminUtil.getAdminOptions(language,
          CommonUtils.getSiteName(currentResource.getPath()));

      if (adminOptions.isHidePackagesPrice()) {
        filters.removeIf(PRICE_FILTER);
      }

      if (Objects.nonNull(i18nProvider)) { // update city to i18n value
        ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(language));

        setFiltersTitle(CommonUtils.getI18nString(i18nBundle, "filters"));
        setClearFiltersButtonLabel(CommonUtils.getI18nString(i18nBundle, "clearall"));
        setPackageDetailsLinkLabel(CommonUtils.getI18nString(i18nBundle, "learnmore"));
        setClearButtonLabel(CommonUtils.getI18nString(i18nBundle, "clearall"));
        setLoadMoreButtonLabel(CommonUtils.getI18nString(i18nBundle, "loadmore"));
        setResultsTitle(CommonUtils.getI18nString(i18nBundle, "packagesfound"));
        setDataLayerName("TourPackagesFiltersForm");
        setApplyFiltersButtonLabel(CommonUtils.getI18nString(i18nBundle, "apply"));
        showAllButton.setShowAllLabel(CommonUtils.getI18nString(i18nBundle, "showall"));
        showAllButton.setShowLessLabel(CommonUtils.getI18nString(i18nBundle, "showless"));
        setMultipleLocationsCopy(
            CommonUtils.getI18nString(i18nBundle, Constants.I18N_MULTIPLE_DESTINATION_KEY));
      }



      for (FilterModel filter : filters) {
        filter.setShowAllButton(showAllButton);
      }
    } catch (Exception e) {
      LOGGER.error("Error in PackageFilterSlingModel");
    }

  }
}
