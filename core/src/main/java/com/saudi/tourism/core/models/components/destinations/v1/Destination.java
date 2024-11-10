package com.saudi.tourism.core.models.components.destinations.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Category;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.components.contentfragment.destination.DestinationCFModel;
import com.saudi.tourism.core.services.weather.model.output.SimpleWeatherModel;
import lombok.Data;

import java.util.List;

/**
 * Destination dto.
 */
@Data
public class Destination {
  /**
   * Destination title.
   */
  @Expose
  private String title;

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
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<Category> categories;



  /**
   * Weather.
   */
  @Expose
  private SimpleWeatherModel weather;

  /**
   * Destination page.
   */
  @Expose
  private Link pagePath;

  /**
   * tagline.
   */
  @Expose
  private String tagline;

  /**
   * Method to transform a DestinationCFModel to Destination.
   * @param model The DestinationCFModel
   * @return a Destination object
   */
  public static final Destination fromCFModel(final DestinationCFModel model) {
    if (model == null) {
      return null;
    }
    final var destination = new Destination();
    destination.setTitle(model.getTitle());
    destination.setSubTitle(model.getSubTitle());
    destination.setBannerImage(model.getBannerImage());
    destination.setPopUpImage(model.getPopUpImage());
    destination.setLatitude(model.getLatitude());
    destination.setLongitude(model.getLongitude());
    destination.setId(model.getId());
    destination.setAboutHeading(model.getAboutHeading());
    destination.setAboutDescription(model.getAboutDescription());
    destination.setCategories(model.getCategories());
    destination.setPagePath(model.getPagePath());
    destination.setTagline(model.getTagline());

    return destination;
  }
}
