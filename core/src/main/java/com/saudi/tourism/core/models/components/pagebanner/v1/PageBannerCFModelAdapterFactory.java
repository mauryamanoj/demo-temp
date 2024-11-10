package com.saudi.tourism.core.models.components.pagebanner.v1;

import com.saudi.tourism.core.models.components.contentfragment.CFAdapterFactory;
import lombok.Setter;
import org.apache.sling.api.adapter.AdapterFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;

import java.util.List;

@Component(
    service = AdapterFactory.class,
    immediate = true,
    configurationPid =
        "com.saudi.tourism.core.models.components.pagebanner.v1.PageBannerCFModelAdapterFactory",
    property = {
        "adaptables=org.apache.sling.api.resource.Resource",
        "adapters=com.saudi.tourism.core.models.components.pagebanner.v1.PageBannerCFModel"
    })
public class PageBannerCFModelAdapterFactory extends CFAdapterFactory<PageBannerCFAdapter> {


  /**
   * PageBannerCFAdapter adapters.
   */
  @Setter
  @Reference(policy = ReferencePolicy.DYNAMIC)
  private volatile List<PageBannerCFAdapter> adapters;

  @Override
  protected List<PageBannerCFAdapter> getAdapters() {
    return adapters;
  }
}
