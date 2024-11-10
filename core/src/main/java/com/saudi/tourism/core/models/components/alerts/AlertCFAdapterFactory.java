package com.saudi.tourism.core.models.components.alerts;

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
      "com.saudi.tourism.core.models.components.alerts.AlertCFAdapterFactory",
    property = {
      "adaptables=org.apache.sling.api.resource.Resource",
      "adapters=com.saudi.tourism.core.models.components.alerts.AlertCFModel"
    })
public class AlertCFAdapterFactory extends CFAdapterFactory<AlertCFAdapter> {

  /**
   * AlertCFModel adapters.
   */
  @Setter
  @Reference(policy = ReferencePolicy.DYNAMIC)
  private volatile List<AlertCFAdapter> adapters;


  @Override
  protected List<AlertCFAdapter> getAdapters() {
    return adapters;
  }
}
