package com.saudi.tourism.core.services.attractions.v1;

import com.saudi.tourism.core.models.common.Category;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.components.contentfragment.attraction.AttractionCFModel;
import com.saudi.tourism.core.models.components.contentfragment.destination.DestinationCFModel;
import com.saudi.tourism.core.models.components.contentfragment.common.BannerImage;
import lombok.Builder;
import lombok.Data;
import lombok.With;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Attraction.
 */
@Builder
@Data
@With
public class Attraction {

  /**
   * id for the attraction.
   */
  private String id;

  /**
   * Attraction title.
   */
  private String title;


  /**
   * Attraction subTitle.
   */
  private String subTitle;

  /**
   * Attractions banner image.
   */
  private Image bannerImage;



  /**
   * Attraction Tag Categories.
   */
  private List<Category> categories;


  /**
   * Attraction location.
   */
  private String location;

  /**
   * Latitude.
   */
  private String latitude;

  /**
   * Longitude.
   */
  private String longitude;

  /**
   * Page Link.
   */
  private Link pageLink;

  /**
   * Destination.
   */
  private transient DestinationCFModel destinationCFModel;


  public static Attraction fromCFModel(AttractionCFModel model) {

    if (model == null) {
      return null;
    }

    List<Category> categoryList = new ArrayList<>();
    if (model.getCategoriesTags() != null) {
      for (String title : model.getCategoriesTags()) {
        Category category = new Category();
        category.setTitle(title);
        categoryList.add(category);
      }
    } else {
      categoryList = Collections.emptyList();
    }

    Image bannerImage = null;
    Optional<Image> bannerImageOptional =
        CollectionUtils.emptyIfNull(model.getBannerImages()).stream()
            .map(BannerImage::getImage)
            .findFirst();

    if (bannerImageOptional.isPresent()) {
      bannerImage = bannerImageOptional.get(); // Set the banner image if it's present
    }

    return Attraction.builder()
      .id(model.getId())
      .title(model.getTitle())
      .subTitle(model.getSubtitle())
      .bannerImage(bannerImage)
      .destinationCFModel(model.getDestination())
      .location(Optional.ofNullable(model.getDestination())
        .map(DestinationCFModel::getTitle)
        .orElse(""))
      .categories(categoryList)
      .latitude(model.getLat())
      .longitude(model.getLng())
      .pageLink(model.getPageLink())
      .build();
  }

}
