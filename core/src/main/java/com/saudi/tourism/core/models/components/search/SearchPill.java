package com.saudi.tourism.core.models.components.search;

import com.google.gson.annotations.Expose;
import lombok.Data;

import java.io.Serializable;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * The Search pill model.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class SearchPill implements Serializable {

  /**
   * Variable to store title of pills block.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * Variable to store link.
   */
  @ValueMapValue
  @Expose
  private String id;


  /**
   * type of suggestions.
   */
  private String type;

  /**
   * url of suggestions if direct link for web.
   */
  private String urlWeb;

  /**
   * url of suggestions if direct link for app.
   */
  private String urlApp;

  /**
   * app Type.
   */
  private String appType;



}
