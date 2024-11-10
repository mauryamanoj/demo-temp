package com.saudi.tourism.core.models.components.contentfragment.story;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.ImageBanner;
import com.saudi.tourism.core.models.common.Video;
import com.saudi.tourism.core.models.components.contentfragment.utils.ContentFragmentAwareModel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.settings.SlingSettingsService;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.List;
import java.util.stream.Collectors;

@Model(
    adaptables = {Resource.class, ContentFragment.class},
    adapters = SubStoryCFModel.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@Slf4j
public class SubStoryCFModel implements ContentFragmentAwareModel {

  /**
   * Story ContentFragment Model.
   */
  @Self
  private ContentFragment contentFragment;

  /**
   * Current resource.
   */
  @Self
  private Resource resource;

  /**
   * Story page path (corresponding to the current CF).
   */
  private String storyPagePath;
  /**
   * Sling settings service to check if the current environment is author or publish
   * (clear after PostConstruct!).
   */
  @Inject
  private transient SlingSettingsService settingsService;

  public String getTitle() {
    return getElementValue(contentFragment, "title", String.class);
  }

  public String getAboutDescription() {
    return getElementValue(contentFragment, "aboutDescription", String.class);
  }

  /**
   * Get the list of image banners.
   *
   * @return list of image banners.
   */
  public List<ImageBanner> getImageBanners() {
    String[] images = getElementValue(contentFragment, "images", String[].class);

    if (images == null || images.length == 0) {
      return Collections.emptyList();
    }

    ObjectMapper mapper = new ObjectMapper();
    return Arrays.stream(images)
        .map(
            imageJson -> {
              try {
                ImageCF asset = mapper.readValue(imageJson, ImageCF.class);
                ImageBanner item = new ImageBanner();

                final var featured = asset.getFeatureInSubStory();
                var type = asset.getType();


                if (StringUtils.isNotEmpty(featured) && "true".equals(featured)) {
                  item.setType(type);

                  if ("image".equals(type)) {
                    Image imageItem = new Image();
                    imageItem.setFileReference(asset.getImage());
                    imageItem.setS7fileReference(asset.getS7image());
                    item.setImage(imageItem);
                  }

                  if ("video".equals(type)) {
                    Video videoItem = new Video();
                    videoItem.setVideoFileReference(asset.getVideo());
                    videoItem.setS7videoFileReference(asset.getS7video());
                    item.setVideo(videoItem);
                    item.setThumbnail(asset.getThumbnail());
                  }
                } else {
                  return null;
                }

                return item;
              } catch (JsonProcessingException e) {
                LOGGER.warn("Error while parsing imageBanner", e);
                return null;
              }
            })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  @Getter
  @Setter
  public static class ImageCF {

    /**
     * Type.
     */
    @Expose
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
     * featureInSubStory.
     */
    private String featureInSubStory;

    public ImageCF() {
    }

  }
}

