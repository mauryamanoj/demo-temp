package com.saudi.tourism.core.models.components.events.eventscards.v1;

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
import java.util.Locale;
import java.util.Objects;

import static com.saudi.tourism.core.utils.I18nConstants.LOAD_MORE_TEXT;

/** Events Cards Model. */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@With
public class EventsCardsModel {
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
   * Filter.
   */
  @Expose
  @ChildResource
  private EventsCardsFilterModel filter;

  /**
   * Sort.
   */
  @Expose
  @ChildResource
  private EventsCardsSortModel sort;

  /**
   * Display.
   */
  @Expose
  @ChildResource
  private EventsCardsDisplayModel display;


  /**
   * handpick.
   */
  @Expose
  @ChildResource
  private EventsCardsHandPickModel handpick;

  /**
   * Load More.
   */
  @Expose
  private EventsCardsLoadMoreModel loadMore;

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

    if (Objects.nonNull(filter) && StringUtils.isNotBlank(filter.getPeriod())) {
      filter = filter.withStartDate(null).withEndDate(null);
    }

    lang = CommonUtils.getLanguageForPath(currentResource.getPath());
    final var i18nBundle = i18nProvider.getResourceBundle(new Locale(lang));
    final var loadMoreLabel = i18nBundle.getString(LOAD_MORE_TEXT);
    loadMore = EventsCardsLoadMoreModel.builder().loadMoreLabel(loadMoreLabel).build();

    if (Objects.nonNull(display) && StringUtils.equals("list_view", display.getDisplayType())) {
      display = display.withCardSize("small");
    }

    if (Objects.nonNull(handpick) && CollectionUtils.isNotEmpty(handpick.getEventCFPaths())) {
      filter = null;
      sort = null;
    }

    apiUrl = saudiTourismConfigs.getEventsApiEndpoint();
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

  private void loadFavoriteConfig() {
    updateFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getUpdateFavUrl();
    deleteFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getDeleteFavUrl();
    getFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getGetFavUrl();
  }

}
