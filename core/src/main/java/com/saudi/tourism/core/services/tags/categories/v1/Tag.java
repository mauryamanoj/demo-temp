package com.saudi.tourism.core.services.tags.categories.v1;

import com.google.gson.annotations.Expose;
import lombok.Builder;
import lombok.Data;
import lombok.With;

@Builder
@Data
@With
public class Tag {

  /**
   * Tag title.
   */
  @Expose
  private String title;

  /**
   * Tag code title.
   */
  @Expose
  private String path;


}
