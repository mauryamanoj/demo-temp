package com.saudi.tourism.core.models.components.photogallery.v1;

import com.saudi.tourism.core.models.components.card.v1.CardModel;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import java.util.List;

/**
 * Photo gallery model.
 */
@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class PhotoGalleryModel {

  /**
   * Amount of photos to be displayed in the initial grid.
   */
  public static final int GRID_PHOTOS_AMOUNT = 3;

  /**
   * Last photo index in the grid to display additional information.
   */
  public static final int GRID_LAST_PHOTO_INDEX = GRID_PHOTOS_AMOUNT - 1;

  /**
   * Cards.
   */
  @ChildResource
  private List<CardModel> cards;

  /**
   * This number is needed to display number of photos that are not visible to users in the
   * carousel.
   * @return number of background photos
   */
  public int getAdditionalPhotosAmount() {
    if (CollectionUtils.isNotEmpty(cards) && cards.size() >= GRID_PHOTOS_AMOUNT) {
      return cards.size() - GRID_PHOTOS_AMOUNT;
    } else {
      return 0;
    }
  }
}
