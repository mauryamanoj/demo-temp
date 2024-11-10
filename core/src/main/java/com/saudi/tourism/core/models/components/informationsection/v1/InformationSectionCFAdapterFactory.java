package com.saudi.tourism.core.models.components.informationsection.v1;

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
        "com.saudi.tourism.core.models.components.informationsection.v1.InformationSectionCFAdapterFactory",
    property = {
        "adaptables=org.apache.sling.api.resource.Resource",
        "adapters=com.saudi.tourism.core.models.components.informationsection.v1.InformationSectionCFModel"
    })
public class InformationSectionCFAdapterFactory extends CFAdapterFactory<InformationSectionCFAdapter> {


  /**
   * InformationSectionCFModel adapters.
   */
  @Setter
  @Reference(policy = ReferencePolicy.DYNAMIC)
  private volatile List<InformationSectionCFAdapter> adapters;

  @Override
  protected List<InformationSectionCFAdapter> getAdapters() {
    return adapters;
  }
}
