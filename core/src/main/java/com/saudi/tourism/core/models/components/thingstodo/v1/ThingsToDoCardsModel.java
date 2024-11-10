package com.saudi.tourism.core.models.components.thingstodo.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.components.favorites.v1.FavoritesApiEndpointsModel;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.gson.IsoCalendarAdapter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.saudi.tourism.core.utils.Constants.DEFAULT_CATEGORY_ICON;
import static com.saudi.tourism.core.utils.I18nConstants.LOAD_MORE_TEXT;
import static com.saudi.tourism.core.utils.I18nConstants.ALL_LABEL;

/** Things ToDo Cards Model. */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@With
public class ThingsToDoCardsModel {

  /**
   * Component Id.
   */
  @ValueMapValue
  @Expose
  private String componentId;
  /**
   * Current Resource.
   */
  @Self
  private transient Resource currentResource;

  /** Reference of Saudi Tourism Configuration. */
  @Inject
  private transient SaudiTourismConfigs saudiTourismConfigs;

  /** i18n provider. */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private transient ResourceBundleProvider i18nProvider;

  /**
   * Title.
   */
  @Expose
  @ValueMapValue
  private String title;

  /**
   * Link.
   */
  @Expose
  @ChildResource
  private Link link;


  /**
   * Display.
   */
  @Expose
  @ChildResource
  private ThingsToDoCardsDisplayModel display;


  /**
   * handpick.
   */
  @Expose
  @ChildResource
  private ThingsToDoCardsHandPickModel handpick;

  /**
   * Filter.
   */
  @Expose
  @ChildResource
  private ThingsToDoCardsFilterModel filter;

  /**
   * tabs.
   */
  @ChildResource
  @Setter
  @Expose
  private List<Tab> tabs;


  /**
   * Sort.
   */
  @Expose
  @ChildResource
  private ThingsToDoCardsSortModel sort;

  /**
   * Load More.
   */
  @Expose
  private ThingsToDoCardsLoadMoreModel loadMore;

  /**
   * Events api url.
   */
  @Expose
  @ValueMapValue
  private String apiUrl;
  /**
   * Servlet endpoint to update favorites.
   */
  @Expose
  private String updateFavUrl;

  /**
   * Servlet endpoint to delete a favorite.
   */
  @Expose
  private String deleteFavUrl;

  /**
   * Servlet endpoint to fetch favorites.
   */
  @Expose
  private String getFavUrl;

  /**
   * Favorites Endpoint Model.
   */
  @Self
  private transient FavoritesApiEndpointsModel favoritesApiEndpointsModel;


  /**
   * language.
   */
  @JsonIgnore
  private String lang = Constants.DEFAULT_LOCALE;

  @PostConstruct
  void init() {

    loadFavoriteConfig();
    lang = CommonUtils.getLanguageForPath(currentResource.getPath());
    final var i18nBundle = i18nProvider.getResourceBundle(new Locale(lang));
    final var loadMoreLabel = i18nBundle.getString(LOAD_MORE_TEXT);
    loadMore = ThingsToDoCardsLoadMoreModel.builder().loadMoreLabel(loadMoreLabel).build();

    if (Objects.nonNull(display) && StringUtils.equals("list_view", display.getDisplayType())) {
      display = display.withCardSize("small");
    }

    if (Objects.nonNull(handpick) && CollectionUtils.isNotEmpty(handpick.getThingsToDoCFPaths())) {
      filter = null;
      sort = null;
    }

    addTabAtFirstPositionIfNotEmpty(i18nBundle);

    apiUrl = saudiTourismConfigs.getThingsToDoApiEndpoint();
  }

  private void loadFavoriteConfig() {
    if (favoritesApiEndpointsModel != null) {
      updateFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getUpdateFavUrl();
      deleteFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getDeleteFavUrl();
      getFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getGetFavUrl();
    }
  }

  /**
   * Add tab at first position if not empty.
   * @param i18nBundle
   */
  public void addTabAtFirstPositionIfNotEmpty(ResourceBundle i18nBundle) {
    // Check if the tabs list is not null and not empty
    if (CollectionUtils.isNotEmpty(tabs)) {
      // Add newTab at the first position
      String allLabel = i18nBundle.getString(ALL_LABEL);
      tabs.add(0, Tab.builder().
          title(allLabel)
          .icon(DEFAULT_CATEGORY_ICON)
          .build());
    }
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder
        .excludeFieldsWithoutExposeAnnotation()
        .setPrettyPrinting()
        .registerTypeHierarchyAdapter(Calendar.class, new IsoCalendarAdapter());
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
