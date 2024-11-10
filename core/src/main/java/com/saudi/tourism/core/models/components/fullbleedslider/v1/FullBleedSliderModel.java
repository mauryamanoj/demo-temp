package com.saudi.tourism.core.models.components.fullbleedslider.v1;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMMode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.ComponentHeading;
import com.saudi.tourism.core.models.common.Slide;
import com.saudi.tourism.core.models.components.favorites.v1.FavoritesApiEndpointsModel;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.saudi.tourism.core.utils.I18nConstants.SAVE_TO_FAV_TEXT;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_SAVED_TO_FAV;

/**
 * C-04 Full Bleed Slider Model.
 *
 * @author cbarrios
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class FullBleedSliderModel {

  /**
   * Variable to hold component title.
   */
  @Getter
  @ChildResource
  @Expose
  private ComponentHeading componentHeading;

  /**
   * Variable to hold list of slides.
   */
  @ChildResource
  @Expose
  private List<Slide> slides;

  /**
   * i18nProvider.
   */
  @OSGiService(filter = Constants.I18N_PROVIDER_SERVICE_TARGET)
  private ResourceBundleProvider i18nProvider;

  /**
   * Variable to hold list of slides.
   */
  @Expose
  private String getFavUrl;
  /**
   * Variable to hold list of slides.
   */
  @Expose
  private String updateFavUrl;
  /**
   * Variable to hold list of slides.
   */
  @Expose
  private String deleteFavUrl;
  /** Favorites Endpoint Model. */
  @Self
  private FavoritesApiEndpointsModel favoritesApiEndpointsModel;
  /**
   * language.
   */
  private String language;

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
   * is editor mode.
   */
  @Expose
  private boolean isEditer;

  /**
   * Variable for current resource.
   */
  @ScriptVariable
  private Page currentPage;

  /**
   * The request.
   */
  @SlingObject
  @JsonIgnore
  private transient SlingHttpServletRequest request;

  /**
   * Initialize the properties.
   */
  @PostConstruct private void init() {
    isEditer = false;
    if (null != request) {
      isEditer = (WCMMode.EDIT.equals(WCMMode.fromRequest(request))
          || WCMMode.PREVIEW.equals(WCMMode.fromRequest(request)));
    }
    try {
      Resource currentResource = currentPage.getContentResource();
      if (Objects.nonNull(currentResource) && Objects.nonNull(i18nProvider)) {
        language = CommonUtils.getLanguageForPath(currentResource.getPath());
        ResourceBundle i18n = i18nProvider.getResourceBundle(new Locale(language));
        saveToFavoritesText = i18n.getString(SAVE_TO_FAV_TEXT);
        savedToFavoritesText = i18n.getString(I18_KEY_SAVED_TO_FAV);
        updateFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getUpdateFavUrl();
        deleteFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getDeleteFavUrl();
        getFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getGetFavUrl();
      }
    } catch (Exception e) {
      LOGGER.error("Error at init method", e);
    }
    slides = slides.stream().filter(slide -> !slide.isHideSlide()).collect(Collectors.toList());
  }
  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
