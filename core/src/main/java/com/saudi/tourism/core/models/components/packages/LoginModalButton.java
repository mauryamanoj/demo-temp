package com.saudi.tourism.core.models.components.packages;


import com.google.gson.annotations.Expose;
import lombok.Data;

@Data
public class LoginModalButton {
  /**
   * Copy text.
   */
  @Expose
  private String copy;

  /**
   * button Link.
   */
  @Expose
  private String link;
}
