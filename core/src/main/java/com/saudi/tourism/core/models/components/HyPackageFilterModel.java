package com.saudi.tourism.core.models.components;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Halayalla packages filter options.
 */
@Model(adaptables = Resource.class,
    cache = true,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class HyPackageFilterModel implements Serializable {

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
   * Filter area options.
   */
  @ChildResource
  private List<IdValueModel> areaData;

  /**
   * filter data.
   */
  @Expose
  private List<IdValueModel> data;

  /**
   * min price range.
   */
  @ValueMapValue
  private String minPriceRange;

  /**
   * max price range.
   */
  @ValueMapValue
  private String maxPriceRange;

  /**
   * init method.
   */
  @PostConstruct
  protected void init() {
    if (getKey().equals("city")) {
      setData(getAreaData());
    }
    if (getKey().equals("minPrice,maxPrice")) {
      List<IdValueModel> priceDataList = new ArrayList<>();
      IdValueModel minPriceData = new IdValueModel();
      minPriceData.setId("minPrice");
      if (null != minPriceRange) {
        minPriceData.setValue(minPriceRange);
      } else {
        minPriceData.setValue("0");
      }
      priceDataList.add(minPriceData);
      IdValueModel maxPriceData = new IdValueModel();
      maxPriceData.setId("maxPrice");
      if (null != maxPriceRange) {
        maxPriceData.setValue(maxPriceRange);
      } else {
        maxPriceData.setValue("3000");
      }
      priceDataList.add(maxPriceData);
      setData(priceDataList);
    }
  }
}
