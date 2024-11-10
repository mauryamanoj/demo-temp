package com.saudi.tourism.core.models.components.loyalty.v1;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.login.servlets.SSIDLoyaltyEnrollmentServlet;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * EnrollmentModal Model.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class EnrollmentModalModel {
  /**
   * Already Enrolled Message.
   */
  @ValueMapValue
  @Expose
  private String alreadyEnrolledMessage;

  /**
   * Successful Enrollment Message.
   */
  @ValueMapValue
  @Expose
  private String successfullyEnrolledMessage;

  /**
   * Unsuccessful Enrollment Message.
   */
  @ValueMapValue
  @Expose
  private String unsuccessfullyEnrolledMessage;

  /**
   * OK Label.
   */
  @ValueMapValue
  @Expose
  private String copy;


  /**
   * button Link.
   */
  @Expose
  private String apiEndpoint = SSIDLoyaltyEnrollmentServlet.SERVLET_PATH;
}
