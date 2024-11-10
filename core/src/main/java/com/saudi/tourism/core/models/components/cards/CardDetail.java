package com.saudi.tourism.core.models.components.cards;

import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.tagging.TagManager;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.CardImage;
import com.saudi.tourism.core.models.components.ArticleDetail;
import com.saudi.tourism.core.models.components.IdValueModel;
import com.saudi.tourism.core.models.components.events.AppFilterItem;
import com.saudi.tourism.core.services.SaudiModeConfig;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;
import com.saudi.tourism.core.utils.SpecialCharConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * The Class CardDetail.
 */

@Model(adaptables = Resource.class,
    resourceType = Constants.CARDS_RES_TYPE,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(
    selector = "search",
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION,
    options = {@ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS", value = "true")})
@Slf4j
public class CardDetail implements Serializable {

  /**
   * The name.
   */
  @Expose
  @ValueMapValue
  @Getter
  private String name;

  /**
   * The ctaKey.
   */
  @Expose
  @ValueMapValue
  @Getter
  private String ctaKey;
  /**
   * The type.
   */
  @ValueMapValue
  @Getter
  private String[] cardCategory;

  /**
   * The city.
   */
  @ValueMapValue
  @Getter
  private String[] cardCity;

  /**
   * List for card types.
   */
  @Getter
  private List<String> cardCategories;

  /**
   * The cities.
   */
  @Getter
  private List<String> cardCities;

  /**
   * The type.
   */
  @Setter
  @Getter
  @Expose
  private List<AppFilterItem> category;

  /**
   * The city.
   */
  @Setter
  @Getter
  @Expose
  private List<AppFilterItem> city;

  /**
   * The description.
   */
  @Expose
  @ValueMapValue
  @Getter
  private String description;

  /**
   * The priceRange.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String priceRange;

  /**
   * The image.
   */
  @Getter
  @Expose
  @ChildResource
  private CardImage image;

  /**
   * The id.
   */
  @Getter
  @Expose
  private String id;

  /**
   * The path.
   */
  @Getter
  @Expose
  private String path;

  /**
   * The CTA Text.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String linkText;

  /**
   * The CTA Type.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String linkType;


  /**
   * The CTA URL.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String linkUrl;

  /**
   * The more details header title.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String detailsHeaderTitle;

  /**
   * The more details Title.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String detailsTitle;

  /**
   * The more details Link.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String detailsLink;

  /**
   * The more details type.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String detailsType;

  /**
   * The more details.
   */
  @ChildResource
  @Getter
  @Expose
  private List<ArticleDetail> details;

  /**
   * The options.
   */
  @Getter
  @Setter
  @Expose
  private Object options;


  /**
   * ResourceBundleProvider.
   */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private transient ResourceBundleProvider i18nProvider;

  /**
   * ResourceResolver.
   */
  @Self
  private transient Resource currentResource;

  /**
   * Inject SlingSettingsService.
   */
  @Inject
  private transient SaudiModeConfig saudiModeConfig;

  /**
   * ResourceResolver.
   */
  @SlingObject
  private transient ResourceResolver resourceResolver;

  /**
   * The Article Map.
   */
  @ChildResource
  @Getter
  private List<IdValueModel> articles;

  /**
   * The Article Ids.
   */
  @Getter
  @Setter
  private List<String> articleIds;

  /**
   * The init method.
   */
  @PostConstruct protected void init() {
    path = Optional.ofNullable(currentResource.getParent()).map(Resource::getPath)
      .orElse(StringUtils.EMPTY);
    try {
      TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
      String language = CommonUtils.getLanguageForPath(path);
      if (Objects.nonNull(saudiModeConfig)) {
        if (saudiModeConfig.getPublish() != null) {
          path = LinkUtils.transformUrl(path, true);
        } else {
          path = LinkUtils.transformUrl(path, false);
        }

        // Create id (with a workaround for home (/en) pages)
        id = AppUtils.stringToID(StringUtils.defaultIfBlank(StringUtils.substringAfter(path,
            Constants.FORWARD_SLASH_CHARACTER + language + Constants.FORWARD_SLASH_CHARACTER),
          SpecialCharConstants.FORWARD_SLASH));
      }
      if (Objects.nonNull(i18nProvider) && Objects.nonNull(tagManager)) {
        // update city to i18n value
        Locale localeEn = new Locale((Constants.DEFAULT_LOCALE));
        Locale locale = new Locale(language);

        //types
        if (null != cardCategory) {
          setCategory(getAppFilterItems(tagManager, localeEn, locale, cardCategory));
          cardCategories = CommonUtils.getIds(CommonUtils
            .getCategoryFromTagName(cardCategory, resourceResolver, Constants.DEFAULT_LOCALE));
        }

        //cities
        if (null != cardCity) {
          setCity(getAppFilterItems(tagManager, localeEn, locale, cardCity));
          cardCities = CommonUtils.getIds(CommonUtils
            .getCategoryFromTagName(cardCity, resourceResolver, Constants.DEFAULT_LOCALE));
        }
      }

      if (null != articles) {
        List<String> ids = new ArrayList<>();
        articles.forEach(article -> {
          if ((null != article.getId()) && (article.getId().split("/" + language + "/").length > 1)) {
            ids.add(AppUtils.stringToID(article.getId().split("/" + language + "/")[1]));
          }
        });
        setArticleIds(ids);
      }
    } catch (Exception e) {
      LOGGER.error("Error in CardDetail ", e);
    }
  }

  /**
   *
   * @param tagManager tag manager.
   * @param localeEn en locale.
   * @param locale locale.
   * @param filter filter.
   * @return filter list.
   */
  @NotNull
  private List<AppFilterItem> getAppFilterItems(TagManager tagManager, Locale localeEn, Locale locale,
                                                String[] filter) {
    List<AppFilterItem> typeList = new ArrayList<>();
    for (String filterItem : filter) {
      String filterId = CommonUtils.getTagName(filterItem, tagManager, localeEn).toLowerCase();
      String filterValue = CommonUtils.getTagName(filterItem, tagManager, locale);
      AppFilterItem typeItem = new AppFilterItem(filterId, filterValue);
      typeList.add(typeItem);
    }
    return typeList;
  }

}
