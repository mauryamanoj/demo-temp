package com.saudi.tourism.core.models.components.keepExploring.v1;

import com.day.cq.commons.Externalizer;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.components.contentfragment.destination.DestinationCFModel;
import com.saudi.tourism.core.utils.CropEnum;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Builder;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;

import java.util.Objects;

/** Destination Card dto. */
@Data
@Builder
public class DestinationCard {

  /** Destination title. */
  @Expose private String title;

  /** Destination subTitle. */
  @Expose private String subTitle;

  /** Destination tagline. */
  @Expose private String tagline;

  /** Popup Image. */
  @Expose private Image image;

  /** link the the Card. */
  @Expose private String cardCtaLink;

  public static DestinationCard fromCFModel(
      final DestinationCFModel model,
      final Resource resource,
      final ResourceResolver resourceResolver,
      final SlingSettingsService settingsService) {

    if (model == null) {
      return null;
    }
    String title = model.getTitle();
    String subtitle = model.getSubTitle();
    Image image = model.getPopUpImage();
    String tagline = model.getTagline();

    if (Objects.nonNull(image)) {
      image.setTransparent(true);
      DynamicMediaUtils.setAllImgBreakPointsInfo(
          image,
          CropEnum.CROP_375x210.getValue(),
          CropEnum.CROP_375x210.getValue(),
          "1280",
          "420",
          resource.getResourceResolver(),
          resource);
    }

    String cardCtaLink = null;
    if (model.getPagePath() != null) {
      cardCtaLink = LinkUtils.getAuthorPublishUrl(
          resourceResolver,
          model.getPagePath().getUrl(),
          settingsService.getRunModes().contains(Externalizer.PUBLISH));
    }


    final DestinationCard destinationCard =
        DestinationCard.builder()
            .title(title)
            .subTitle(subtitle)
            .image(image)
            .tagline(tagline)
            .cardCtaLink(cardCtaLink)
            .build();
    return destinationCard;
  }
}
