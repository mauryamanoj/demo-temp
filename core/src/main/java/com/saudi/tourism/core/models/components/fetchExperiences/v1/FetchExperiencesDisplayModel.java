package com.saudi.tourism.core.models.components.fetchExperiences.v1;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/** Things ToDo Cards Display Model. */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@With
public class FetchExperiencesDisplayModel {
  /**
   * Display: Card size.
   */
  @Expose
  @ValueMapValue
  private String cardSize;

  /**
   * Display: Display Type.
   */
  @Expose
  @ValueMapValue
  private String displayType;

  /**
   * Display: Number of Cards.
   */
  @Expose
  @ValueMapValue
  private Integer numberOfCards;
}
