package com.saudi.tourism.core.models.app.page;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;
/**
 * AppPageInfo.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppPageInfo implements Serializable {

  /**
   * path.AppPageInfo .
   */
  @Expose
  private String path;

  /**
   * id.
   */
  @Expose
  private String id;

  /**
   * type.
   */
  @Expose
  @ValueMapValue
  private String type;

  /**
   * title.
   */
  @Expose
  private String title;
  /**
   * shortDescription.
   */
  @Expose
  private String shortDescription;

  /**
   * featuredImage.
   */
  @Expose
  private String featuredImage;

}
