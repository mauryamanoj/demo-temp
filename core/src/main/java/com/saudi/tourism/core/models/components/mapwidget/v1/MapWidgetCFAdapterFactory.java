package com.saudi.tourism.core.models.components.mapwidget.v1;

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
        "com.saudi.tourism.core.models.components.mapwidget.v1.MapWidgetCFAdapterFactory",
    property = {
        "adaptables=org.apache.sling.api.resource.Resource",
        "adapters=com.saudi.tourism.core.models.components.mapwidget.v1.MapWidgetCFModel"
    })
public class MapWidgetCFAdapterFactory extends CFAdapterFactory<MapWidgetCFAdapter> {


  /**
   * MapWidgetCFAdapter adapters.
   */
  @Setter
  @Reference(policy = ReferencePolicy.DYNAMIC)
  private volatile List<MapWidgetCFAdapter> adapters;

  @Override
  protected List<MapWidgetCFAdapter> getAdapters() {

    return adapters;
  }
}