package com.saudi.tourism.core.models.app.page;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.inject.Named;
import java.io.Serializable;

/**
 * Component text model.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@ToString
public class ComponentInfo implements Serializable {

  /**
   * Type.
   */
  @Getter
  @Setter
  @ValueMapValue
  private String type;

  /**
   * Text.
   */
  @Getter
  @Setter
  @ValueMapValue
  private String text;

  /**
   * Image.
   */
  @Getter
  @Setter
  @ValueMapValue
  @Named("fileReference")
  private String image;

  /**
   * Size.
   */
  @Getter
  @Setter
  @ValueMapValue
  private String size;

}
