package com.saudi.tourism.core.models.app.entertainer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.models.app.location.PolygonCoordinatesModel;
import com.saudi.tourism.core.utils.CommonUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;

/**
 * Entertainer City model.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class EntertainerCityModel implements Serializable {
  /**
   * The the image path.
   */
  @ValueMapValue
  @Getter
  @Setter
  private String image;

  /**
   * The Entertainer locationId.
   */
  @ValueMapValue
  @Getter
  @Setter
  private String locationId;

  /**
   * disableEntertainer.
   */
  @ValueMapValue
  @Getter
  @Setter
  private boolean disableEntertainer;

  /**
   * coordinates.
   */
  @Getter
  @Setter
  @ChildResource
  private List<PolygonCoordinatesModel> coordinates;

  /**
   * coordinates.
   */
  @Getter
  private String cityId;

  /**
   * List of destination tags wrapped into the CategoryTag object.
   *
   */
  @ValueMapValue
  @Getter
  @Setter
  private String[] destinationFeatureTags;

  /**
   * CurrentResource.
   */
  @Self
  @JsonIgnore
  private transient Resource currentResource;

  /**
   * init method of sling model.
   */
  @PostConstruct
  protected void init() {
    cityId = currentResource.getName();
  }

  /**
   * The Entertainer city latitude.
   */
  @ValueMapValue
  @Getter
  @Setter
  private String latitude;
  /**
   * The Entertainer city longitude.
   */
  @ValueMapValue
  @Getter
  @Setter
  private String longitude;
  /**
   * The Entertainer city radius.
   */
  @ValueMapValue
  @Getter
  @Setter
  private String radius;



  /**
   * Return localized destinationFeatureTags.
   *
   * @param locale locale.
   * @return localizedDestinationFeatureTags
   */
  public List<String> getLocalizedDestinationFeatureTags(String locale) {
    List<String> destinationTags =
        CommonUtils.getCategoryFromTagName(destinationFeatureTags, currentResource.getResourceResolver(), locale);
    return destinationTags;
  }

}
