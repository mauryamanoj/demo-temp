package com.saudi.tourism.core.models.components.smallstories.v1;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.day.cq.commons.Externalizer;
import com.day.cq.tagging.TagManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.components.contentfragment.destination.DestinationCFModel;
import com.saudi.tourism.core.models.components.favorites.v1.FavoritesApiEndpointsModel;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.stories.v1.StoriesCFService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

/**
 * Model for Small Stories component.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class SmallStoriesModel {
  /** tagManager. */
  @Inject
  private transient TagManager tagManager;

  /**
   * Default number of stories.
   */
  private static final int DEFAULT_NUMBER_OF_STORIES = 10;

  /**
   * Resource Resolver.
   */
  @JsonIgnore
  @Inject
  private ResourceResolver resourceResolver;

  /**
   * Current resource.
   */
  @Self
  private Resource resource;

  /**
   * Sling settings service.
   */
  @JsonIgnore
  @OSGiService
  private transient SlingSettingsService settingsService;

  /** Reference of Saudi Tourism Configuration. */
  @Inject
  private transient SaudiTourismConfigs saudiTourismConfigs;

  /**
   * StoriesCFService.
   */
  @OSGiService
  private transient StoriesCFService storiesCFService;

  /**
   * Fetch latest stories.
   */
  @Default(booleanValues = false)
  @ValueMapValue
  @Expose
  private Boolean fetchLatestStories;

  /**
   * ApiUrl.
   */
  @Expose
  private String apiUrl;

  /**
   * Number of stories.
   */
  @Expose
  @ValueMapValue
  private Integer numberOfStories = DEFAULT_NUMBER_OF_STORIES; // Default value set to the constant

  /**
   * Title.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * Type of Cards.
   */
  @Expose
  private final String type = "BuyCards";

  /**
   * Link.
   */
  @ChildResource
  @Expose
  private Link link;


  /**
   * Stories.
   */
  @ValueMapValue
  @JsonIgnore
  private transient List<String> cardCFPaths;

  /**
   * List of Story cards.
   */
  @Expose
  private List<CardModel> cards;

  /**
   * saudiTourismConfig.
   */
  @Inject
  private SaudiTourismConfigs saudiTourismConfig;

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
   * Filter.
   */
  @Expose
  @ChildResource
  private SmallStoriesFilterModel filter;

  /**
   * Favorites Endpoint Model.
   */
  @Self
  private transient FavoritesApiEndpointsModel favoritesApiEndpointsModel;

  @PostConstruct
  void init() {
    String language = CommonUtils.getLanguageForPath(resource.getPath());
    if (Objects.isNull(tagManager)) {
      tagManager = resourceResolver.adaptTo(TagManager.class);
    }
    Locale locale = new Locale(language);
    loadFavoriteConfig();
    if (fetchLatestStories) {
      apiUrl = saudiTourismConfigs.getStoriesApiEndpoint();
    } else if (CollectionUtils.isNotEmpty(cardCFPaths)) {
      cards =
          cardCFPaths.stream()
              .filter(StringUtils::isNotEmpty)
              .map(
                  p -> {

                    final var storyCF = resourceResolver.getResource(p);
                    if (storyCF == null) {
                      return null;
                    }
                    final var cardModel = storyCF.adaptTo(CardModel.class);

                    if (Objects.nonNull(cardModel)) {
                      cardModel.setHideFavorite(!saudiTourismConfig.getEnableFavorite() || cardModel.getHideFavorite());
                    }

                    final var destinationResource =
                        resourceResolver.getResource(cardModel.getDestinationPath());
                    if (destinationResource != null
                        && destinationResource.adaptTo(DestinationCFModel.class) != null) {
                      cardModel.setCity(
                          destinationResource.adaptTo(DestinationCFModel.class).getTitle());
                    }
                    Image image = cardModel.getImage();
                    if (Objects.nonNull(image)) {
                      DynamicMediaUtils.setAllImgBreakPointsInfo(
                          image,
                          "crop-375x667",
                          "crop-375x667",
                          "1280",
                          "420",
                          resource.getResourceResolver(),
                          resource);
                    }

                    cardModel.setCardCtaLink(
                        LinkUtils.getAuthorPublishUrl(
                            resourceResolver,
                            cardModel.getCardCtaLink(),
                            settingsService.getRunModes().contains(Externalizer.PUBLISH)));

                    if (tagManager != null && StringUtils.isNotBlank(cardModel.getTag())) {
                      String tagName = CommonUtils.getTagName(cardModel.getTag(), tagManager, locale);
                      cardModel.setTag(tagName);
                    }

                    return cardModel;
                  })
              .filter(Objects::nonNull)
              .collect(Collectors.toList());
    }
  }

  private void loadFavoriteConfig() {
    if (favoritesApiEndpointsModel != null) {
      updateFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getUpdateFavUrl();
      deleteFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getDeleteFavUrl();
      getFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getGetFavUrl();
    }
  }
  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }


}
