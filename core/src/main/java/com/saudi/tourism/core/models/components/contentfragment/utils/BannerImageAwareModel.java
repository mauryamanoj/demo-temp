package com.saudi.tourism.core.models.components.contentfragment.utils;

import com.google.gson.JsonParser;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Video;
import com.saudi.tourism.core.models.components.contentfragment.common.BannerImage;
import com.saudi.tourism.core.models.components.contentfragment.common.BannerVideo;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Banner Image Aware Model.
 */
public interface BannerImageAwareModel {
  /**
   * Build Banner Videos.
   * @param bannerImagesObjects
   * @return Banner Videos
   */
  default List<BannerVideo> buildBannerVideos(final String[] bannerImagesObjects) {
    return Arrays.stream(bannerImagesObjects)
        .filter(Objects::nonNull)
        .filter(StringUtils::isNotEmpty)
        .map(
            s -> {
              final var jsonObject = JsonParser.parseString(s).getAsJsonObject();
              if (Objects.isNull(jsonObject)) {
                return null;
              }

              String bannerImageType = null;
              String bannerVideoPath = null;
              String bannerVideoS7Path = null;
              String bannerVideoThumbnailPath = null;
              String bannerVideoLocation = null;

              if (jsonObject.has("type")) {
                bannerImageType = jsonObject.get("type").getAsString();
              }

              if (jsonObject.has("video")) {
                bannerVideoPath = jsonObject.get("video").getAsString();
              }

              if (jsonObject.has("s7video")) {
                bannerVideoS7Path = jsonObject.get("s7video").getAsString();
              }

              if (jsonObject.has("thumbnail")) {
                bannerVideoThumbnailPath = jsonObject.get("thumbnail").getAsString();
              }

              if (jsonObject.has("location")) {
                bannerVideoLocation = jsonObject.get("location").getAsString();
              }

              if (!StringUtils.equals("video", bannerImageType)) {
                return null;
              }

              if (StringUtils.isEmpty(bannerVideoPath)) {
                return null;
              }

              final var bannerVideo = new Video();
              bannerVideo.setVideoFileReference(bannerVideoPath);
              bannerVideo.setS7videoFileReference(bannerVideoS7Path);
              bannerVideo.setMobileVideoReference(bannerVideoPath);
              bannerVideo.setS7mobileVideoReference(bannerVideoS7Path);

              final var builder =
                  BannerVideo.builder().video(bannerVideo).location(bannerVideoLocation);

              if (StringUtils.isNotEmpty(bannerVideoThumbnailPath)) {
                final var thumbnail = new Image();
                thumbnail.setFileReference(bannerVideoThumbnailPath);
                builder.thumbnail(thumbnail);
              }

              return builder.build();
            })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  /**
   * Build banner images.
   * @param bannerImagesObjects
   * @return banner images
   */
  default List<BannerImage> buildBannerImages(String[] bannerImagesObjects) {
    return Arrays.stream(bannerImagesObjects)
      .filter(Objects::nonNull)
      .filter(StringUtils::isNotEmpty)
      .map(
        s -> {
          final var jsonObject = JsonParser.parseString(s).getAsJsonObject();
          if (Objects.isNull(jsonObject)) {
            return null;
          }

          String bannerImageType = null;
          String bannerImagePath = null;
          String s7bannerImagePath = null;
          String altBannerImage = null;
          String bannerImageLocation = null;

          if (jsonObject.has("type")) {
            bannerImageType = jsonObject.get("type").getAsString();
          }

          if (jsonObject.has("image")) {
            bannerImagePath = jsonObject.get("image").getAsString();
          }

          if (jsonObject.has("s7image")) {
            s7bannerImagePath = jsonObject.get("s7image").getAsString();
          }

          if (jsonObject.has("alt")) {
            altBannerImage = jsonObject.get("alt").getAsString();
          }

          if (jsonObject.has("location")) {
            bannerImageLocation = jsonObject.get("location").getAsString();
          }

          if (!StringUtils.equals("image", bannerImageType)) {
            return null;
          }

          if (StringUtils.isEmpty(bannerImagePath)) {
            return null;
          }

          final var bannerImage = new Image();
          bannerImage.setFileReference(bannerImagePath);
          bannerImage.setS7fileReference(s7bannerImagePath);
          bannerImage.setMobileImageReference(bannerImagePath);
          bannerImage.setS7mobileImageReference(s7bannerImagePath);
          bannerImage.setAlt(altBannerImage);

          return BannerImage.builder().image(bannerImage).location(bannerImageLocation).build();
        })
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
  }
}
