package com.saudi.tourism.core.services;

import com.saudi.tourism.core.models.mobile.components.AboutGoLandingModel;
import com.saudi.tourism.core.models.mobile.components.GenericCardModel;
import com.saudi.tourism.core.models.mobile.components.MobileSearchResults;
import com.saudi.tourism.core.models.mobile.components.SspOnboardingModel;
import com.saudi.tourism.core.models.app.page.OnboardingModel;
import com.saudi.tourism.core.models.nativeApp.page.NativeAppEVisaModel;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.List;

/**
 * NativeAppHomepageService.
 */
public interface NativeAppHomepageService {

  /**
   * Get the Onboarding model.
   *
   * @param request      request
   * @param resourcePath resourcePath code
   * @return model
   */
  OnboardingModel getNativeOnboarding(SlingHttpServletRequest request, String resourcePath);

  /**
   * Get the NativeAppEVisa model.
   *
   * @param request      request
   * @param resourcePath resourcePath code
   * @return model
   */
  NativeAppEVisaModel getNativeAppEVisaBanner(SlingHttpServletRequest request, String resourcePath);

  /**
   * Get the Onboarding model.
   *
   * @param request      request
   * @param resourcePath resourcePath code
   * @return model
   */
  SspOnboardingModel getSspOnboarding(SlingHttpServletRequest request, String resourcePath);


  /**
   * Get the QR code tutorial config model.
   *
   * @param request      request
   * @param resourcePath resourcePath code
   * @return model
   */
  GenericCardModel getQrCodeTutorialConfig(SlingHttpServletRequest request, String resourcePath);

  /**
   * Get the About GO Landing config model.
   *
   * @param request      request
   * @param resourcePath resourcePath code
   * @return model
   */
  AboutGoLandingModel getAboutGoLandingConfig(SlingHttpServletRequest request, String resourcePath);

  /**
   * Get the Loyalty Onboarding model.
   *
   * @param request      request
   * @param resourcePath resourcePath code
   * @return model
   */
  GenericCardModel getLoyaltyOnboarding(SlingHttpServletRequest request, String resourcePath);

  /**
   * Get trending configured in mobile home page.
   * @param language
   * @return trending.
   */
  List<String> getTrending(String language);


  /**
   * Get suggestions configured in mobile home page.
   * @param language
   * @return trending.
   */
  List<MobileSearchResults> getSuggestions(String language);
}
