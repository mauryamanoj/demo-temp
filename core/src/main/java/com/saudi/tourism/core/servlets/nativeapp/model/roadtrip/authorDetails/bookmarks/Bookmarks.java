package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.bookmarks;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

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
  private String drive;

  /**
   * finished .
   */
  @SerializedName("finished")
  @Expose
  @Getter
  private String finished;

  /**
   * unlock.
   */
  @SerializedName("unlock")
  @Expose
  @Getter
  private String unlock;
}
