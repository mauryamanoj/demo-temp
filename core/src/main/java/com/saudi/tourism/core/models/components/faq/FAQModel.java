package com.saudi.tourism.core.models.components.faq;

import com.day.cq.wcm.api.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;

/**
 * FAQ Model.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
@Data
public class FAQModel implements Serializable {
  /**
   * API_BASE_PATH.
   */
  private static final String API_BASE_PATH = "/bin/api/v1/faq";
  /**
   * QUERY_PARAM_PATH_EQUAL.
   */
  public static final String QUERY_PARAM_PATH_EQUAL = "?path=";

  /**
   * Headline.
   */
  @ValueMapValue
  @Expose
  private String headline;

  /**
   * FAQ Topics.
   */
  @ChildResource
  @Expose
  private List<FAQTopic> items;

  /**
   * Component ID.
   */
  @ValueMapValue
  @Expose
  private String componentId;
  /**
   * current page.
   */
  @ScriptVariable
  private Page currentPage;
  /**
   * api url.
   */
  @Expose
  private String apiUrl;
  /**
   * currentResource.
   */
  @SlingObject
  @JsonIgnore
  private transient Resource currentResource;
  /**
   * fetching apiUrl.
   */
  @PostConstruct
  protected void init() {
    apiUrl = new StringBuilder(API_BASE_PATH).append(QUERY_PARAM_PATH_EQUAL)
          .append(currentResource.getPath()).toString();
  }
  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
