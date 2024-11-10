package com.saudi.tourism.core.models.components.nav.v2;

import com.day.cq.commons.jcr.JcrConstants;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.DictItem;
import com.saudi.tourism.core.models.common.ImageCaption;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.common.RegionCity;
import com.saudi.tourism.core.models.components.ArticleItem;
import com.saudi.tourism.core.models.components.nav.v3.NavigationLink;
import com.saudi.tourism.core.models.components.nav.v4.NavCard;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.servlets.WeatherServlet;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;
import com.saudi.tourism.core.utils.NavItemUtils;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;

/**
 * Define all properties of Navigation Tab.
 */
@Data
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@NoArgsConstructor
public class NavigationTab {

  /**
   * The Graphical mode.
   */
  @ChildResource
  @Expose
  private String navType;

  /**
   * The theme.
   */
  @ValueMapValue
  @Expose
  private String theme;

  /**
   * The image.
   */
  @ValueMapValue
  @Expose
  private String image;

  /**
   * The s7image.
   */
  @ValueMapValue
  @Expose
  private String s7image;
  /**
   * The url.
   */
  @ValueMapValue
  @Expose
  private String url;

  /**
   * The disableCurrencyWeather.
   */
  @ValueMapValue
  @Expose
  private boolean disableCurrencyWeather;

  /**
   * Label for the minimum temperature.
   */
  @ValueMapValue
  @Expose
  private String minTemp;

  /**
   * Label for the maximum temperature.
   */
  @ValueMapValue
  @Expose
  private String maxTemp;

  /**
   * The defaultWeatherCity.
   */
  @Expose
  private String defaultWeatherCity;

  /**
   * The defaultCurrency.
   */
  @ValueMapValue
  @Expose
  private String defaultCurrency;

  /**
   * Image caption.
   */
  @ChildResource
  @Expose
  private ImageCaption caption;

  /**
   * The urlWithExtension.
   */
  @Getter
  @Expose
  private String urlWithExtension;

  /**
   * The Name.
   * For the frontend structure, it's nav/copy.
   */
  @ValueMapValue(name = JcrConstants.JCR_TITLE)
  @Expose
  private String name;

  /**
   * The first section of dropdown panel, according to the current frontend json file, it's the
   * first column (columns/links).
   */
  @Getter
  @Expose
  private List<NavigationLink> linksColumn = new LinkedList<>();


  /**
   * Widget layout.
   */
  @ValueMapValue
  @Expose
  private boolean widgetLayout;

  /**
   * The middle part of dropdown panel, when there's a card is displayed.
   * According to the frontend json file, it's the second column named `widgets` of type `card`
   * (columns/widgets[type=card]), if any.
   */
  @Getter
  @Expose
  private ArticleItem articleItem;

  /**
   * The middle part of dropdown panel "Secondary Links".
   * According to the frontend json file, it's the second column named `secondary-links` in the
   * columns array (columns/secondary-links).
   */
  @ChildResource(name = Constants.PLANS)
  @Expose
  private List<PlanItem> secondaryLinks = new LinkedList<>();

  /**
   * The path to article to be displayed as a card.
   */
  @ValueMapValue
  @Expose
  private String articlePath;

  /**
   * The article Image.
   */
  @ValueMapValue
  @Expose
  private String articleImage;
  /**
   * The s7article Image.
   */
  @ValueMapValue
  @Expose
  private String s7articleImage;

  /**
   * The currencies.
   */
  @ChildResource
  @Expose
  private List<DictItem> currencies;

  /**
   * The cities available for weather.
   */
  @Expose
  private List<DictItem> cities = new LinkedList<>();

  /**
   * Upper link to be displayed on events menu dropdown.
   */
  @ValueMapValue
  @Expose
  private Link upperLink;

  /**
   * The Graphical mode.
   */
  @ChildResource
  @Expose
  private List<NavCard> cards;

  /**
   * The weather API.
   */
  @Expose
  private String weatherWidgetUrl = WeatherServlet.SIMPLE_WEATHER_API;

  /**
   * The extended weather API.
   */
  @Expose
  private String extendedWeatherWidgetUrl = WeatherServlet.EXTENDED_WEATHER_API;

  /**
   * Inject constructor, initializes cities field.
   *
   * @param currentResource the current navigation tab resource
   * @param citiesSvc       cities service to get localized city name
   */
  @Inject
  public NavigationTab(@Self final Resource currentResource,
      @OSGiService final RegionCityService citiesSvc) {
    final String[] cityIds =
        ResourceUtil.getValueMap(currentResource).get("cities", String[].class);
    if (ArrayUtils.isEmpty(cityIds)) {
      return;
    }
    assert cityIds != null;

    final String locale = CommonUtils.getLanguageForPath(currentResource.getPath());
    for (String cityId : cityIds) {
      final RegionCity city = citiesSvc.getRegionCityById(cityId, locale);
      if (city != null) {
        cities.add(new DictItem(cityId, city.getName()));
      }
    }
  }

  /**
   * init method of sling model.
   */
  @PostConstruct
  protected void init() {
    urlWithExtension = LinkUtils.getUrlWithExtension(url);
    if (CollectionUtils.isNotEmpty(cities)) {
      setDefaultWeatherCity(cities.get(0).getCode());
    }
    if (CollectionUtils.isNotEmpty(currencies)) {
      setDefaultCurrency(currencies.get(0).getValue());
    }
  }

  /**
   * This getter corresponds to the explicit nest field in the current frontend implementation.
   * <p>
   * `nest` means the navigation item has a dropdown with child items, it should answer true if it
   * is events column or has child items ({@link #linksColumn}) or middle-column elements
   * ({@link #articleItem} or {@link #secondaryLinks}).
   *
   * @return {@code true} if nest, {@code false} otherwise
   */
  public boolean isNest() {
    return hasColumns();
  }

  /**
   * Returns true if this nav item has columns ({@link #linksColumn}) or middle-column
   * elements ({@link #articleItem} or {@link #secondaryLinks}), according to frontend structure,
   * it checks nav/columns.
   *
   * @return true if nav/columns
   */
  public boolean hasColumns() {
    return !CollectionUtils.isEmpty(linksColumn) || (
        NavItemUtils.NAV_TYPE_ARTICLES.equals(this.navType) && articleItem != null) || (
        NavItemUtils.NAV_TYPE_PLAN.equals(this.navType) && !CollectionUtils
            .isEmpty(secondaryLinks));
  }

  /**
   * Returns true if this nav item is of type widgets (weathercurrency) or has an article to
   * display.
   *
   * @return true if nav/columns/widgets
   */
  public boolean hasWidgets() {
    return NavItemUtils.NAV_TYPE_WIDGETS.equals(this.navType) || (
        NavItemUtils.NAV_TYPE_ARTICLES.equals(this.navType) && articleItem != null);
  }
}
