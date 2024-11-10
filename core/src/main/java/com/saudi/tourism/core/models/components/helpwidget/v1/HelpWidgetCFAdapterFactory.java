package com.saudi.tourism.core.models.components.helpwidget.v1;

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
        "com.saudi.tourism.core.models.components.helpwidget.v1.HelpWidgetCFAdapterFactory",
    property = {
        "adaptables=org.apache.sling.api.resource.Resource",
        "adapters=com.saudi.tourism.core.models.components.helpwidget.v1.HelpWidgetCFModel"
    })
public class HelpWidgetCFAdapterFactory extends CFAdapterFactory<HelpWidgetCFAdapter> {


  /**
   * HelpWidgetCFAdapter adapters.
   */
  @Setter
  @Reference(policy = ReferencePolicy.DYNAMIC)
  private volatile List<HelpWidgetCFAdapter> adapters;

  @Override
  protected List<HelpWidgetCFAdapter> getAdapters() {

    return adapters;
  }
}
