package com.saudi.tourism.core.models.components.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * The Class ButtonsModel.
 */
@Setter
public class ButtonsModel implements Serializable {

  /**
   * The showAllLabel.
   */
  @Getter
  @SerializedName(value = "showAllLabel")
  @Expose
  private String showAllLabel;

  /**
   * The showLessLabel.
   */
  @Getter
  @SerializedName(value = "showLessLabel")
  @Expose
  private String showLessLabel;
}
