package com.sta.core.vendors.models;

import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;

import com.google.gson.Gson;

/**
 * Package entry model.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class PackageEntry implements Serializable {

  /**
   * Package name in English.
   */
  @ValueMapValue
  private String packageName;

  /**
   * ID.
   */
  private String id;

  /**
   * Title in English.
   */
  @ValueMapValue
  private String title;

  /**
   * Title in Arabic.
   */
  @ValueMapValue
  private String title_ar;

  /**
   * Description in English.
   */
  @ValueMapValue
  private String copy;

  /**
   * Description in Arabic.
   */
  @ValueMapValue
  private String copy_ar;

  /**
   * Itinerary details in English.
   */
  @ValueMapValue
  private String itineraryDetails;

  /**
   * Important Info in English.
   */
  @ValueMapValue
  private String importantInfo;

  /**
   * Important Info in Arabic.
   */
  @ValueMapValue
  private String importantInfo_ar;

  /**
   * Package include in English.
   */
  @ValueMapValue
  private String include;

  /**
   * Package include in Arabic.
   */
  @ValueMapValue
  private String include_ar;

  /**
   * Package exclude in English.
   */
  @ValueMapValue
  private String exclude;

  /**
   * Package exclude in Arabic.
   */
  @ValueMapValue
  private String exclude_ar;

  /**
   * City.
   */
  @ValueMapValue
  private String[] packageAreaTags;

  /**
   * Duration.
   */
  @ValueMapValue
  private String durationAuth;

  /**
   * Category.
   */
  @ValueMapValue
  private String[] packageCategoryTags;

  /**
   * packageTargetTags.
   */
  @ValueMapValue
  private String[] packageTargetTags;

  /**
   * Banner image.
   */
  @ValueMapValue
  private String bannerImage;

  /**
   * Price per person.
   */
  @ValueMapValue
  private String price;


  /**
   * book now in English.
   */
  @ValueMapValue
  private String bookNow;

  /**
   * book now in Arabic.
   */
  @ValueMapValue
  private String bookNow_ar;

  /**
   * The Current resource.
   */
  @Self
  private transient Resource currentResource;

  /**
   * init method for slingModel.
   */
  @PostConstruct
  private void init() {
    this.id = currentResource.getName();

    if (Objects.nonNull(bannerImage)) {
      Gson gson = new Gson();
      ImageEntry image = gson.fromJson(bannerImage, ImageEntry.class);
      if (!image.getValue().contains("ugc")) {
        image.setValue(image.getValue() + ":crop-667x375?defaultImage=progress&wid=667&" + System
            .currentTimeMillis());
      }
      bannerImage = gson.toJson(image);
    }
  }
}
