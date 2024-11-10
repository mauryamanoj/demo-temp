package com.saudi.tourism.core.models.nativeApp.page;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
* AppLocale Model.
*/
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class AppLocale {

 /**
  * id .
  */
  @ValueMapValue
  private Integer id;

 /**
  * value .
  */
  @ValueMapValue
  @Expose
  private String value;

 /**
  * code . .
  */
  @ValueMapValue
  @Expose
  private String code;

 /**
  * flag .
  */
  @ValueMapValue
  @Expose
  private String flag;
}
