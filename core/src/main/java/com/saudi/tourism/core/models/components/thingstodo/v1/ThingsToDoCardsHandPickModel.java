package com.saudi.tourism.core.models.components.thingstodo.v1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.List;

/** Things ToDo Cards Hand Pick Model. */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@With
public class ThingsToDoCardsHandPickModel {

  /**
   * Things ToDo.
   */
  @ValueMapValue
  @Expose
  @SerializedName("cfPaths")
  private List<String> thingsToDoCFPaths;
}
