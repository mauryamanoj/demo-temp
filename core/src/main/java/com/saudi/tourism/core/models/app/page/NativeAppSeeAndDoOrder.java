package com.saudi.tourism.core.models.app.page;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.nativeApp.page.HomeSections;
import lombok.Data;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import java.io.Serializable;
import java.util.List;

/**
 * NativeAppSeeAndDoOrder model.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class NativeAppSeeAndDoOrder implements Serializable {
  /**
   * seeAndDo.
   */
  @ChildResource(name = "homeSections")
  @Expose
  @Getter
  private List<HomeSections> seeAndDo;

  /**
   * attraction types.
   */
  @Expose
  @Getter
  @ChildResource
  private List<AttractionItem> attractionTypes;



}
