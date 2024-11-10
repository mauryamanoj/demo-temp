package com.saudi.tourism.core.models.app.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.io.Serializable;

/**
 * AppPage model.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class AppPage implements Serializable {
  /**
   * URL.
   */
  @ValueMapValue
  @Expose
  private String url;

  /**
   * URL.
   */
  @ValueMapValue
  @Expose
  private String path;

  /**
   * id.
   */
  @Expose
  @Setter
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
  @ValueMapValue
  private String title;

  /**
   * subTitle.
   */
  @Expose
  @ValueMapValue
  @SerializedName("shortDescription")
  private String subTitle;

  /**
   * backgroundImage.
   */
  @Expose
  @ValueMapValue
  @SerializedName("featuredImage")
  private String backgroundImage;

  /**
   * cta title.
   */
  @Expose
  @ValueMapValue
  private String ctaTitle;

  /**
   * init method.
   */
  @PostConstruct
  protected void init() {
    if (url != null) {
      setId(url);
    }
  }
}
