package com.saudi.tourism.core.models.components.search;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.TagManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.DictItem;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.SearchUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.saudi.tourism.core.utils.Constants.PN_IMAGE;

/**
 * The Search trending page model.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class TrendingCard implements Serializable {
  /**
   * Page title. jcr:title from page properties.
   */
  @ValueMapValue(name = JcrConstants.JCR_TITLE)
  @Expose
  private String title;

  /**
   * Variable to store search category.
   */
  @ValueMapValue
  @Expose
  private String category;
  /**
   * Variable to store search category key.
   */
  private String categoryKey;
  /**
   * Variable to store page type for the app.
   */
  private String appType;

  /**
   * Variable to store link.
   */
  @ValueMapValue
  @Expose
  private String link;

  /**
   * Variable to store image.
   */
  @ChildResource
  @Expose
  private Image image;

  /**
   * The current resource.
   */
  @JsonIgnore
  @Self(injectionStrategy = InjectionStrategy.REQUIRED)
  private transient Resource resource;

  /**
   * The User service.
   */
  @JsonIgnore
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private transient UserService userService;

  /**
   * The init method.
   */
  @PostConstruct protected void init() {
    String path =
        Optional.ofNullable(resource.getParent()).map(Resource::getPath).orElse(StringUtils.EMPTY);
    String language = CommonUtils.getLanguageForPath(path);
    link = path.replace(Constants.ROOT_CONTENT_PATH, Constants.BLANK);

    ValueMap properties = resource.adaptTo(ValueMap.class);
    DictItem tagCategory =
        SearchUtils.getSearchCategoryTagTitleExcludeHighlight(resource.getResourceResolver(), language,
            properties.get("searchCategories", String[].class));

    if (tagCategory != null) {
      categoryKey = tagCategory.getCode();
    } else {
      categoryKey = AppUtils.getDefaultSearchCategory(resource.getResourceType());
    }

    List<DictItem> types;

    try (ResourceResolver resolver = userService.getResourceResolver()) {
      types =
          SearchUtils.getSearchCategoriesFromTags(resolver, language);
    }

    category = types.stream()
        .filter(d -> d.getCode().equalsIgnoreCase(categoryKey))
        .map(DictItem::getValue).findAny().orElse("Page");

    appType = AppUtils.getPageType(properties);
    image = new Image();
    image.setDesktopImage(
        properties.get(Constants.FEATURE_IMAGE, Constants.DEFAULT_IMAGE_PLACEHOLDER));
    image.setMobileImage(properties.get(Constants.MOBILE_NAV_IMAGE, image.getDesktopImage()));
    //workaround for native app featured image
    if (image.getMobileImage().equals(Constants.DEFAULT_IMAGE_PLACEHOLDER)) {
      image.setMobileImage(properties.get(Constants.FEATURED_IMAGE, Constants.DEFAULT_IMAGE_PLACEHOLDER));
    }
    image.setAlt(PN_IMAGE);
  }

  /**
   * This method is used to get title of the tag.
   *
   * @param language         locale
   * @param resourceResolver ResourceResolver
   * @param tags             tags array
   * @return tag title
   */
  private String getSearchCategoryTagTitle(ResourceResolver resourceResolver, String language,
      String[] tags) {
    if (tags != null) {
      Locale locale = new Locale(language);
      TagManager tagManager = resourceResolver.adaptTo(TagManager.class);

      for (String tag : tags) {
        if (!tag.contains("Highlight")) {
          return CommonUtils.getTagName(tag, tagManager, locale);
        }
      }
    }
    return "Page";
  }
}
