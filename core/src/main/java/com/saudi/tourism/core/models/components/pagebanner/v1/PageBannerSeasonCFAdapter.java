package com.saudi.tourism.core.models.components.pagebanner.v1;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.saudi.tourism.core.models.common.BannerCard;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.components.contentfragment.utils.ContentFragmentAwareModel;
import com.saudi.tourism.core.utils.PrimConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.Adaptable;
import org.osgi.service.component.annotations.Component;

import java.util.ArrayList;

/**
 * Adapts season CF to PageBannerCFModel.
 */
@Component(service = PageBannerCFAdapter.class, immediate = true)
@Slf4j
public class PageBannerSeasonCFAdapter implements PageBannerCFAdapter, ContentFragmentAwareModel {



  @Override
  public boolean supports(Adaptable adaptable) {
    if (adaptable == null) {
      return false;
    }

    var cf = adaptable.adaptTo(ContentFragment.class);
    if (cf == null) {
      return false;
    }

    return StringUtils.equalsIgnoreCase("season", cf.getTemplate().getTitle());
  }

  @Override
  public PageBannerCFModel adaptTo(Adaptable adaptable) {
    final var cf = adaptable.adaptTo(ContentFragment.class);

    if (cf == null) {
      return null;
    }
    var title = getElementValue(cf, "title", String.class);
    var subtitle = getElementValue(cf, "subtitle", String.class);

    var imagePath = getElementValue(cf, "image", String.class);
    var s7imagePath = getElementValue(cf, "s7image", String.class);

    final var image = new Image();
    image.setFileReference(imagePath);
    image.setMobileImageReference(imagePath);
    image.setS7fileReference(s7imagePath);
    image.setS7mobileImageReference(s7imagePath);


    var bannerCard = new BannerCard();
    bannerCard.setTitle(title);
    bannerCard.setImage(image);
    bannerCard.setDescription(subtitle);

    var cards = new ArrayList<BannerCard>();
    cards.add(bannerCard);




    return PageBannerCFModel.builder()
        .cards(cards).hideImageBrush(PrimConstants.STR_FALSE)
        .build();
  }
}
