package com.saudi.tourism.core.models.components;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.components.cards.CardDetail;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.List;


/**
 * Tab details model.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class TabDetailsModel {

  /**
   * Tab title.
   */
  @Expose
  @ValueMapValue
  private String title;

  /**
   * Tab link.
   */
  @Expose
  @ValueMapValue
  private String link;

  /**
   * Tab content type.
   */
  @Expose
  @ValueMapValue
  private String type;

  /**
   * The data.
   */
  @Expose
  private List<CardDetail> data;

  /**
   * The Article path.
   */
  @ValueMapValue
  private String articlePath;
}
