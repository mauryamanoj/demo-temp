package com.saudi.tourism.core.models.components.nav.v3;

import com.day.cq.wcm.api.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.saudi.tourism.core.utils.Constants.API_V2_BASE_PATH;

/**
 * LoginModalSlingModel.
 */
@Model(adaptables = SlingHttpServletRequest.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LoginModalSlingModel {
  /**
   * 'Sign In' i18n key.
   */
  static final String SIGN_IN_I18N_KEY = "Sign In";

  /**
   * 'Sign Out' i18n key.
   */
  static final String SIGN_OUT_I18N_KEY = "Sign Out";

  /**
   * 'Personal Information' i18n key.
   */
  static final String PERSONAL_INFORMATION_I18N_KEY = "Personal Information";

  /**
   * 'Log In' i18n key.
   */
  static final String LOG_IN_I18N_KEY = "Log In";

  /**
   * 'My Favorites' i18n key.
   */
  static final String MY_FAVORITES_I18N_KEY = "My Favorites";

  /**
   * 'My trips' i18n key.
   */
  static final String MY_TRIPS_I18N_KEY = "My trips";

  /**
   * Default account path.
   */
  static final String DEFAULT_ACCOUNT_PATH = "/en/account";

  /**
   * SSID domain.
   */
  @Expose
  @Getter
  @Inject
  @Optional
  private String domain;

  /**
   * SSID clientId.
   */
  @Expose
  @Getter
  @Inject
  @Optional
  private String clientId;

  /**
   * CTA Login Text.
   */
  @Expose
  @Getter
  private String ctaLoginText;

  /**
   * Acoount Path.
   */
  @Expose
  @Getter
  @Inject
  @Optional
  private String accountPath;

  /**
   * MyFavorites Path.
   */
  @Getter
  @Inject
  @Optional
  private String myFavorites;


  /**
   * myTrips Path.
   */
  @Getter
  @Inject
  @Optional
  private String myTrips;

  /**
   * 'Sign In' traduction.
   */
  @Expose
  @Getter
  private String signInCopy;

  /**
   * 'Sign Out' traduction.
   */
  @Expose
  @Getter
  private String signOutCopy;

  /**
   * Profil avatar config.
   */
  @Expose
  @Getter
  private Avatar avatar;

  /**
   * V2 API path.
   */
  @Expose
  @Getter
  private String favoriteApiPath;

  /**
   * Messages to show when SSID API returns an error.
   */
  @Expose
  @Getter
  private ApiError apiError;

  /**
   * User API endpoints config.
   */
  @Expose
  @Getter
  private UserApi userApi;

  /**
   * Profile sub menus.
   */
  @Expose
  @Getter
  private List<SubMenu> submenu;

  /**
   * The Current page.
   */
  @ScriptVariable
  private Page currentPage;

  /**
   * The request.
   */
  @SlingObject
  private SlingHttpServletRequest request;

  /**
   * i18n provider.
   */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private ResourceBundleProvider i18nProvider;

  /**
   * init method of sling model.
   */
  @PostConstruct
  private void init() {
    final String lang =
        CommonUtils.getPageNameByIndex(
            currentPage.getPath(), Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION);

    this.avatar = new Avatar();

    this.apiError = new ApiError();

    this.favoriteApiPath = API_V2_BASE_PATH;

    this.userApi = new UserApi(domain, clientId);

    final ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(lang));
    this.ctaLoginText = i18nBundle.getString(LOG_IN_I18N_KEY);
    this.signInCopy = i18nBundle.getString(SIGN_IN_I18N_KEY);
    this.signOutCopy = i18nBundle.getString(SIGN_OUT_I18N_KEY);

    if (StringUtils.isEmpty(accountPath)) {
      accountPath = DEFAULT_ACCOUNT_PATH;
    }

    final String personalInformationCopy = i18nBundle.getString(PERSONAL_INFORMATION_I18N_KEY);
    final SubMenuCta personalInfoCta = SubMenuCta.builder()
          .icon("file")
          .copy(personalInformationCopy)
          .url(accountPath)
          .build();
    final SubMenu personalInfoSubMenu = SubMenu.builder()
        .cta(personalInfoCta)
        .build();

    final String myFavoritesCopy = i18nBundle.getString(MY_FAVORITES_I18N_KEY);
    final SubMenuCta favoritesCta = SubMenuCta.builder()
        .icon("heart-inactive")
        .copy(myFavoritesCopy)
        .url(myFavorites)
        .build();
    final SubMenu favoritesSubMenu = SubMenu.builder()
        .cta(favoritesCta)
        .build();

    final String myTripsCopy = i18nBundle.getString(MY_TRIPS_I18N_KEY);
    final SubMenuCta tripsCta = SubMenuCta.builder()
        .icon("trip")
        .copy(myTripsCopy)
        .url(myTrips)
        .build();
    final SubMenu tripsSubMenu = SubMenu.builder()
        .cta(tripsCta)
        .build();

    this.submenu =
        Arrays.asList(new SubMenu[] {personalInfoSubMenu, favoritesSubMenu, tripsSubMenu});
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
