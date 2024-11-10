package com.saudi.tourism.core.services.stories.v1;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.saudi.tourism.core.models.common.Category;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.components.contentfragment.destination.DestinationCFModel;
import com.saudi.tourism.core.models.components.contentfragment.story.StoryCFModel;
import lombok.Builder;
import lombok.Data;
import lombok.With;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

/**
 * Story.
 */
@Builder
@Data
@With
public class Story {

  /**
   * Type.
   */
  private String type;

  /**
   * id for the story.
   */
  private String id;

  /**
   * Title.
   */
  private String title;

  /** Banner images. */
  private List<Image> bannerImages;

  /**
   * Destination.
   */
  private String city;

  /**
   * destinationPath.
   */
  @JsonIgnore
  private String destinationPath;

  /**
   * favorite.
   */
  @Builder.Default
  private Boolean hideFavorite = false;

  /**
   * Location.
   */
  private Destination destination;

  /**
   * Card image.
   */
  private Image image;

  /**
   * Tags.
   */
  @JsonIgnore
  private transient List<String> tags;

  /**
   * tag.
   */
  private String tag;

  /**
   * favId.
   */
  private String favId;

  /**
   * Page Link.
   */
  private Link pageLink;

  /**
   * Card link.
   */
  private String cardCtaLink;

  /**
   * Categories Tags.
   */
  @JsonIgnore
  private transient List<String> categoriesTags;

  /**
   * Categories Tags.
   */
  private  List<Category> categories;

  /**
   * Internal Page Path.
   */
  @JsonIgnore
  private transient String pagePath;

  /**
   * Published Date.
   */
  @JsonIgnore
  private transient Calendar publishedDate;

  /**
   * Event Latitude.
   */
  @JsonIgnore
  private transient String lat;

  /**
   * Event Longitude.
   */
  @JsonIgnore
  private transient String lng;

  public static Story fromCFModel(StoryCFModel model) {
    if (model == null) {
      return null;
    }

    var image = extractFirstImage(model.getImages());

    return Story.builder()
      .type("story")
      .id(Optional.ofNullable(model.getTitle())
        .map(title -> title.toLowerCase().replace(" ", ""))
        .orElse(""))
      .title(model.getTitle())
      .city(Optional.ofNullable(model.getDestination())
        .map(Destination::fromCFModel)
        .map(Destination::getTitle)
        .orElse(""))
      .hideFavorite(Optional.ofNullable(model.isHideFavorite()).orElse(false))
      .favId(model.getFavId())
      .image(image)
      .bannerImages(Collections.singletonList(image))
      .destination(Destination.fromCFModel(model.getDestination()))
      .cardCtaLink(model.getStoryPagePath())
      .pageLink(model.getLink())
      .categoriesTags(
        Optional.ofNullable(model.getCategoriesTags())
          .map(Arrays::stream)
          .orElseGet(Stream::empty)
          .collect(Collectors.toList()))
      .destinationPath(Optional.ofNullable(model.getDestination())
        .map(DestinationCFModel::getResource)
        .map(Resource::getPath)
        .orElse(""))
        .pagePath(model.getInternalPagePath())
        .publishedDate(model.getPublishedDate())
        .lat(Optional.ofNullable(model.getLatitude())
            .map(Object::toString)
            .orElse(null))
        .lng(Optional.ofNullable(model.getLongitude())
            .map(Object::toString)
            .orElse(null))
        .build();
  }

  /**
   * Destination.
   */
  @Builder
  @Data
  public static class Destination {
    /**
     * Destination id.
     */
    @JsonIgnore
    private String id;

    /**
     * Destination Title.
     */
    private String title;

    /**
     * Destination path.
     */
    private String path;

    public static final Destination fromCFModel(final DestinationCFModel model) {
      if (model == null) {
        return null;
      }

      return Destination.builder()
        .id(model.getDestinationId())
        .title(model.getTitle())
        .path(model.getResource().getPath())
        .build();
    }
  }

  /**
   * Extract first image from the list of images.
   *
   * @param images
   * @return image
   */
  private static Image extractFirstImage(String[] images) {
    final var image = new Image();
    if (images != null) {
      Arrays.stream(images)
          .map(JsonParser::parseString)
          .map(JsonElement::getAsJsonObject)
          .filter(jsonObject -> "image".equals(jsonObject.get("type").getAsString()))
          .findFirst()
          .ifPresent(imageJson -> {
            image.setFileReference(getJsonString(imageJson, "image"));
            image.setMobileImageReference(getJsonString(imageJson, "image"));
            image.setS7fileReference(getJsonString(imageJson, "s7image"));
            image.setS7mobileImageReference(getJsonString(imageJson, "s7image"));
          });
    }
    return image;
  }

  /**
   * Get json string.
   *
   * @param jsonObject
   * @param key
   * @return json string
   */
  private static String getJsonString(JsonObject jsonObject, String key) {
    if (jsonObject != null && jsonObject.has(key)) {
      JsonElement element = jsonObject.get(key);
      if (!element.isJsonNull()) {
        return element.getAsString();
      }
    }
    return StringUtils.EMPTY;
  }
}
