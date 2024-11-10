package com.saudi.tourism.core.models.components.viewall;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

/**
 * The Class HotelListSlingModel.
 */
@Model(adaptables = {Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ViewAllSlingModel {
  /**
   * The title.
   */
  @Self
  @Getter
  private ViewAllFilterSlingModel viewAllFilterSlingModel;

  /**
   * @return json.
   * The init method.
   */
  @JsonIgnore
  public String getJson() {
    return new Gson().toJson(viewAllFilterSlingModel);
  }

}
