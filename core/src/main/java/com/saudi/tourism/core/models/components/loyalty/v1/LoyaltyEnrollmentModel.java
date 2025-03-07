package com.saudi.tourism.core.models.components.loyalty.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

/**
 * Loyalty Enrollment Model.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class LoyaltyEnrollmentModel {
  /**
   * Query parameter.
   */
  @ChildResource
  @Expose
  private QueryParameterModel queryParameter;

  /**
   * Login Modal.
   */
  @ChildResource
  @Expose
  private LoginModalModel loginModal;

  /**
   * Enrollment Modal.
   */
  @ChildResource
  @Expose
  private EnrollmentModalModel enrollmentModal;

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
