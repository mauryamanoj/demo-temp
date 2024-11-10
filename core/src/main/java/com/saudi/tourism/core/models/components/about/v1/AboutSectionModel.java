package com.saudi.tourism.core.models.components.about.v1;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Category;
import com.saudi.tourism.core.models.components.favorites.v1.FavoritesApiEndpointsModel;
import com.saudi.tourism.core.services.RunModeService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import com.saudi.tourism.core.utils.TagUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/** Model for About Section. */
@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class AboutSectionModel {


  // Injected services and resources
  /** tagManager. */
  @Inject private TagManager tagManager;
  /** resourceResolver. */
  @Inject private transient ResourceResolver resourceResolver;
  /** currentResource. */
  @Self private transient Resource currentResource;
  /** i18nProvider. */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private transient ResourceBundleProvider i18nProvider;

  // Exposed fields
  /** enableManualAuthoring. */
  @Expose
  @Default(booleanValues = false)
  @ValueMapValue private Boolean enableManualAuthoring;
  /** aboutTitle. */
  @Expose private String aboutTitle;
  /** aboutDescription. */
  @Expose private String aboutDescription;
  /** favoriteIcon. */
  @Expose private String favoriteIcon;
  /** shareIcon. */
  @Expose private String shareIcon;
  /** hideReadMore. */
  @Expose private Boolean hideReadMore;
  /** readMoreLabel. */
  @Expose private String readMoreLabel;
  /** readLessLabel. */
  @Expose private String readLessLabel;
  /** hideFavorite. */
  @Expose private Boolean hideFavorite;
  /** hideShare. */
  @Expose private Boolean hideShare;

  /** Servlet endpoint to update favorites. */
  @Expose
  private String updateFavUrl;

  /** Servlet endpoint to delete a favorite. */
  @Expose
  private String deleteFavUrl;

  /** Servlet endpoint to fetch favorites. */
  @Expose
  private String getFavUrl;

  /** favId. */
  @Expose
  private String favId;

  // Internal fields
  /** category CF Paths. */
  @ValueMapValue
  private List<String> categoryCFPaths;
  /** categories Icon. */
  @ValueMapValue
  private List<String> categories;
  /** categories Tags. */
  @Expose private List<Category> categoriesTags;
  /** language. */
  private String language = StringUtils.EMPTY;

  /**
   * saudiTourismConfig.
   */
  @Inject
  private SaudiTourismConfigs saudiTourismConfig;

  /**
   * RunMode Service.
   *
   * N.B: It's marked transient to avoid serializing it via CommonUtils.writeNewJSONFormat.
   */
  @OSGiService
  @Getter(AccessLevel.NONE)
  private transient RunModeService runModeService;

  /**
   * Favorites Endpoint Model.
   */
  @Self
  private transient FavoritesApiEndpointsModel favoritesApiEndpointsModel;


  @PostConstruct
  void init() {
    language = CommonUtils.getLanguageForPath(currentResource.getPath());
    loadFavoriteConfig();
    if (!enableManualAuthoring) {
      loadFromContentFragment();
    } else {
      loadFromManualAuthoring();
    }
    if (StringUtils.isNotEmpty(aboutDescription)) {
      aboutDescription = LinkUtils.rewriteLinksInHtml(aboutDescription, resourceResolver,
          runModeService.isPublishRunMode());
    }
    processCategoriesTags();
  }

  private void loadFromContentFragment() {
    var pageManager = resourceResolver.adaptTo(PageManager.class);
    var currentPage = pageManager.getContainingPage(currentResource);
    if (currentPage == null || StringUtils.isEmpty(currentPage.getProperties()
        .get("referencedFragmentPath", String.class))) {
      return;
    }

    var cfResource = resourceResolver.getResource(currentPage.getProperties()
        .get("referencedFragmentPath", String.class));
    if (cfResource == null) {
      return;
    }
    var cfModel = cfResource.adaptTo(AboutCFModel.class);
    if (cfModel == null) {
      return;
    }

    var i18n = i18nProvider.getResourceBundle(new Locale(language));
    setFieldsFromContentFragment(cfModel, i18n);
  }

  private void setFieldsFromContentFragment(AboutCFModel cfModel, ResourceBundle i18n) {
    aboutTitle = cfModel.getAboutTitle();
    aboutDescription = cfModel.getAboutDescription();
    hideReadMore = false;
    readMoreLabel = cfModel.getReadMoreLabel();
    readLessLabel = cfModel.getReadLessLabel();
    hideFavorite = !saudiTourismConfig.getEnableFavorite() ||  cfModel.isHideFavorite();
    hideShare = false;
    categories = cfModel.getCategoriesIcon();
  }

  private void loadFromManualAuthoring() {
    if (currentResource == null) {
      return;
    }
    var valueMap = currentResource.getValueMap();
    aboutTitle = valueMap.get("aboutTitle", String.class);
    aboutDescription = valueMap.get("aboutDescription", String.class);
    favoriteIcon = valueMap.get("favoriteIcon", String.class);
    shareIcon = valueMap.get("shareIcon", String.class);
    hideReadMore = valueMap.get("hideReadMore", Boolean.class);
    readMoreLabel = valueMap.get("readMoreLabel", String.class);
    readLessLabel = valueMap.get("readLessLabel", String.class);
    hideFavorite = !saudiTourismConfig.getEnableFavorite() || valueMap.get("hideFavorite", Boolean.class);
    hideShare = valueMap.get("hideShare", Boolean.class);
  }

  private void processCategoriesTags() {
    if (CollectionUtils.isNotEmpty(categories)) {
      categoriesTags = categories.stream()
        .map(p -> TagUtils.getTagOrChildren(p, tagManager, resourceResolver, language))
        .flatMap(List::stream)
        .collect(Collectors.toList());
    }
  }

  private void loadFavoriteConfig() {

    if (favoritesApiEndpointsModel != null) {
      final var pageManager = resourceResolver.adaptTo(PageManager.class);
      final var currentPage = pageManager.getContainingPage(currentResource);
      if (Objects.nonNull(currentPage)) {
        var path = currentPage.getPath();
        favId = LinkUtils.getFavoritePath(path);
      }
      updateFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getUpdateFavUrl();
      deleteFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getDeleteFavUrl();
      getFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getGetFavUrl();
    }
  }
  @JsonIgnore
  public String getJson() {
    var gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    return gsonBuilder.create().toJson(this);
  }


}
