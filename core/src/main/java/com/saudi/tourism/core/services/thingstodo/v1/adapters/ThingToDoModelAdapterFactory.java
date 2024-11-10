package com.saudi.tourism.core.services.thingstodo.v1.adapters;

import com.saudi.tourism.core.models.common.CFAdapter;
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
        "com.saudi.tourism.core.services.thingstodo.v1.adapters.ThingToDoModelAdapterFactory",
    property = {
      "adaptables=org.apache.sling.api.resource.Resource",
      "adapters=com.saudi.tourism.core.services.thingstodo.v1.ThingToDoModel"
    })
/** Things ToDo Adapter Factory. */
public class ThingToDoModelAdapterFactory extends CFAdapterFactory<ThingToDoCFAdapter> {

  /**
   * ThingsToDoModel adapters.
   */
  @Setter
  @Reference(policy = ReferencePolicy.DYNAMIC)
  private volatile List<ThingToDoCFAdapter> adapters;

  @Override
  protected List<? extends CFAdapter> getAdapters() {
    return adapters;
  }
}
