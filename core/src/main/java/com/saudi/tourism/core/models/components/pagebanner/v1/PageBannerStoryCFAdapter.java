package com.saudi.tourism.core.models.components.pagebanner.v1;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saudi.tourism.core.models.common.BannerCard;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.ImageBanner;
import com.saudi.tourism.core.models.common.Video;
import com.saudi.tourism.core.models.components.contentfragment.utils.ContentFragmentAwareModel;
import com.saudi.tourism.core.utils.PrimConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.Adaptable;
import org.osgi.service.component.annotations.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapts story CF to PageBannerCFModel.
 */
@Component(service = PageBannerCFAdapter.class, immediate = true)
@Slf4j
public class PageBannerStoryCFAdapter implements PageBannerCFAdapter, ContentFragmentAwareModel {



  @Override
  public boolean supports(Adaptable adaptable) {
    if (adaptable == null) {
      return false;
    }

    var cf = adaptable.adaptTo(ContentFragment.class);
    if (cf == null) {
      return false;
    }

    return StringUtils.equalsIgnoreCase("story", cf.getTemplate().getTitle());
  }

  @Override
  public PageBannerCFModel adaptTo(Adaptable adaptable) {
    final var cf = adaptable.adaptTo(ContentFragment.class);

    if (cf == null) {
      return null;
    }
    var title = getElementValue(cf, "title", String.class);
    var subtitle = getElementValue(cf, "subtitle", String.class);

    String[] imagesArray = getElementValue(cf, "images", String[].class);


    final var assets = Optional.ofNullable(imagesArray).map(Arrays::asList).orElse(Collections.emptyList());
    ObjectMapper mapper = new ObjectMapper();


    String firstImage = null;
    List<String> remainingAssets = new ArrayList<>();
    boolean foundFirstImage = false;

    for (String jsonString : assets) {
      if (!foundFirstImage) {
        try {
          JsonNode rootNode = mapper.readTree(jsonString);
          JsonNode typeNode = rootNode.get("type");
          if (typeNode != null && "image".equals(typeNode.asText())) {
            firstImage = jsonString;
            foundFirstImage = true;
            continue;
          }
        } catch (JsonProcessingException e) {
          LOGGER.warn("Error while parsing jsonString", e);
        }
      }
      remainingAssets.add(jsonString);
    }

    final var image = new Image();

    if (StringUtils.isNotBlank(firstImage)) {

      try {
        ImageCF imageCF = mapper.readValue(firstImage, ImageCF.class);
        image.setFileReference(imageCF.getImage());
        image.setMobileImageReference(imageCF.getImage());
        image.setS7fileReference(imageCF.getS7image());
        image.setS7mobileImageReference(imageCF.getS7image());
      } catch (JsonProcessingException e) {
        LOGGER.warn("Error while parsing imageBanner", e);

      }
    }
    var bannerCard = new BannerCard();
    bannerCard.setTitle(title);
    bannerCard.setImage(image);
    bannerCard.setDescription(subtitle);

    var cards = new ArrayList<BannerCard>();
    cards.add(bannerCard);

    final var gallery =
        remainingAssets.stream()
            .map(this::createImageBannerFromJsonString)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());


    return PageBannerCFModel.builder()
        .cards(cards).hideImageBrush(PrimConstants.STR_FALSE).assets(gallery)
        .build();
  }

  private ImageBanner createImageBannerFromJsonString(String jsonString) {
    try {

      ObjectMapper mapper = new ObjectMapper();

      ImageCF asset = mapper.readValue(jsonString, ImageCF.class);
      String type = asset.getType();
      String location = asset.getLocation();
      String image;
      String s7image;
      String video;
      String s7video;
      String thumbnail;

      ImageBanner item = new ImageBanner();
      item.setType(type);
      item.setLocation(location);

      if (StringUtils.equals("image", type)) {

        image = asset.getImage();
        s7image = asset.getS7image();

        if (StringUtils.isNotBlank(image)) {
          Image imageItem = new Image();
          imageItem.setFileReference(image);
          imageItem.setS7fileReference(s7image);


          item.setImage(imageItem);
        }

      } else if (StringUtils.equals("video", type)) {
        video = asset.getVideo();
        s7video = asset.getS7video();
        thumbnail = asset.getThumbnail();
        Video videoItem = new Video();
        videoItem.setVideoFileReference(video);
        videoItem.setS7videoFileReference(s7video);
        videoItem.setPoster(thumbnail);
        item.setVideo(videoItem);
        item.setThumbnail(thumbnail);
      } else {
        return null;
      }
      return item;
    } catch (JsonProcessingException e) {
      LOGGER.warn("Error while parsing GalleryItem", e);
      return null;
    }
  }

  /**
   * Represents the Image Banner.
   */
  @Getter
  @Setter
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class ImageCF {

    /**
     * Type.
     */
    private String type;

    /**
     * image.
     */
    private String image;

    /**
     * s7image.
     */
    private String s7image;

    /**
     * video.
     */
    private String video;

    /**
     * s7video.
     */
    private String s7video;

    /**
     * thumbnail.
     */
    private String thumbnail;

    /**
     * location.
     */
    private String location;

    /**
     * featureInSubStory.
     */
    private String featureInSubStory;

    public ImageCF() {
    }

  }

}
