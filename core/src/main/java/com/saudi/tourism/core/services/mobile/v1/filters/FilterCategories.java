package com.saudi.tourism.core.services.mobile.v1.filters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.components.IdValueModel;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.util.stream.Collectors;

@Model(
    adaptables = Resource.class,
    cache = true,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class FilterCategories implements Serializable {
  /** id. */
  @ValueMapValue(name = "key")
  @Expose
  private String id;

  /** title. */
  @ValueMapValue(name = "name")
  @Expose
  private String title;

  /** selection type. */
  @Expose private String selectionType = "multiple";

  /** filterComponentStyle. */
  @Expose private FilterComponentStyle filterComponentStyle = new FilterComponentStyle();

  /** Filter area options. */
  @ChildResource
  @JsonIgnore
  private List<IdValueModel> areaData;

  /** min price range. */
  @ValueMapValue
  @JsonIgnore
  private String minPriceRange;

  /** max price range. */
  @ValueMapValue
  @JsonIgnore
  private String maxPriceRange;

  /** items. */
  @Expose private List<FiltersIdTitleModel> items;

  @PostConstruct
  void init() {
    if (id.equals("categories") || id.equals("destinations")) {
      filterComponentStyle.setComponentUIType("MULTIPLE_TEXT_CHOICES");
    }

    if (id.equals("seasons")) {
      filterComponentStyle.setComponentUIType("TAG");
    }

    if (id.equals("date")) {
      filterComponentStyle.setComponentUIType("DATE");
    }

    if (id.equals("discounted")) {
      filterComponentStyle.setComponentUIType("SINGLE_TEXT_CHOICE");
    }

    if (id.equals("city")) {
      setId("destinations");
      filterComponentStyle.setComponentUIType("MULTIPLE_TEXT_CHOICES");
      if (CollectionUtils.isNotEmpty(areaData)) {
        items =
            areaData.stream()
                .filter(Objects::nonNull)
                .map(
                    city -> {
                      FiltersIdTitleModel idTitleModel = new FiltersIdTitleModel();
                      idTitleModel.setId(city.getId());
                      idTitleModel.setTitle(city.getValue());
                      idTitleModel.setIconUrl(StringUtils.EMPTY);
                      return idTitleModel;
                    })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
      }
    }
    if (id.equals("minPrice,maxPrice")) {
      filterComponentStyle.setComponentUIType("MULTIPLE_TEXT_CHOICES");
      List<FiltersIdTitleModel> priceDataList = new ArrayList<>();
      FiltersIdTitleModel minPriceData = new FiltersIdTitleModel();
      minPriceData.setId("minPrice");
      if (null != minPriceRange) {
        minPriceData.setValue(minPriceRange);
      } else {
        minPriceData.setValue("0");
      }
      priceDataList.add(minPriceData);
      FiltersIdTitleModel maxPriceData = new FiltersIdTitleModel();
      maxPriceData.setId("maxPrice");
      if (null != maxPriceRange) {
        maxPriceData.setValue(maxPriceRange);
      } else {
        maxPriceData.setValue("3000");
      }
      priceDataList.add(maxPriceData);
      setItems(priceDataList);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FilterCategories that = (FilterCategories) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  /** Inner class representing the filter component style. */
  @Data
  public static class FilterComponentStyle {

    /** componentUIType. */
    private String componentUIType;
  }
}
