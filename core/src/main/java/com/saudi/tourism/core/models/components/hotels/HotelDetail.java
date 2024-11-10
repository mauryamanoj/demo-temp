package com.saudi.tourism.core.models.components.hotels;

import com.day.cq.tagging.TagManager;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.components.events.AppFilterItem;
import com.saudi.tourism.core.models.components.IdValueModel;
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
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * The Class HotelDetail.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class HotelDetail implements Serializable {

  /**
   * The hotel name.
   */
  @Expose
  @ValueMapValue
  @Getter
  private String hotelName;

  /**
   * The city.
   */
  @ValueMapValue
  @Getter
  private String[] hotelCity;

  /**
   * The Chain.
   */
  @ValueMapValue
  @Getter
  private String[] chain;

  /**
   * CtaKey for native App.
   */
  @Expose
  @ValueMapValue
  @Getter
  private String ctaKey;


  /**
   * List for cities.
   */
  @Getter
  private List<String> hotelCities;

  /**
   * The area.
   *
   */
  @Setter
  @Getter
  @Expose
  private List<AppFilterItem> area;


  /**
   * The Hotel Chain.
   */
  @Setter
  @Getter
  @Expose
  private List<AppFilterItem> hotelChain;

  /**
   * List for hotel chains.
   */
  @Getter
  private List<String> hotelChains;

  /**
   * The hotel image.
   */
  @ValueMapValue
  @Expose
  @Getter
  private String hotelImage;

  /**
   * The hotel image alt text.
   */
  @ValueMapValue
  @Expose
  @Getter
  private String hotelImageAltText;

  /**
   * The CTA Text.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String ctaText;

  /**
   * The CTA URL.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String ctaUrl;

  /**
   * The Special Offers.
   */
  @ChildResource
  @Getter
  @Expose
  private HotelsSpecialOffers specialOffers;

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
   * List of event related paths for the servlet.
   */
  @Getter
  @Setter
  private List<String> relatedPackagesPaths;

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

        //Cities
        String[] cityTags = getHotelCity();
        hotelCities = CommonUtils
            .getCategoryFromTagName(cityTags, resourceResolver, Constants.DEFAULT_LOCALE);
        List<AppFilterItem> cityList = new ArrayList<>();
        for (String filterItem : hotelCity) {
          String filterId = CommonUtils.getTagName(filterItem, tagManager, localeEn).toLowerCase();
          String filterValue = CommonUtils.getTagName(filterItem, tagManager, locale);
          AppFilterItem cityItem = new AppFilterItem(filterId, filterValue);
          cityList.add(cityItem);
        }
        setArea(cityList);
        hotelCities = CommonUtils.getIds(hotelCities);
        String[] chainTags = getChain();
        hotelChains = CommonUtils
          .getCategoryFromTagName(chainTags, resourceResolver, Constants.DEFAULT_LOCALE);
        List<AppFilterItem> chainList = new ArrayList<>();
        if (null != chain) {
          for (String chainFilterItem : chain) {
            String chainFilterId = CommonUtils.getTagName(chainFilterItem, tagManager, localeEn).toLowerCase();
            String chainFilterValue = CommonUtils.getTagName(chainFilterItem, tagManager, locale);
            AppFilterItem cityItem = new AppFilterItem(chainFilterId, chainFilterValue);
            chainList.add(cityItem);
          }
        }
        setHotelChain(chainList);
        hotelChains = CommonUtils.getIds(hotelChains);
        // if offers details are 0 than offer cta will be blank, so fallback to normal cta text
        if (Objects.nonNull(specialOffers) && StringUtils.isBlank(specialOffers.getCtaText())) {
          specialOffers.setCtaText(getCtaText());
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
      LOGGER.error("Error in PackageDetail ", e);
    }
  }

}
