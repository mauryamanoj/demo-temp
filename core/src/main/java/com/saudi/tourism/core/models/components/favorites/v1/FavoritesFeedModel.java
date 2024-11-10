package com.saudi.tourism.core.models.components.favorites.v1;

import com.day.cq.wcm.api.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Data;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.saudi.tourism.core.utils.I18nConstants.LOAD_MORE_TEXT;
import static com.saudi.tourism.core.utils.I18nConstants.SAVE_TO_FAV_TEXT;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_SAVED_TO_FAV;

@Model(
    adaptables = {Resource.class, SlingHttpServletRequest.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class FavoritesFeedModel {
  /** No Result title. */
  @ValueMapValue
  @Expose
  private String noResultTitle;

  /** No Result Copy. */
  @ValueMapValue
  @Expose
  private String noResultCopy;

  /** Load More i18n traduction. */
  @Expose
  private String loadMore;

  /** The Current page. */
  @ScriptVariable
  private Page currentPage;

  /** i18n provider. */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private ResourceBundleProvider i18nProvider;

  /** Favorites Endpoint Model. */
  @Self
  private FavoritesApiEndpointsModel favoritesApiEndpointsModel;

  /** Servlet endpoint to update favorites. */
  @Expose
  private String updateFavUrl;

  /** Servlet endpoint to delete a favorite. */
  @Expose
  private String deleteFavUrl;

  /** Servlet endpoint to fetch favorites. */
  @Expose
  private String getFavUrl;

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

  /** init method of sling model. */
  @PostConstruct
  protected void init() {
    final String lang =
        CommonUtils.getPageNameByIndex(
            currentPage.getPath(), Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION);

    final ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(lang));

    this.loadMore = i18nBundle.getString(LOAD_MORE_TEXT);
    saveToFavoritesText = i18nBundle.getString(SAVE_TO_FAV_TEXT);
    savedToFavoritesText = i18nBundle.getString(I18_KEY_SAVED_TO_FAV);
    updateFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getUpdateFavUrl();
    deleteFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getDeleteFavUrl();
    getFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getGetFavUrl();
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
