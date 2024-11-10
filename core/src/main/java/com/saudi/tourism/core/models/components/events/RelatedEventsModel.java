package com.saudi.tourism.core.models.components.events;

import com.day.cq.commons.Externalizer;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.crx.JcrConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_RELATED_EVENTS;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_VIEW_ALL;

/**
 * Related Events.
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@Getter
public class RelatedEventsModel {

  /**
   * Card Type 'calendar'.
   */
  private static final String CARD_TYPE_CALENDAR = "calendar";

  /**
   * Card Style 'related-events'.
   */
  private static final String CARD_STYLE_RELATED_EVENTS = "related-events";

  /**
   * Path Separator.
   */
  private static final String PATH_SEPARATOR = "/";

  /**
   * The Sling settings service.
   */
  @OSGiService
  private transient SlingSettingsService settingsService;

  /**
   * Variable for current resource.
   */
  @ScriptVariable
  private transient Page currentPage;

  /**
   * Resource Bundle Provider.
   */
  @OSGiService(filter = Constants.I18N_PROVIDER_SERVICE_TARGET)
  private transient ResourceBundleProvider i18nProvider;

  /**
   * SaudiTourismConfigs.
   */
  @OSGiService
  private transient SaudiTourismConfigs saudiTourismConfigs;

  /**
   * Current Resource.
   */
  @Inject
  private transient Resource resource;

  /**
   * Events Filter Page Path.
   */
  private String filtersPagePath = StringUtils.EMPTY;

  /**
   * View All Text.
   */
  private String viewAllText;

  /**
   * Category Tags.
   */
  private String[] categoryTags;

  /**
   * Categories.
   */
  @Expose
  private List<String> categories;

  /**
   * Headline.
   */
  @Expose
  private String headline;

  /**
   * Path.
   */
  private String path;

  /**
   * Card type.
   */
  @Getter
  @Expose
  private String cardType = CARD_TYPE_CALENDAR;

  /**
   * Card Style.
   */
  @Getter
  @Expose
  private String cardStyle = CARD_STYLE_RELATED_EVENTS;

  /**
   * Events api url.
   */
  @Expose
  private String apiUrl;

  /**
   * View all link.
   */
  @Expose
  private Link link;

  /**
   * init method of sling model.
   */
  @PostConstruct
  protected void init() {

    ResourceResolver resolver = resource.getResourceResolver();
    PageManager pageManager = resolver.adaptTo(PageManager.class);

    InheritanceValueMap ivm = new HierarchyNodeInheritanceValueMap(currentPage.getContentResource());
    String locale = ivm.getInherited(JcrConstants.JCR_LANGUAGE, Constants.DEFAULT_LOCALE);

    if (StringUtils.isNotEmpty(saudiTourismConfigs.getEventsFilterPagePath())) {
      filtersPagePath = LinkUtils.getAuthorPublishUrl(
              resolver,
              Constants.ROOT_CONTENT_PATH
                  + PATH_SEPARATOR
                  + locale
                  + saudiTourismConfigs.getEventsFilterPagePath(),
              settingsService.getRunModes().contains(Externalizer.PUBLISH));
    }

    ResourceBundle i18n = i18nProvider.getResourceBundle(new Locale(locale));
    viewAllText = i18n.getString(I18_KEY_VIEW_ALL);
    headline = i18n.getString(I18_KEY_RELATED_EVENTS);

    link = new Link(filtersPagePath, viewAllText, false);
    apiUrl = saudiTourismConfigs.getEventsApiEndpoint();

    if (pageManager != null) {
      this.path = Optional.ofNullable(pageManager.getContainingPage(resource))
              .map(Page::getPath).orElse(StringUtils.EMPTY);

      if (ArrayUtils.isEmpty(this.categoryTags)) {
        Page page = pageManager.getContainingPage(resource);
        if (Objects.nonNull(page)) {
          Resource jcrContent = page.getContentResource();
          if (Objects.nonNull(jcrContent)) {
            this.categoryTags =  jcrContent.getValueMap().get("categoryTags", String[].class);
          }
        }
      }

      if (ArrayUtils.isNotEmpty(this.categoryTags)) {
        TagManager tagManager = resolver.adaptTo(TagManager.class);
        categories = Arrays.stream(categoryTags)
                .map(
                    c -> {
                      Tag tag = tagManager.resolve(c);
                      if (tag != null && tag.getTitle() != null) {
                        return AppUtils.stringToID(tag.getTitle());
                      }
                      return null;
                    })
                .filter(c -> c != null)
                .collect(Collectors.toList());
      }
    }
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
