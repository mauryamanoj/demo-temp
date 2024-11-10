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
 * NativeAppVersion Model.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class NativeAppVersion implements Serializable {

  /**
   * AppVersion List .
   */
  @ChildResource(name = "appVersion")
  @Expose
  @Getter
  private List<AppVersion> appVersion;
}
