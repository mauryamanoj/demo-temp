package com.saudi.tourism.core.models.common;

import com.google.gson.annotations.Expose;
import lombok.Data;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * Model class of image related data.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class Video implements Serializable {

  /**
   * Variable to store video fileReference.
   */
  @Expose
  @ValueMapValue
  private String videoFileReference;

  /**
   * Variable to store mobile Video Reference.
   */
  @Expose
  @ValueMapValue
  private String mobileVideoReference;

  /**
   * Variable to store s7 Video fileReference.
   */
  @Expose
  @ValueMapValue(name = "s7videoFileReference")
  private String s7videoFileReference;

  /**
   * Variable to store s7 mobile Video Reference.
   */
  @Expose
  @ValueMapValue
  private String s7mobileVideoReference;

  /**
   * Variable to store poster.
   */
  @Expose
  @ValueMapValue
  private String poster;

  /**
   * autorerun.
   */
  @Expose
  @Getter
  @ValueMapValue
  private boolean autorerun;

  /**
   * autoplay.
   */
  @Expose
  @Getter
  @ValueMapValue
  private boolean autoplay;

  /**
   * Copy Image instrance.
   * @return copy of instance
   */
  public Video copy() {
    Video video = new Video();
    video.setVideoFileReference(videoFileReference);
    video.setMobileVideoReference(mobileVideoReference);
    video.setS7videoFileReference(s7videoFileReference);
    video.setS7mobileVideoReference(s7mobileVideoReference);
    return video;
  }

  /**
   * Sets the file reference.
   *
   * @param videoFileReference the new file reference
   */
  public void setFileReference(String videoFileReference) {
    this.videoFileReference = videoFileReference;
  }
}
