package com.saudi.tourism.core.models.components.faq.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;

/**
 * FAQ Model.
 */
@Model(adaptables = {Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

@Data
public class FAQModel {

  /** The saudi tourism config. */
  @OSGiService
  private SaudiTourismConfigs saudiTourismConfig;

  /**
   * Headline.
   */
  @ValueMapValue
  @Expose
  private String headline;

  /**
   * Component ID.
   */
  @ValueMapValue
  @Expose
  private String componentId;

  /**
   * The link.
   */
  @ChildResource
  @Expose
  private Link link;

  /**
   * FAQ: categories endpoint.
   */
  @Expose
  private String faqCategoriesEndpoint;

  /**
   * FAQ: articles endpoint.
   */
  @Expose
  private String faqArticlesEndpoint;

  /**
   * The init method.
   */
  @PostConstruct
  protected void init() {
    faqCategoriesEndpoint = saudiTourismConfig.getFaqCategoriesEndpoint();
    faqArticlesEndpoint = saudiTourismConfig.getFaqArticlesEndpoint();
  }

  /**
   * getJson method for account component.
   *
   * @return json representation.
   */
  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
