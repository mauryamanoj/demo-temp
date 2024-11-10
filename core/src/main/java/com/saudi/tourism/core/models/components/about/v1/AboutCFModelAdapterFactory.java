package com.saudi.tourism.core.models.components.about.v1;

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
        "com.saudi.tourism.core.models.components.about.v1.AboutCFModelAdapterFactory",
    property = {
        "adaptables=org.apache.sling.api.resource.Resource",
        "adapters=com.saudi.tourism.core.models.components.about.v1.AboutCFModel"
    })
public class AboutCFModelAdapterFactory extends CFAdapterFactory<AboutCFAdapter> {


  /**
   * AboutCFModel adapters.
   */
  @Setter
  @Reference(policy = ReferencePolicy.DYNAMIC)
  private volatile List<AboutCFAdapter> adapters;

  @Override
  protected List<AboutCFAdapter> getAdapters() {
    return adapters;
  }
}
