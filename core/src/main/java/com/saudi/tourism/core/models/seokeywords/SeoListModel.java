package com.saudi.tourism.core.models.seokeywords;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.Constants;
import lombok.Data;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The Class SeoListModel.
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class SeoListModel implements Serializable {

  /**
   * The SeoModel.
   */

  @ChildResource
  private List<SeoModel> seoKeys;

  /**
   * The SEO keywords.
   */
  @Expose
  private String seoKeywords;
  /**
   *
   */
  @Inject
  private SlingHttpServletRequest request;
  /**
   * acceptedLocales.
   */
  private List<Locale> acceptedLocales;
  /**
   * acceptedLocale.
   */
  private String primaryLocale;

  /**
   * post construct.
   */
  @PostConstruct
  void init() {
    final StringBuffer keys = new StringBuffer();
    if (null != seoKeys && !seoKeys.isEmpty()) {
      seoKeys.forEach(key -> {
        if (seoKeys.indexOf(key) + 1 < seoKeys.size()) {
          keys.append(key.getKeyword()).append(",");
        } else {
          keys.append(key.getKeyword());
        }
      });
    }
    seoKeywords = keys.toString();
    String acceptLanguageHeader = request.getHeader("Accept-Language");
    acceptedLocales = new ArrayList<>();
    if (acceptLanguageHeader != null) {
      String[] languages = acceptLanguageHeader.split(",");
      for (String language : languages) {
        String[] localeParts = language.trim().split(";")[0].split("-");
        Locale locale;
        if (localeParts.length == 2) {
          locale = new Locale(localeParts[0], localeParts[1]);
        } else {
          locale = new Locale(localeParts[0]);
        }
        acceptedLocales.add(locale);
      }
      primaryLocale = acceptedLocales.get(0).toString();
    } else {
      primaryLocale = Constants.DEFAULT_LOCALE;
    }
  }
}
