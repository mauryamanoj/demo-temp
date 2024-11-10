package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.customAuthorDetails.bookmarks;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * Bookmarks POJO.
 */
public class Bookmarks {
  /**
   * drive.
   */
  @SerializedName("drive")
  @Expose
  @Getter
  @Setter
  private String drive;

  /**
   * finished .
   */
  @SerializedName("finished")
  @Expose
  @Getter
  @Setter
  private String finished;

  /**
   * unlock.
   */
  @SerializedName("unlock")
  @Expose
  @Getter
  @Setter
  private String unlock;
}
