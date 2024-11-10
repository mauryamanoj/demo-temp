package com.saudi.tourism.core.models.components.packages;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The Class PackageDayModel.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PackageDayModel implements Serializable {

  /**
   * The title.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String title;
  /**
   * The image.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String image;

  /**
   * The Dynamic Media image.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String s7image;

  /**
   * The Dynamic Media image.
   */
  @ValueMapValue
  @Getter
  @Expose
  private boolean enableImageSharpen;

  /**
   * List of links.
   */
  @ChildResource (name = "details")
  @Getter
  private List<PackageInfoItem> items;

  /**
   * List of details.
   */
  @Getter
  @Expose
  private List<String> details;

  /**
   * The init method.
   */
  @PostConstruct protected void init() {
    details = new ArrayList<>();
    for (PackageInfoItem item : items) {
      details.add(item.getText());
    }

    if (Objects.nonNull(image) && image.contains("scth/ugc")) {
      image = image.replace("http://", "https://") + "?scl=1";
    }

  }
}
