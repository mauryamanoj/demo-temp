package com.saudi.tourism.core.services.article.v1;

import com.google.gson.annotations.Expose;
import lombok.Builder;
import lombok.Data;
import lombok.With;

@Builder
@Data
@With
public class ArticleTag {

  /**
   * Tag Id.
   */
  @Expose
  private String id;

  /**
   * Label for the tag, localized.
   */
  @Expose
  private String title;
}
