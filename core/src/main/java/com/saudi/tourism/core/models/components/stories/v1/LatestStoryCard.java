package com.saudi.tourism.core.models.components.stories.v1;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.components.contentfragment.story.StoryCFModel;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.api.resource.Resource;

/**
 * Latest Story Card dto.
 */
@Data
@Builder
public class LatestStoryCard {
  /**
   * Story title.
   */
  @Expose
  private String title;

  /**
   * Banner Image.
   */
  @Expose
  private Image image;

  /**
   * Tags.
   */
  @Expose
  private List<String> tags;

  /**
   * Story page.
   */
  @Expose
  private Link link;

  /**
   * Published date.
   */
  private Calendar publishedDate;

  /**
   * Method to transform a StoryCFModel to LatestStoryCard.
   *
   * @param model    The StoryCFModel
   * @param resource resource
   * @param language language
   * @return a LatestStoryCard object
   */
  public static final LatestStoryCard fromCFModel(final StoryCFModel model, final Resource resource,
                                                  final String language) {
    if (model == null) {
      return null;
    }

    Image image = null;
    List<StoryCFModel.ImageBanner> imageBanners = model.getImageBanners();
    // Get the first image banner with "image" as type.
    if (CollectionUtils.isNotEmpty(imageBanners)) {
      image = imageBanners.stream()
        .filter(imageBanner -> "image".equals(imageBanner.getType()))
        .findFirst()
        .map(StoryCFModel.ImageBanner::getImageBanner)
        .orElse(null);
    }
    if (Objects.nonNull(image)) {
      DynamicMediaUtils.setAllImgBreakPointsInfo(image,
          "crop-375x667",
          "crop-375x667",
          "1280",
          "420",
          resource.getResourceResolver(),
          resource);
    }

    final LatestStoryCard latestStoryCard = LatestStoryCard.builder()
        .title(model.getTitle())
        .image(image)
        .link(model.getLink())
        .tags(model.getTags())
        .publishedDate(model.getPublishedDate())
        .build();
    return latestStoryCard;
  }
}
