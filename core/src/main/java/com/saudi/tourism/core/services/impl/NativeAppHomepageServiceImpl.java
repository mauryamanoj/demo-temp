package com.saudi.tourism.core.services.impl;

import com.day.crx.JcrConstants;
import com.saudi.tourism.core.models.mobile.components.AboutGoLandingModel;
import com.saudi.tourism.core.models.mobile.components.GenericCardModel;
import com.saudi.tourism.core.models.mobile.components.MobileSearchResults;
import com.saudi.tourism.core.models.mobile.components.SspOnboardingModel;
import com.saudi.tourism.core.models.app.page.OnboardingModel;
import com.saudi.tourism.core.models.mobile.components.SuggestionsModel;
import com.saudi.tourism.core.models.mobile.components.TrendingModel;
import com.saudi.tourism.core.models.mobile.components.atoms.MediaGallery;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.models.nativeApp.page.NativeAppEVisaModel;
import com.saudi.tourism.core.services.NativeAppHomepageService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Optional;
import java.util.Collections;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * NativeAppHomepageServiceImpl .
 */
@Component(service = NativeAppHomepageService.class,
    immediate = true,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "NativeApp Homepage Service"})
@Slf4j
public class NativeAppHomepageServiceImpl implements NativeAppHomepageService {
  /**
   * ONBOARDING variable .
   */
  private static final String ONBOARDING = "nativeAppOnboarding";

  /**
   * ONBOARDING variable .
   */
  private static final String SSP_ONBOARDING = "sspOnboarding";

  /**
   * QR_CODE_TUTORIAL_CONFIG variable .
   */
  private static final String QR_CODE_TUTORIAL_CONFIG = "qrCodeTutorial";

  /**
   * ABOUT_GO_LANDING_CONFIG variable .
   */
  private static final String ABOUT_GO_LANDING_CONFIG = "mobileAboutGoLanding";

  /**
   * LOYALTY_ONBOARDING variable .
   */
  private static final String LOYALTY_ONBOARDING = "loyaltyOnboarding";

  /**
   * Evisa variable .
   */
  private static final String NATIVE_APP_E_VISA = "nativeAppEvisa";

  /** Saudi Tourism Config. */
  @Reference private SaudiTourismConfigs saudiTourismConfigs;

  /** User Service. */
  @Reference private UserService userService;

  /**
   * Localization bundle provider.
   */
  @Reference(target = Constants.I18N_PROVIDER_SERVICE_TARGET)
  private ResourceBundleProvider i18nProvider;


  @Override
   public OnboardingModel getNativeOnboarding(SlingHttpServletRequest request, String resourcePath) {

    Resource resource = request.getResourceResolver()
        .getResource(resourcePath + Constants.FORWARD_SLASH_CHARACTER + JcrConstants.JCR_CONTENT
        + Constants.FORWARD_SLASH_CHARACTER + ONBOARDING);

    OnboardingModel model = null;
    if (resource != null) {
      model = resource.adaptTo(OnboardingModel.class);
    }
    return model;
  }

  @Override
  public NativeAppEVisaModel getNativeAppEVisaBanner(SlingHttpServletRequest request, String resourcePath) {
    Resource resource = request.getResourceResolver()
        .getResource(resourcePath + Constants.FORWARD_SLASH_CHARACTER + JcrConstants.JCR_CONTENT
        + Constants.FORWARD_SLASH_CHARACTER + NATIVE_APP_E_VISA);

    NativeAppEVisaModel model = null;
    if (resource != null) {
      model = resource.adaptTo(NativeAppEVisaModel.class);
    }
    return model;

  }

  public SspOnboardingModel getSspOnboarding(SlingHttpServletRequest request, String resourcePath) {

    Resource resource = request.getResourceResolver()
        .getResource(resourcePath + Constants.SLASH_JCR_CONTENT
        + Constants.FORWARD_SLASH_CHARACTER + SSP_ONBOARDING);

    SspOnboardingModel model = null;
    if (resource != null) {
      model = resource.adaptTo(SspOnboardingModel.class);
    }
    return model;
  }

  public GenericCardModel getQrCodeTutorialConfig(SlingHttpServletRequest request, String resourcePath) {

    Resource resource = request.getResourceResolver()
        .getResource(resourcePath + Constants.SLASH_JCR_CONTENT
            + Constants.FORWARD_SLASH_CHARACTER + QR_CODE_TUTORIAL_CONFIG);

    GenericCardModel model = null;
    if (resource != null) {
      model = resource.adaptTo(GenericCardModel.class);
    }
    return model;
  }

  public AboutGoLandingModel getAboutGoLandingConfig(SlingHttpServletRequest request, String resourcePath) {

    Resource resource = request.getResourceResolver()
        .getResource(resourcePath + Constants.SLASH_JCR_CONTENT
            + Constants.FORWARD_SLASH_CHARACTER + ABOUT_GO_LANDING_CONFIG);

    AboutGoLandingModel model = null;
    if (resource != null) {
      model = resource.adaptTo(AboutGoLandingModel.class);
    }
    return model;
  }

  @Override
  public GenericCardModel getLoyaltyOnboarding(SlingHttpServletRequest request, String resourcePath) {

    Resource resource = request.getResourceResolver()
        .getResource(resourcePath + Constants.SLASH_JCR_CONTENT
            + Constants.FORWARD_SLASH_CHARACTER + LOYALTY_ONBOARDING);

    GenericCardModel model = null;
    if (resource != null) {
      model = resource.adaptTo(GenericCardModel.class);
    }
    return model;
  }

  @Override
  public List<String> getTrending(final String language) {
    final String trendingResourcePath =
        MessageFormat.format(saudiTourismConfigs.getTrendingResourcePath(), language);
    try (ResourceResolver resolver = userService.getResourceResolver()) {
      Resource resource = resolver.getResource(trendingResourcePath);
      if (Objects.isNull(resource)) {
        return null;
      }
      TrendingModel trending = resource.adaptTo(TrendingModel.class);
      if (Objects.isNull(trending) || CollectionUtils.isEmpty(trending.getTrending())) {
        return Collections.emptyList();
      }
      return trending.getTrending();
    }
  }


  @Override
  public List<MobileSearchResults> getSuggestions(String language) {
    final String suggestionsResourceType =
        MessageFormat.format(saudiTourismConfigs.getSuggestionsResourcePath(), language);

    List<MobileSearchResults> results = new ArrayList<>();

    try (ResourceResolver resolver = userService.getResourceResolver()) {
      Resource resource = resolver.getResource(suggestionsResourceType);
      if (Objects.isNull(resource)) {
        return null;
      }

      SuggestionsModel model = resource.adaptTo(SuggestionsModel.class);
      if (Objects.isNull(model)) {
        return null;
      }

      List<String> suggestions = model.getItemPaths();
      if (CollectionUtils.isNotEmpty(suggestions)) {
        for (String suggestion : suggestions) {
          Resource itemResourceBase = resolver.getResource(suggestion + Constants.SLASH_JCR_CONTENT);
          if (itemResourceBase != null) {
            Resource itemResource = itemResourceBase.getChild(Constants.ITEM);
            if (Objects.nonNull(itemResource)) {
              ItemResponseModel item = itemResource.adaptTo(ItemResponseModel.class);
              if (Objects.nonNull(item)) {
                List<MediaGallery> mediaGallery =
                    Optional.ofNullable(item.getMediaGallery())
                        .orElse(Collections.emptyList())
                        .stream()
                        .filter(Objects::nonNull)
                        .filter(media -> "IMAGE".equals(media.getType()) && StringUtils.isNotBlank(media.getUrl()))
                        .collect(Collectors.toList());

                String image = null;
                String title = null;

                if (CollectionUtils.isNotEmpty(mediaGallery)) {
                  image = mediaGallery.stream().map(MediaGallery::getUrl).findFirst().orElse(null);
                }

                if (Objects.nonNull(item.getTitles())) {
                  title = item.getTitles().getTitle();
                }

                MobileSearchResults.MediaGallery gallery =
                    MobileSearchResults.MediaGallery.builder()
                        .type(Constants.PN_IMAGE)
                        .url(image)
                        .build();
                String type = Constants.ITEM;
                String contentTypeTitle = getTranslation(item.getType(), language);

                if (StringUtils.isNotBlank(item.getType()) && AppUtils.isDeepLinkType(item.getType())) {
                  type = item.getType();
                  contentTypeTitle = StringUtils.EMPTY;
                }


                MobileSearchResults suggestionItem =
                    MobileSearchResults.builder()
                        .id(item.getId())
                        .title(title)
                        .mediaGallery(gallery)
                        .contentTypeTitle(contentTypeTitle)
                        .type(type)
                        .build();

                results.add(suggestionItem);
              }
            }
          }
        }
      }
    }

    return results;
  }


  /**
   * This method is used to get translation.
   *
   * @param key      translation key
   * @param language language
   * @return translated String
   */
  private String getTranslation(String key, String language) {
    if (!StringUtils.isEmpty(key)) {
      ResourceBundle i18n = i18nProvider.getResourceBundle(new Locale(language));
      return i18n.getString(key);
    }
    return Constants.BLANK;
  }
}
