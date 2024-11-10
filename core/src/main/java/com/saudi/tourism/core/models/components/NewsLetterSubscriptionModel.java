package com.saudi.tourism.core.models.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import lombok.Data;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
/**
 * News Letter Subscription Model.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Data
public class NewsLetterSubscriptionModel {
  /** stayUpToDate label. */
  @ValueMapValue
  @Expose
  private String stayUpToDate;

  /** subscribeText label. */
  @ValueMapValue
  @Expose
  private String subscribeText;

  /** subscribeButtonText label. */
  @ValueMapValue
  @Expose
  private String subscribeButtonText;

  /** emailInputPlaceholder label. */
  @ValueMapValue
  @Expose
  private String emailInputPlaceholder;

  /** invalidEmailMessage label. */
  @ValueMapValue
  @Expose
  private String invalidEmailMessage;

  /** successMessage label. */
  @ValueMapValue
  @Expose
  private String successMessage;

  /** errorMessage label. */
  @ValueMapValue
  @Expose
  private String errorMessage;

  /** email subscription api url. */
  @Expose
  private String apiUrl = "/bin/api/v1/newsletter";

  /**
   * getJson method for account component.
   *
   * @return json representation.
   */
  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
