package com.saudi.tourism.core.services.destinations.v1;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Category;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.components.contentfragment.destination.DestinationCFModel;
import lombok.Builder;
import lombok.Data;
import lombok.With;

import java.util.List;

@Builder
@Data
@With
public class Destination {
  /**
   * Destination title.
   */
  @Expose
  private String title;

  /**
   * Destination path.
   */
  @Expose
  private String path;

  /**
   * tagline for destination.
   */
  @Expose
  private String tagline;

  /**
   * Destination subtitle.
   */
  @Expose
  private String subTitle;

  /**
   * Banner Image.
   */
  @Expose
  private Image bannerImage;


  /**
   * PopUp Image.
   */
  @Expose
  private Image popUpImage;

  /**
   * Latitude.
   */
  @Expose
  private String latitude;

  /**
   * Longitude.
   */
  @Expose
  private String longitude;

  /**
   * Id.
   */
  @Expose
  private String id;

  /**
   * About heading.
   */
  @Expose
  private String aboutHeading;

  /**
   * About description.
   */
  @Expose
  private String aboutDescription;

  /**
   * Categories.
   */
  @Expose
  private List<Category> categories;


  public static final Destination fromCFModel(final DestinationCFModel model) {
    if (model == null) {
      return null;
    }
    return Destination.builder()
      .title(model.getTitle())
      .subTitle(model.getSubTitle())
      .aboutDescription(model.getAboutDescription())
      .aboutHeading(model.getAboutHeading())
      .bannerImage(model.getBannerImage())
      .popUpImage(model.getPopUpImage())
      .id(model.getId())
      .latitude(model.getLatitude())
      .longitude(model.getLongitude())
      .categories(model.getCategories())
      .tagline(model.getTagline())
      .path(model.getDestinationPagePath())
      .build();
  }
}
