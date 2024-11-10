package com.saudi.tourism.core.services.filters.v1;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * The item filter options.
 */
@Model(adaptables = Resource.class,
    cache = true,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class FiltersItemFilterModel implements Serializable {

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
  private List<FiltersIdValueModel> data;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FiltersItemFilterModel that = (FiltersItemFilterModel) o;
    return Objects.equals(key, that.key);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key);
  }
}
