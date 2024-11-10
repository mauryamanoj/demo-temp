package com.saudi.tourism.core.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.saudi.tourism.core.models.app.i18n.AppTradPageModel;
import com.saudi.tourism.core.services.AppTranslationService;
import com.saudi.tourism.core.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Map;

import static com.saudi.tourism.core.utils.Constants.ROOT_APP_CONTENT_PATH;

/**
 * Service for App Translations.
 */
@Slf4j
@Component(service = AppTranslationService.class, immediate = true)
public class AppTranslationServiceImpl implements AppTranslationService {

  /**
   * The User service.
   */
  @Reference
  private transient UserService userService;
  /**
   * Target Android.
   */
  static final String TARGET_ANDROID = "android";
  /**
   * Target IOS.
   */
  static final String TARGET_IOS = "ios";
  /**
   * Target Parameter.
   */
  static final String PARAM_TARGET = "target";

  /**
   * Original variable.
   */
  static final String ORIGNAL_VARIABLE = "{{x}}";

  /**
   * Original pattern for variables.
   */
  static final String ORIGNAL_PATTERN = "\\{\\{([0-9])\\}\\}";

  /**
   * IOS variable.
   */
  static final String IOS_PATTERN = "%@";

  /**
   * ANDROID PATTERN.
   */
  static final String ANDROID_PATTERN = "%$1\\$s";


  @Override
   public  Map<String, String> loopAllI18keys(final ResourceResolver resolver, final String target,
                              final String locale) {

    String path = String.format(ROOT_APP_CONTENT_PATH + "/%s/i18n", locale);
    Resource res = resolver.getResource(path);
    Map<String, String> allKeyValues = null;
    if (null != res) {
      AppTradPageModel appTradPage = res.getChild(JcrConstants.JCR_CONTENT).adaptTo(AppTradPageModel.class);
      if (appTradPage != null) {
        allKeyValues = appTradPage.getMap(target);
      }

    }

    return allKeyValues;
  }
}
