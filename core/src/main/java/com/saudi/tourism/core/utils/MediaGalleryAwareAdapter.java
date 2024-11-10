package com.saudi.tourism.core.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.ImageBanner;
import com.saudi.tourism.core.models.common.Video;
import org.apache.commons.lang3.StringUtils;

import static com.day.cq.personalization.offerlibrary.usebean.OffercardPropertiesProvider.LOGGER;

/**
 * This class is used as a utility for media gallery.
 */
public interface MediaGalleryAwareAdapter {

  /**
   * This method is used to create gallery items from json object.
   *
   * @param jsonString
   * @return ImageBanner
   */
  default ImageBanner createImageBannerFromJsonString(String jsonString) {
    try {
      JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
      if (jsonObject == null || jsonObject.isJsonNull()) {
        return null;
      }
      var type = StringUtils.EMPTY;
      var location = StringUtils.EMPTY;
      var image = StringUtils.EMPTY;
      var video = StringUtils.EMPTY;
      var s7image = StringUtils.EMPTY;
      var s7video = StringUtils.EMPTY;
      var thumbnail = StringUtils.EMPTY;

      if (jsonObject.has("type")) {
        type = jsonObject.get("type").getAsString();
      }
      if (jsonObject.has("location")) {
        location = jsonObject.get("location").getAsString();
      }

      ImageBanner item = new ImageBanner();
      item.setType(type);
      item.setLocation(location);
      if (StringUtils.equals("image", type)) {
        if (jsonObject.has("image")) {
          image = jsonObject.get("image").getAsString();
        }
        if (jsonObject.has("s7image")) {
          s7image = jsonObject.get("s7image").getAsString();
        }
        Image imageItem = new Image();
        imageItem.setFileReference(image);
        imageItem.setS7fileReference(s7image);
        item.setImage(imageItem);
      }
      if (StringUtils.equals("video", type)) {
        if (jsonObject.has("video")) {
          video = jsonObject.get("video").getAsString();
        }
        if (jsonObject.has("s7video")) {
          s7video = jsonObject.get("s7video").getAsString();
        }
        if (jsonObject.has("thumbnail")) {
          thumbnail = jsonObject.get("thumbnail").getAsString();
        }
        Video videoItem = new Video();
        videoItem.setVideoFileReference(video);
        videoItem.setS7videoFileReference(s7video);
        item.setVideo(videoItem);
        item.setThumbnail(thumbnail);
      }
      return item;
    } catch (JsonParseException e) {
      LOGGER.warn("Error while parsing GalleryItem", e);
      return null;
    }
  }
}
