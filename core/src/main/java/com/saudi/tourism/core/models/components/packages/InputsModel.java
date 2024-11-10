package com.saudi.tourism.core.models.components.packages;

import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import java.io.Serializable;

/**
 * The Class InputModel for input dialog mapping.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Setter
public class InputsModel implements Serializable {

  /**
   * The firstName.
   */
  @ChildResource
  @Getter
  private InputModel firstName;
  /**
   * The lastName.
   */
  @ChildResource
  @Getter
  private InputModel lastName;
  /**
   * The email.
   */
  @ChildResource
  @Getter
  private InputModel email;
  /**
   * The phone.
   */
  @ChildResource
  @Getter
  private PhoneInputModel phone;
  /**
   * The nationality.
   */
  @ChildResource
  @Getter
  private InputModel nationality;
  /**
   * The comment.
   */
  @ChildResource
  @Getter
  private InputModel comment;

}
