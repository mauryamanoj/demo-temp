package com.saudi.tourism.core.models.components.nav.v2;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.login.services.SaudiSSIDConfig;
import com.saudi.tourism.core.models.common.LanguageLink;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.components.search.SearchConfigModel;
import com.saudi.tourism.core.services.SaudiModeConfig;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.I18nConstants;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.saudi.tourism.core.utils.Constants.API_V2_BASE_PATH;

/**
 * Define all properties of Navigation Header.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class NavigationHeader {

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
   * The Navigation tabs.
   */
  @Setter
  @Expose
  private List<NavigationTab> navigationTabs = new ArrayList<>();

  /**
   * The Middle part.
   */
  @Setter
  @Expose
  private List<LanguageLink> languages = new ArrayList<>();

  /**
   * The Middle part.
   */
  @Setter
  @ChildResource
  @Expose
  private Link evisa;

  /**
   * The map.
   */
  @Setter
  @ChildResource
  @Expose
  private Link map;

  /**
   * The current language.
   */
  @Setter
  @ChildResource
  @Expose
  private Link currentLanguage;

  /**
   * The phoneNumers.
   */
  @Setter
  @ChildResource
  @Expose
  private List<Link> phoneNumbers;

  /**
   * FAQ link.
   */
  @ChildResource
  @Expose
  private Link faq;

  /**
   * FAQ link.
   */
  @ChildResource
  @Expose
  private Link whatsapp;

  /**
   * The contacts.
   */
  @Setter
  @ChildResource(name = "contacts")
  @Expose
  private List<Link> secondaryLinks;

  /**
   * The Search text.
   */
  @ValueMapValue
  @Expose
  private String searchText;

  /**
   * The tool tip title.
   */
  @ValueMapValue
  @Expose
  private String toolTipTitle;

  /**
   * The tool tip description.
   */
  @ValueMapValue
  @Expose
  private String toolTipDescription;

  /**
   * The tool tip new text.
   */
  @ValueMapValue
  @Expose
  private String toolTipNewText;

  /**
   * The map region id.
   */
  @ValueMapValue
  @Expose
  private String regionId;

  /**
   * hide login button checkbox.
   */
  @ValueMapValue
  private String hideLogin;

  /**
   * hide search icon checkbox.
   */
  @ValueMapValue
  private String hideSearch;

  /**
   * hide language selector.
   */
  @ValueMapValue
  private String hideLangSelector;

  /**
   * The map city id.
   */
  @ValueMapValue
  @Expose
  private String cityId;

  /**
   * The map location type.
   */
  @ValueMapValue
  @Expose
  private String locationType;

  /**
   * The map link.
   */
  @ValueMapValue
  @Expose
  private String mapLink;

  /**
   * The Search text.
   */
  @ValueMapValue
  @Expose
  private String logoPath;

  /**
   * The Search Path.
   */
  @ValueMapValue
  @Expose
  private String searchPath;

  /**
   * search Placeholder.
   */
  @Expose
  private String searchPlaceholder;
  /**
   * search cancel Label.
   */
  @Expose
  private String cancelLabel;
  /**
   * search clear Label.
   */
  @Expose
  private String clearLabel;
  /**
   * search pill block title.
   */
  @Expose
  private String pillBlockTitle;
  /**
   * search trending block title.
   */
  @Expose
  private String trendingBlockTitle;

  /**
   * search trending featured title.
   */
  @Expose
  private String featuredTitle;

  /**
   * Search Config Model.
   */
  @Setter
  @ChildResource
  @Expose
  private SearchConfigModel searchConfigModel;

  /**
   * The Auth0 domain.
   */
  @Expose
  private String domain;

  /**
   * The Auth0 clientId.
   */
  @Expose
  private String clientId;

  /**
   * The Favorites Results Page Url.
   */
  @ValueMapValue
  @Expose
  private String favoritesResultsPageUrl;

  /**
   * The accountPath.
   */
  @ValueMapValue
  @Expose
  private String accountPath;

  /**
   * The myFavorites.
   */
  @ValueMapValue
  @Expose
  private String myFavorites;

  /**
   * The myTrips.
   */
  @ValueMapValue
  @Expose
  private String myTrips;

  /**
   * Menu label.
   */
  @ValueMapValue
  @Expose
  private String menuLabel;

  /**
   * CTA Login Text.
   */
  @Expose
  @Getter
  private String ctaLoginText;

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
   * isSearchPage.
   */
  @Setter
  @Expose
  private boolean isSearchPage = false;

  /**
   * Inject SaudiModeConfig.
   */
  @Inject
  private SaudiModeConfig saudiModeConfig;

  /**
   * Inject SaudiModeConfig.
   */
  @OSGiService
  private SaudiSSIDConfig saudiLoginConfig;

  /**
   * Variable for current resource.
   */
  @Self
  private Resource currentResource;

  /**
   * ResourceBundleProvider.
   */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private ResourceBundleProvider i18nProvider;

  /**
   * The Model Initializer.
   */
  @PostConstruct private void init() {
    String path = Optional.ofNullable(currentResource.getPath()).orElse(StringUtils.EMPTY);
    String language =
        CommonUtils.getPageNameByIndex(path, Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION);
    ResourceResolver resolver = currentResource.getResourceResolver();
    if (Objects.nonNull(searchPath)) {
      searchPath = LinkUtils.getAuthorPublishUrl(resolver, searchPath, saudiModeConfig.getPublish());
    }
    if (Objects.nonNull(logoPath)) {
      logoPath = LinkUtils.getUrlWithExtension(logoPath);
    }
    if (Objects.nonNull(favoritesResultsPageUrl)) {
      favoritesResultsPageUrl = LinkUtils.getUrlWithExtension(favoritesResultsPageUrl);
    }
    if (Objects.nonNull(accountPath)) {
      accountPath = LinkUtils.getAuthorPublishUrl(resolver, accountPath, saudiModeConfig.getPublish());
    }
    if (Objects.nonNull(myFavorites)) {
      myFavorites = LinkUtils.getAuthorPublishUrl(resolver, myFavorites, saudiModeConfig.getPublish());
    }
    if (Objects.nonNull(myTrips)) {
      myTrips = LinkUtils.getAuthorPublishUrl(resolver, myTrips, saudiModeConfig.getPublish());
    }
    domain = saudiLoginConfig.getSSIDDomain();
    clientId = saudiLoginConfig.getClientId();
    if (Objects.nonNull(i18nProvider)) {
      ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(language));
      searchPlaceholder = CommonUtils.getI18nString(i18nBundle, I18nConstants.SEARCH_PLACEHOLDER);
      clearLabel = CommonUtils.getI18nString(i18nBundle, "searchClear");
      cancelLabel = CommonUtils.getI18nString(i18nBundle, I18nConstants.CANCEL);
      pillBlockTitle = CommonUtils.getI18nString(i18nBundle, I18nConstants.SEARCH_PILL_BLOCK_TITLE);
      trendingBlockTitle =
          CommonUtils.getI18nString(i18nBundle, I18nConstants.SEARCH_TRENDING_BLOCK_TITLE);
      featuredTitle = CommonUtils.getI18nString(i18nBundle, I18nConstants.SEARCH_FEATURED_TITLE);
      this.apiError = new ApiError(i18nBundle);
    }
    if (Objects.nonNull(whatsapp) && StringUtils.isNotBlank(whatsapp.getUrl())) {
      whatsapp.setUrlWithExtension("https://wa.me/" + whatsapp.getUrl());
    }

    this.avatar = new Avatar();

    this.favoriteApiPath = API_V2_BASE_PATH;

    this.userApi = new UserApi(domain, clientId);

    final ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(language));
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

}
