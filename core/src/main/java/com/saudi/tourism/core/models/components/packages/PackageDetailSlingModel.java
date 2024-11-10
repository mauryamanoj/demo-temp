package com.saudi.tourism.core.models.components.packages;

import com.saudi.tourism.core.models.components.events.FilterModel;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * The Class EventListSlingModel.
 */
@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class PackageDetailSlingModel {
  /**
   * The constant DURATION_I18KEY.
   */
  private static final String DURATION_I18KEY = "Duration";
  /**
   * The constant AREA_I18KEY.
   */
  private static final String AREA_I18KEY = "Area";
  /**
   * The constant ACTIVITIES_I18KEY.
   */
  private static final String ACTIVITIES_I18KEY = "Activities";
  /**
   * The constant TRAVELLERS_I18KEY.
   */
  private static final String TRAVELLERS_I18KEY = "Travellers";
  /**
   * The constant PRICE_I18KEY.
   */
  private static final String PRICE_I18KEY = "Price";
  /**
   * The title.
   */
  @Self
  @Getter
  private PackageDetail packageDetail;
  /**
   * The filters.
   */
  @Getter
  private List<FilterModel> filters;
  /**
   * The Areas list String.
   */
  @Getter
  private String areasEn;
  /**
   * Areas.
   */
  @Getter
  private String areas;
  /**
   * ResourceBundleProvider.
   */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private ResourceBundleProvider i18nProvider;
  /**
   * currentResource.
   */
  @Self
  private Resource currentResource;

  /**
   * /**
   * The init method.
   */
  @PostConstruct protected void init() {
    try {
      String path = Optional.ofNullable(currentResource.getParent()).map(Resource::getPath)
          .orElse(StringUtils.EMPTY);
      String language =
          CommonUtils.getPageNameByIndex(path, Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION);
      filters = new ArrayList<>();
      areas = trimTagValues(CommonUtils.getListString(Constants.COMMA, packageDetail.getArea()));
      if (Objects.nonNull(i18nProvider)) { // update city to i18n value
        ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(language));
        buildFilter(CommonUtils.getI18nString(i18nBundle, AREA_I18KEY),
                CommonUtils.getListString(packageDetail.getArea()), "area");
        buildFilter(CommonUtils.getI18nString(i18nBundle, DURATION_I18KEY),
            packageDetail.getDuration().getValue(), "duration");
        if (Objects.nonNull(packageDetail.getPrice()) && !packageDetail.getPrice().isEmpty()) {
          buildFilter(CommonUtils.getI18nString(i18nBundle, PRICE_I18KEY),
                  packageDetail.getPriceDisplay(), "price");
        }
        buildFilter(CommonUtils.getI18nString(i18nBundle, ACTIVITIES_I18KEY),
            CommonUtils.getListString(packageDetail.getCategory()), "binoculars");
        buildFilter(CommonUtils.getI18nString(i18nBundle, TRAVELLERS_I18KEY),
            CommonUtils.getListString(packageDetail.getTarget()), "user-icon");
      }

      //list of areas in english for analytics
      areasEn = trimTagValues(packageDetail.getAreasEn());

    } catch (Exception e) {
      LOGGER.error("Error in PackageFilterSlingModel");
    }
  }

  /**
   * Build filter.
   *
   * @param name  the name
   * @param value the value
   * @param icon  the icon
   */
  private void buildFilter(final String name, final String value, final String icon) {
    FilterModel filterModel = new FilterModel();
    filterModel.setType(icon);
    filterModel.setFilterTitle(name);
    filterModel.setPlaceHolderText(value);
    filters.add(filterModel);
  }

  /**
   * Trim spaces for every string separated by comma. Tags that consists of multiple cities
   * separated by comma should share the same logic.
   * @param joinedTags tags
   * @return String of values where each single value separated by comma is trimmed.
   */
  private static String trimTagValues(@NonNull String joinedTags) {
    return Arrays.asList(joinedTags.split(Constants.COMMA))
        .stream()
        .map(String::trim)
        .distinct()
        .collect(Collectors.joining(Constants.COMMA));
  }

}
