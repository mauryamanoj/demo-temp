package com.saudi.tourism.core.models.app.page;

import com.day.cq.commons.Externalizer;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Data;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * AppHomepage model.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class NativeAppHomepage implements Serializable {

  /**
   * carousel object.
   */
  @ChildResource(name = "carousel")
  private transient AppCarousel carouselObj;


  /**
   * Explore deals object.
   */
  @ChildResource(name = "explore-deals/cards")
  private List<Card> exploreDeals;

  /**
   * carousel.
   */
  @Expose
  private List<AppPage> carousel;

  /**
   * travel Essentials.
   */
  @Setter
  @Expose
  private List<Card> travelEssentials;

  /**
   * Partners.
   */
  @Setter
  @Expose
  private List<Card> partners;


  /**
   * Promotions Home.
   */
  @Setter
  @Expose
  private List<Card> promotionsHome;

  /**
   * Promotions.
   */
  @Setter
  @Expose
  private List<PromotionCard> promotions;

  /**
   * happening Now.
   */
  @Setter
  @Expose
  private List<PromotionCard> happeningNow;

  /**
   * eVisa Banner.
   */
  @Expose
  @ChildResource
  private EVisaBannerConfig eVisaBanner;

  /**
   * homePageOrder.
   */
  @Setter
  @Expose
  private List<HomeSection> homepageOrder;

  /**
   * CurrentResource.
   */
  @Self
  private transient Resource currentResource;

  /**
   * homepageOrder.
   */
  private static final String HOME_PAGE_ORDER = "homepageOrder";

  /**
   * Label constant for cards resource name.
   */
  private static final String CARDS_LABEL = "cards";

  /**
   * Label constant for travel essentials resource name.
   */
  private static final String TRAVEL_ESSENTIALS_LABEL = "travel-essentials";

  /**
   * Label constant for explore deals resource name.
   */
  private static final String EXPLORE_DEALS_LABEL = "explore-deals";

  /**
   * Label constant for flydeal resource name.
   */
  private static final String FLYDEAL_LABEL = "flydeal";

  /**
   * Label constant for happening now resource name.
   */
  private static final String HAPPENING_NOW_LABEL = "happeningNow";

  /**
   * Label constant for promotions home resource name.
   */
  private static final String PROMOTIONS_HOME_LABEL = "promotionsHome";

  /**
   * Label constant for promotions resource name.
   */
  private static final String PROMOTIONS_LABEL = "promotions";

  /**
   * Label constant for homepageOrder child resource name.
   */
  private static final String HOME_PAGE_SECTION_LABEL = "homeSections";


  /**
   * Sling settings service to check if the current environment is author or publish
   * (nullified in PostConstruct).
   */
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private transient SlingSettingsService settingsService;

  /**
   * init method of sling model.
   */
  @PostConstruct
  protected void init() {
    ResourceResolver resolver = currentResource.getResourceResolver();
    if (Objects.nonNull(eVisaBanner) && Objects.nonNull(eVisaBanner.getCtaUrl())) {
      eVisaBanner.setCtaUrl(LinkUtils.getAuthorPublishUrl(resolver, eVisaBanner.getCtaUrl(),
          settingsService.getRunModes().contains(Externalizer.PUBLISH)));
    }
    if (null != carouselObj) {
      this.carousel = carouselObj.getAppPages();
    }
    setTravelEssentials(getCards(TRAVEL_ESSENTIALS_LABEL));
    setPartners(getCards(FLYDEAL_LABEL));
    setHappeningNow(getCards(HAPPENING_NOW_LABEL));
    setPromotionsHome(getCards(PROMOTIONS_HOME_LABEL));
    setPromotions(getCards(PROMOTIONS_LABEL));
    setHomepageOrder(getHomeSections(HOME_PAGE_ORDER));
  }

  /**
   * This Method sets the cards info inside any child resource.
   *
   * @param resourceName Resource name
   * @param <T>          generic type.
   * @return list of cards.
   */
  private <T> List<T> getCards(final String resourceName) {
    Resource resource = null;
    if (!currentResource.getPath().endsWith("/" + resourceName)) {
      resource = currentResource.getChild(resourceName);
    } else {
      resource = currentResource;
    }
    final boolean isPromotionsCard = resourceName.equals(PROMOTIONS_LABEL)
        || resourceName.equals(HAPPENING_NOW_LABEL);
    final List<T> cardsList = new ArrayList<>();
    if (null != resource && resource.hasChildren()) {
      Resource childResource = resource.getChild(CARDS_LABEL);
      if (null != childResource) {
        childResource.getChildren().forEach(child -> {
          if (isPromotionsCard) {
            cardsList.add((T) child.adaptTo(PromotionCard.class));
          } else {
            cardsList.add((T) child.adaptTo(Card.class));
          }
        });
      }
    }
    return cardsList;
  }

  /**
   * This is Get Home Section method to set the order.
   *
   * @param resourceName .
   * @return List .
   */
  private List<HomeSection> getHomeSections(final String resourceName) {
    Resource resource = null;
    if (!currentResource.getPath().endsWith("/" + resourceName)) {
      resource = currentResource.getChild(HOME_PAGE_ORDER);
    } else {
      resource = currentResource;
    }
    final List<HomeSection> homeSectionList = new ArrayList<>();
    if (null != resource && resource.hasChildren()) {
      Resource childResource = resource.getChild(HOME_PAGE_SECTION_LABEL);
      if (null != childResource) {
        childResource.getChildren().forEach(child -> {
          HomeSection homeSection = new HomeSection();
          Section secObj = child.adaptTo(Section.class);
          homeSection.setSection(secObj);
          homeSectionList.add(homeSection);
        });
      }
    }
    return homeSectionList;
  }
}
