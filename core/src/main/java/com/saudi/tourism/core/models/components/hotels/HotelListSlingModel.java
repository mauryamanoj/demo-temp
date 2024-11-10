package com.saudi.tourism.core.models.components.hotels;

import com.google.gson.Gson;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;

/**
 * The Class HotelListSlingModel.
 */
@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HotelListSlingModel {
  /**
   * The title.
   */
  @Self
  @Getter
  private HotelFilterSlingModel hotelFilterSlingModel;
  /**
   * The data.
   */
  @Getter
  private String data;

  /**
   * The init method.
   */
  @PostConstruct protected void init() {
    Gson gson = new Gson();
    data = gson.toJson(hotelFilterSlingModel);
  }

}
