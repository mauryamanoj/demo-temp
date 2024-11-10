package com.saudi.tourism.core.models.components.cardsgrid;

import com.day.cq.wcm.api.Page;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.components.card.v1.CardModel;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
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

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Grid Card Model.
 */
@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class  CardsGridModel {
  /**
   * List of cards.
   */
  @ChildResource
  private List<GridCard> gridCards;

  /**
   * List of CardModels.
   */
  @Getter
  private List<CardModel> cardModels;

  /**
   * ResourceResolver.
   */
  @Self
  private Resource currentResource;

  /**
   * Sling ResourceResolver.
   */
  @SlingObject
  private transient ResourceResolver resourceResolver;

  /**
   * ResourceBundleProvider.
   */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private ResourceBundleProvider i18nProvider;

  /**
   * init method of sling model.
   */
  @PostConstruct protected void init() {
    cardModels = new ArrayList<>();
    String path = Optional.ofNullable(currentResource.getParent()).map(Resource::getPath)
        .orElse(StringUtils.EMPTY);
    String language =
        CommonUtils.getPageNameByIndex(path, Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION);

    Locale locale = new Locale(language);

    ResourceBundle i18nBundle = i18nProvider.getResourceBundle(locale);
    if (gridCards != null) {
      for (GridCard gridCard : gridCards) {

        cardModels.add(fillCardModel(gridCard, i18nBundle));

        if (gridCard.getCity() != null) {
          gridCard.setCity(i18nBundle.getString(gridCard.getCity()));
        }
      }
    }
  }

  /**
   * init method returns CardModel.
   *
   * @param gridCard object
   * @param i18nBundle resourceBundle
   * @return CardModel
   */
  private CardModel fillCardModel(GridCard gridCard, ResourceBundle i18nBundle) {
    CardModel cardModel = new CardModel();
    cardModel.setActivities(true);
    Resource pageResource = resourceResolver.getResource(gridCard.getLink().getUrl());
    Page page = null;
    if (pageResource != null) {
      page = pageResource.adaptTo(Page.class);
    }
    if (page != null) {
      cardModel.setTitle(org.apache.commons.lang.StringUtils
          .defaultIfEmpty(page.getNavigationTitle(), page.getTitle()));

      cardModel.setDescription(page.getDescription());

      cardModel.setImage(new Image());
      cardModel.getImage()
          .setFileReference(page.getProperties().get(Constants.NAV_IMAGE, StringUtils.EMPTY));
      if (cardModel.getImage().getFileReference().equals(StringUtils.EMPTY)) {
        cardModel.getImage()
            .setFileReference(page.getProperties().get(Constants.FEATURE_IMAGE, StringUtils.EMPTY));
      }
      cardModel.getImage().setMobileImageReference(
          page.getProperties().get(Constants.MOBILE_NAV_IMAGE, StringUtils.EMPTY));
      cardModel.setCity(CommonUtils.getI18nStringOrKey(i18nBundle, gridCard.getCity()));

      cardModel.setActivity(gridCard.getActivity());

      cardModel.setLink(new Link());
      cardModel.getLink().setCopy(i18nBundle.getString(Constants.LEARNMORE));
      cardModel.getLink().setUrl(gridCard.getLink().getUrlWithExtension());
    }
    return cardModel;
  }
}
