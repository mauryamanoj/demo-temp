package com.saudi.tourism.core.services.filters.v1;

import java.io.Serializable;
import java.util.Objects;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.components.IdValueModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * This class contains the id value details.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FiltersIdValueModel extends IdValueModel implements Serializable {

  /**
   * id.
   */
  @ValueMapValue
  @Expose
  private String id;

  /**
   * value.
   */
  @ValueMapValue
  @Expose
  private String value;

  /**
   * icon.
   */
  @ValueMapValue
  @Expose
  private String icon;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FiltersIdValueModel that = (FiltersIdValueModel) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

}
