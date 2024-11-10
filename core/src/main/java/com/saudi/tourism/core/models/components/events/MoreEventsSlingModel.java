package com.saudi.tourism.core.models.components.events;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.saudi.tourism.core.servlets.EventsServlet.SERVLET_PATH;
import static com.saudi.tourism.core.utils.Constants.API_V2_BASE_PATH;

/**
 * The Class EventsYouMayLikeSlingModel.
 */
@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class MoreEventsSlingModel implements Serializable {

  /**
   * Default value of limit field.
   */
  private static final int DEFAULT_LIMIT = 6;
  /**
   * The title.
   */
  @ValueMapValue
  private String title;
  /**
   * The endpoint.
   */
  private String endpoint;
  /**
   * The favoriteApiBasePath.
   */
  private String favoriteApiBasePath = API_V2_BASE_PATH;

  /**
   * The multipleLocationsCopy.
   */
  @ValueMapValue
  private String multipleLocationsCopy;
  /**
   * The eventDetailsLinkLabel.
   */
  @ValueMapValue
  private String eventDetailsLinkLabel;
  /**
   * The eventDetailsLinkLabel.
   */
  @ValueMapValue
  private String[] categories;
  /**
   * The limit.
   */
  @ValueMapValue
  private Integer limit;
  /**
   * The path.
   */
  private String path;
  /**
   * currentResource.
   */
  @JsonIgnore
  @Self
  private transient Resource currentResource;
  /**
   * ResourceBundleProvider.
   */
  @JsonIgnore
  @OSGiService(filter = Constants.I18N_PROVIDER_SERVICE_TARGET)
  private transient ResourceBundleProvider i18nProvider;

  /**
   * getJson method for account component.
   *
   * @return json representation.
   */
  @JsonIgnore
  public String getJson() {
    return new Gson().toJson(this);
  }
  /**
   * The init method.
   */
  @PostConstruct protected void init() {
    ResourceResolver resolver = currentResource.getResourceResolver();
    PageManager pageManager = resolver.adaptTo(PageManager.class);

    this.path = Optional.ofNullable(pageManager.getContainingPage(currentResource))
        .map(Page::getPath).orElse(StringUtils.EMPTY);
    String language =
        CommonUtils.getPageNameByIndex(path, Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION);
    endpoint = SERVLET_PATH + "?locale=" + language;
    if (Objects.isNull(this.limit)) {
      this.limit = DEFAULT_LIMIT;
    }

    ResourceBundle i18n = i18nProvider.getResourceBundle(new Locale(language));
    this.title = getValueFromBundleIfBlank(this.title, "more-events-like-this", i18n);
    this.multipleLocationsCopy = getValueFromBundleIfBlank(this.multipleLocationsCopy,
        "multipledestinations", i18n);
    this.eventDetailsLinkLabel = getValueFromBundleIfBlank(this.eventDetailsLinkLabel,
        "learnmore", i18n);

    if (Objects.isNull(this.categories)) {
      Page page = pageManager.getContainingPage(currentResource);
      if (Objects.nonNull(page)) {
        Resource jcrContent = page.getContentResource();
        if (Objects.nonNull(jcrContent)) {
          this.categories = jcrContent.getValueMap().get("categoryTags", String[].class);
        }
      }
    }
    if (Objects.nonNull(this.categories)) {
      TagManager tagManager = resolver.adaptTo(TagManager.class);
      for (int i = 0; i < this.categories.length; i++) {
        Tag tag = tagManager.resolve(this.categories[i]);
        if (tag != null && tag.getTitle() != null) {
          this.categories[i] = AppUtils.stringToID(tag.getTitle());
        }
      }
    }
  }

  /**
   * Retrive value from i18n bundle if current value is blank.
   * @param currVal currVal
   * @param key key
   * @param i18n i18n
   * @return value
   */
  private String getValueFromBundleIfBlank(String currVal, String key, ResourceBundle i18n) {
    if (StringUtils.isBlank(currVal)) {
      return CommonUtils.getI18nString(i18n, key);
    }
    return currVal;
  }
}
