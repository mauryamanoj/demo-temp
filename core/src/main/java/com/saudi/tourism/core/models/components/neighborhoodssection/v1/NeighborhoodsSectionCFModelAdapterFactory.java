package com.saudi.tourism.core.models.components.neighborhoodssection.v1;

import java.util.List;

import com.saudi.tourism.core.models.components.contentfragment.CFAdapterFactory;
import lombok.Setter;
import org.apache.sling.api.adapter.AdapterFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(
    service = AdapterFactory.class,
    immediate = true,
    configurationPid =
      "com.saudi.tourism.core.models.components.neighborhoodssection.v1.NeighborhoodsSectionCFModelAdapterFactory",
    property = {
      "adaptables=org.apache.sling.api.resource.Resource",
      "adapters=com.saudi.tourism.core.models.components.neighborhoodssection.v1.NeighborhoodsSectionCard"
  })
public class NeighborhoodsSectionCFModelAdapterFactory extends CFAdapterFactory<NeighborhoodsSectionCFAdapter> {

  /**
   * NeighborhoodsSectionCFAdapters adapters.
   */
  @Setter
  @Reference(policy = ReferencePolicy.DYNAMIC)
  private volatile List<NeighborhoodsSectionCFAdapter> adapters;

  @Override
  protected List<NeighborhoodsSectionCFAdapter> getAdapters() {
    return adapters;
  }
}
