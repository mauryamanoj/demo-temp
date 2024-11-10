package com.saudi.tourism.core.models.app.page;

import com.google.gson.annotations.Expose;
import lombok.Data;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Loyalty AppHomepage model.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class NativeAppLoyaltyHomepage implements Serializable {

  /**
   * carousel object.
   */
  @ChildResource(name = "loyaltyCarousel")
  private transient AppCarousel carouselObj;

  /**
   * carousel.
   */
  @Expose
  private List<AppPage> carousel;


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
  private static final String HOME_PAGE_ORDER = "loyaltyPageOrder";

  /**
   * Label constant for homepageOrder child resource name.
   */
  private static final String HOME_PAGE_SECTION_LABEL = "homeSections";

  /**
   * Label constant for homepageOrder child resource name.
   */
  private static final String LOYALTY_PROMOTIONS = "loyaltyPromo";

  /**
   * Label constant for cards resource name.
   */
  private static final String CARDS_LABEL = "cards";

  /**
   * Setter Promotions for Loyalty Home Page .
   */
  @Setter
  @Expose
  private List<Card> loyaltyPromo;
  /**
   * init method of sling model.
   */
  @PostConstruct
  protected void init() {
    if (null != carouselObj) {
      this.carousel = carouselObj.getAppPages();
    }
    setHomepageOrder(getHomeSections(HOME_PAGE_ORDER));
    setLoyaltyPromo(getCards(LOYALTY_PROMOTIONS));
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
    final List<T> cardsList = new ArrayList<>();
    if (null != resource && resource.hasChildren()) {
      Resource childResource = resource.getChild(CARDS_LABEL);
      if (null != childResource) {
        childResource.getChildren().forEach(child -> {
          cardsList.add((T) child.adaptTo(Card.class));
        });
      }
    }
    return cardsList;
  }
}
