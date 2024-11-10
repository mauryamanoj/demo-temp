package com.saudi.tourism.core.models.components.contentfragment.menu;

import java.util.Locale;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.services.RunModeService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.I18nConstants;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;


@Model(adaptables = Resource.class,
     defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class SearchBoxCFModel {

  /**
   * SaudiTourismConfigs.
   */
  @OSGiService
  private transient SaudiTourismConfigs saudiTourismConfigs;

  /**
   * RunMode Service.
   */
  @OSGiService
  @Getter(AccessLevel.NONE)
  private transient RunModeService runModeService;

  /**
   * The Current resource.
   */
  @Self
  private transient Resource currentResource;

  /**
   * Resource Resolver.
   */
  @JsonIgnore
  @Inject
  private transient ResourceResolver resourceResolver;

  /**
   * ResourceBundleProvider.
   */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private transient ResourceBundleProvider i18nProvider;

  /**
   * Suggestions Endpoint.
   */
  @Expose
  private String suggestionsEndpoint;

  /**
   * Search Page Path.
   */
  @Expose
  @ValueMapValue
  private String searchPagePath;

  /**
   * Search Modal Title.
   */
  @Expose
  @ValueMapValue
  private String searchModalTitle;

  /**
   * Search Placeholder.
   */
  @Expose
  @ValueMapValue
  private String searchPlaceholder;

  /**
   * Cancel Label.
   */
  @Expose
  private String cancelLabel;

  /**
   * Clear Label.
   */
  @Expose
  private String clearLabel;

  /**
   * Clear History Label.
   */
  @Expose
  @ValueMapValue
  private String clearHistoryLabel;

  /**
   * Search Label.
   */
  @Expose
  @ValueMapValue
  private String searchLabel;

  /**
   * Recent Search Label.
   */
  @Expose
  @ValueMapValue
  private String recentSearchLabel;


  @PostConstruct
  public void init() {
    suggestionsEndpoint = saudiTourismConfigs.getSearchApiEndpoint();
    searchPagePath = LinkUtils.getAuthorPublishUrl(resourceResolver, searchPagePath,
      runModeService.isPublishRunMode());
    final var language = CommonUtils.getLanguageForPath(currentResource.getPath());
    if (Objects.nonNull(i18nProvider)) {
      final var i18n = i18nProvider.getResourceBundle(new Locale(language));
      cancelLabel = CommonUtils.getI18nString(i18n, I18nConstants.CANCEL);
      clearLabel = CommonUtils.getI18nString(i18n, I18nConstants.SEARCH_CLEAR);
    }
  }
}
