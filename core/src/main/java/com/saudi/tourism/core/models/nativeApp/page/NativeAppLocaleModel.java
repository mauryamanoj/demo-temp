package com.saudi.tourism.core.models.nativeApp.page;

import com.google.gson.annotations.Expose;
import lombok.Data;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import java.io.Serializable;
import java.util.List;

/**
 * NativeAppLocale Model.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class NativeAppLocaleModel implements Serializable {

  /**
   * AppLocale List .
   */
  @ChildResource(name = "appLocale")
  @Expose
  @Getter
  private List<AppLocale> appLocale;
}
