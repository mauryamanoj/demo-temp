package com.saudi.tourism.core.models.components;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The roadtrip filter options.
 */
@Model(adaptables = Resource.class,
    cache = true,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class RoadTripFilterModel implements Serializable {

  /**
   * Filter name.
   */
  @ValueMapValue
  @Expose
  private String name;

  /**
   * Filter key.
   */
  @ValueMapValue
  @Expose
  private String key;

  /**
   * Filter type.
   */
  @ValueMapValue
  @Expose
  private String type;

  /**
   * filter data.
   */
  @Expose
  private List<IdValueModel> data;

  /**
   * min distance.
   */
  @ValueMapValue
  private String minDistance;

  /**
   * max distance.
   */
  @ValueMapValue
  private String maxDistance;

  /**
   * init method.
   */
  @PostConstruct
  protected void init() {
    if (getKey().equals("minDistance,maxDistance")) {
      List<IdValueModel> distanceDataList = new ArrayList<>();
      IdValueModel minDistanceData = new IdValueModel();
      minDistanceData.setId("minDistance");
      if (null != minDistance) {
        minDistanceData.setValue(minDistance);
      } else {
        minDistanceData.setValue("0");
      }
      distanceDataList.add(minDistanceData);
      IdValueModel maxDistanceData = new IdValueModel();
      maxDistanceData.setId("maxDistance");
      if (null != maxDistance) {
        maxDistanceData.setValue(maxDistance);
      } else {
        maxDistanceData.setValue("500");
      }
      distanceDataList.add(maxDistanceData);
      setData(distanceDataList);
    }
  }
}
