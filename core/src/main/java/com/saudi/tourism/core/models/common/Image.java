package com.saudi.tourism.core.models.common;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;
import java.util.List;

/**
 * Model class of image related data.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image implements Serializable {

  /**
   * Variable to store image fileReference.
   */
  @ValueMapValue
  @Expose
  private String fileReference;

  /**
   * Variable to store mobile Image Reference.
   */
  @ValueMapValue
  @Expose
  private String mobileImageReference;

  /**
   * Variable to store image alternative text (accessibility).
   */
  @ValueMapValue
  @Expose
  private String alt;

  /**
   * Variable to store s7 image fileReference.
   */
  @ValueMapValue(name = "s7fileReference")
  @Expose
  private String s7fileReference;

  /**
   * Variable to store s7 mobile Image Reference.
   */
  @ValueMapValue
  @Expose
  private String s7mobileImageReference;

  /**
   * Variable to store desktopImage.
   */
  @ValueMapValue
  @Expose
  private String desktopImage;

  /**
   * Variable to store mobileImage.
   */
  @ValueMapValue
  @Expose
  private String mobileImage;
  /**
   * Variable to pullDesktopImageFromDAM.
   */
  @ValueMapValue
  @Expose
  private String pullDesktopImageFromDAM;
  /**
   * Variable to pullMobileImageFromDAM.
   */
  @ValueMapValue
  @Expose
  private String pullMobileImageFromDAM;
  /**
   * Variable to special image 768 for Article Cards in Teaser with Cards.
   */
  @ValueMapValue
  @Expose
  private String imageSpecial;
  /**
   * Variable to hold desktop breakpoint.
   */
  private String deskTopBreakpoint;

  /**
   * Variable to hold mobile breakpoint.
   */
  private String mobileBreakpoint;

  /**
   * Variable to hold desktop breakpoints.
   */
  @Expose
  private List<Breakpoint> breakpoints;

  /**
   * Variable to hold default image.
   */
  @Expose
  private String defaultImage;

  /**
   * Variable to special image 768 for Article Cards in Teaser with Cards.
   */
  @ValueMapValue
  @Default(booleanValues = false)
  @Expose
  private boolean isTransparent;

  /**
   * Copy Image instrance.
   * @return copy of instance
   */
  public Image copy() {
    Image image = new Image();
    image.setPullDesktopImageFromDAM(pullDesktopImageFromDAM);
    image.setPullMobileImageFromDAM(pullMobileImageFromDAM);
    image.setFileReference(fileReference);
    image.setMobileImageReference(mobileImageReference);
    image.setAlt(alt);
    image.setS7fileReference(s7fileReference);
    image.setS7mobileImageReference(s7mobileImageReference);
    image.setDesktopImage(desktopImage);
    image.setMobileImage(mobileImage);
    image.setBreakpoints(breakpoints);
    image.setDefaultImage(defaultImage);
    image.setTransparent(isTransparent);
    return image;
  }

  /**
   * Get the is transparent.
   *
   * @return isTransparent
   **/
  public boolean isTransparent() {
    return isTransparent;
  }

  /**
   * Sets the is transparent.
   *
   * @param isTransparent the new is transparent
   */
  public void setTransparent(boolean isTransparent) {
    this.isTransparent = isTransparent;
  }

  /**
   * Sets the file reference.
   *
   * @param fileReference the new file reference
   */
  public void setFileReference(String fileReference) {
    this.fileReference = fileReference;
  }
}
