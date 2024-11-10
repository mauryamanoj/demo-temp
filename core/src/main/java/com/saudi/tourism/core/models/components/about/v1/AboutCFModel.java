package com.saudi.tourism.core.models.components.about.v1;

import com.google.gson.annotations.Expose;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/** About CF Model. */
@Getter
@Builder
public class AboutCFModel {
  /**
   * Title.
   */
  @Getter
  private String aboutTitle;

  /**
   * Description.
   */
  @Getter
  private String aboutDescription;

  /**
   * Categories Icon.
   */
  @Getter
  private List<String> categoriesIcon;

  /**
   * hideFavorite.
   */
  @Expose
  private boolean hideFavorite;

  /**
   * readMoreLabel.
   */
  @Expose
  private String readMoreLabel;

  /**
   * readLessLabel.
   */
  @Expose
  private String readLessLabel;

}
