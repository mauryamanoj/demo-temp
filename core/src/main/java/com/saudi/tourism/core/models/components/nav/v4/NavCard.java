package com.saudi.tourism.core.models.components.nav.v4;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.DictItem;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.common.RegionCity;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Model class of navigation cards.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class NavCard {

  /**
   * Type.
   */
  @ValueMapValue
  @Expose
  private String type;

  /**
   * Title.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * Subtitle.
   */
  @ValueMapValue
  @Expose
  private String subtitle;

  /**
   * Copy.
   */
  @ValueMapValue
  @Expose
  private String copy;

  /**
   * Icon.
   */
  @ValueMapValue
  @Expose
  private String icon;

  /**
   * Widget title.
   */
  @ValueMapValue
  @Expose
  private String widgetTitle;

  /**
   * Widget icon.
   */
  @ValueMapValue
  @Expose
  private String widgetIcon;

  /**
   * Widget icon.
   */
  @ValueMapValue
  @Expose
  private String riyalLabel;

  /**
   * Widget default.
   */
  @Expose
  private String widgetDefault;

  /**
   * CTA link.
   */
  @Expose
  @ChildResource(injectionStrategy = InjectionStrategy.OPTIONAL)
  private Link cta;

  /**
   * List of Number cards.
   */
  @ChildResource(injectionStrategy = InjectionStrategy.OPTIONAL)
  @Expose
  private List<NumberCard> numberCards;

  /**
   * The currencies.
   */
  @ChildResource
  @Expose
  private List<DictItem> currencies;

  /**
   * City codes.
   */
  @ValueMapValue
  @Expose
  private String[] cityCodes;

  /**
   * The cities available for weather.
   */
  @Expose
  private List<DictItem> cities = new LinkedList<>();

  /**
   * Variable of image.
   */
  @ChildResource
  @Expose
  private Image cardImage = new Image();

  /**
   * The Current resource.
   */
  @Self
  private transient Resource currentResource;

  /**
   * The regionCityService.
   */
  @OSGiService
  private RegionCityService regionCityService;

  /**
   * ResourceBundleProvider.
   */
  @OSGiService(filter = Constants.I18N_PROVIDER_SERVICE_TARGET)
  private transient ResourceBundleProvider i18nProvider;

  /**
   * This model post construct initialization.
   */
  @PostConstruct public void init() {
    if ("weather".equals(type)) {
      widgetIcon = "weather-clouds";
      initWeather();
    } else if ("currencies".equals(type)) {
      widgetIcon = "currency-circle";
      if (Objects.nonNull(currencies) && currencies.size() > 0) {
        widgetDefault = currencies.get(0).getCode();
      }
    } else if ("numbers".equals(type)) {
      widgetIcon = "phone";
    }
    boolean isFeatured = "featured".equals(type);
    boolean isArticle = "nav-article-card".equals(type);
    String ctaInternalUrl = cta.getInternalUrl();
    if ((isArticle || isFeatured)
        && Objects.nonNull(cta) && StringUtils.isNotBlank(ctaInternalUrl)) {
      ResourceResolver resolver = currentResource.getResourceResolver();
      PageManager pageManager = resolver.adaptTo(PageManager.class);
      String language =
          CommonUtils.getPageNameByIndex(ctaInternalUrl, Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION);
      ResourceBundle i18n = i18nProvider.getResourceBundle(new Locale(language));
      cta.setCopy(CommonUtils.getI18nString(i18n, "learnmore"));
      Page page = pageManager.getPage(ctaInternalUrl);
      if (Objects.nonNull(page)) {
        Resource jcrContent = page.getContentResource();
        Resource hero = getHeroComponent(jcrContent);
        initTitleFields(jcrContent, hero, isArticle, isFeatured, language);
        if (isFeatured) {
          initCardImage(hero, jcrContent);
        }
      }
    }
  }

  /**
   * Init weather fields.
   */
  private void initWeather() {
    if (Objects.nonNull(cityCodes)) {
      final String locale = CommonUtils.getLanguageForPath(currentResource.getPath());
      for (String cityId : cityCodes) {
        final RegionCity city = regionCityService.getRegionCityById(cityId, locale);
        if (city != null) {
          cities.add(new DictItem(cityId, city.getName()));
        }
      }
      if (cityCodes.length > 0) {
        widgetDefault = cityCodes[0];
      }
    }
  }

  /**
   * Init fields.
   * @param jcrContent - jcr:content resource
   * @param hero - hero resource
   * @param isArticle - is article
   * @param isFeatured - is featured
   * @param lang - language
   */
  private void initTitleFields(Resource jcrContent, Resource hero, final boolean isArticle,
      final boolean isFeatured, String lang) {
    ValueMap heroVM = Optional.ofNullable(hero)
        .map(h -> h.adaptTo(ValueMap.class)).orElse(null);
    ValueMap jcrVM = Optional.ofNullable(jcrContent)
        .map(jcr -> jcr.adaptTo(ValueMap.class)).orElse(null);

    title = Optional.ofNullable(heroVM).map(vm -> vm.get("title", String.class)).orElse(null);
    if (Objects.isNull(title)) {
      title = Optional.ofNullable(jcrVM).map(vm -> vm.get("title", String.class)).orElse(null);
    }

    if (isArticle) {
      copy = Optional.ofNullable(heroVM).map(vm -> vm.get("subtitle", String.class)).orElse(null);
      if (Objects.isNull(copy)) {
        copy = Optional.ofNullable(heroVM)
            .map(vm -> vm.get("subtitle", String.class)).orElse(null);
      }
    } else if (isFeatured) {
      if (Objects.nonNull(jcrVM)) {
        String cityId = jcrVM.get("city", String.class);
        copy = Optional.ofNullable(cityId).map(id -> regionCityService.getRegionCityById(id, lang))
            .map(RegionCity::getName).orElse(null);
        Date startDate = jcrVM.get("calendarStartDate", Date.class);
        Date endDate = jcrVM.get("calendarEndDate", Date.class);
        if (Objects.nonNull(startDate) || Objects.nonNull(endDate)) {
          subtitle = formatDate(startDate) + " - " + formatDate(endDate);
        }
      }
    }
  }

  /**
   * Format date.
   * @param dt date
   * @return formatted date
   */
  private String formatDate(Date dt) {
    if (Objects.isNull(dt)) {
      return "...";
    }
    LocalDateTime date =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(dt.getTime()), ZoneId.systemDefault());
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d");
    return date.format(formatter);
  }

  /**
   * Init card image.
   * @param hero component resource
   * @param jcrContent page resource
   */
  private void initCardImage(Resource hero, final Resource jcrContent) {
    Resource image = Optional.ofNullable(hero).map(r -> r.getChild("image")).orElse(null);
    if (Objects.nonNull(image)) {
      cardImage = image.adaptTo(Image.class);
    } else if (Objects.nonNull(jcrContent)) {
      ValueMap jcrVM = jcrContent.adaptTo(ValueMap.class);
      String imagePath = jcrVM.get("featureEventImage", String.class);
      String imageMobPath = jcrVM.get("featureEventMobileImage", String.class);
      String s7imagePath = jcrVM.get("s7featureEventImage", String.class);
      String s7imageMobPath = jcrVM.get("s7featureEventMobileImage", String.class);

      if (StringUtils.isBlank(imagePath)) {
        imagePath = jcrVM.get("featureImage", String.class);
        s7imagePath = jcrVM.get("s7featureImage", String.class);
      }

      if (StringUtils.isBlank(imageMobPath)) {
        imageMobPath = imagePath;
        s7imageMobPath = s7imagePath;
      }

      cardImage = new Image();
      cardImage.setFileReference(imagePath);
      cardImage.setS7fileReference(s7imagePath);
      cardImage.setMobileImageReference(imageMobPath);
      cardImage.setS7mobileImageReference(s7imageMobPath);
    }
  }

  /**
   * Get hero component resource.
   *
   * @param jcrContent - jcrContent resource
   * @return hero component resource
   */
  private Resource getHeroComponent(Resource jcrContent) {
    if (jcrContent.isResourceType("sauditourism/components/structure/event-detail-page")) {
      return Optional.ofNullable(jcrContent)
          .map(jcr -> jcr.getChild("detail-hero")).orElse(null);
    } else if (jcrContent.isResourceType("sauditourism/components/structure/package-detail-page")) {
      return Optional.ofNullable(jcrContent)
          .map(jcr -> jcr.getChild("h01-brand-page-hero")).orElse(null);
    } else {
      Resource root = Optional.ofNullable(jcrContent).map(jcr -> jcr.getChild("root")).orElse(null);
      Resource grid = Optional.ofNullable(root).map(r -> r.getChild("responsivegrid")).orElse(null);
      if (Objects.nonNull(grid) && grid.hasChildren()) {
        Resource hero = grid.getChildren().iterator().next();
        if (hero.getResourceType().contains("hero")) {
          return hero;
        }
      }
    }
    return null;
  }

  /**
   * Model class of navigation cards.
   */
  @Model(adaptables = Resource.class,
         defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Getter
  public static class NumberCard {
    /**
     * Title.
     */
    @ValueMapValue
    @Expose
    private String title;

    /**
     * Icon.
     */
    @ValueMapValue
    @Expose
    private String icon;

    /**
     * Number.
     */
    @ValueMapValue
    @Expose
    private String number;
  }
}
