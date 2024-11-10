package com.saudi.tourism.core.models.components.informationlistwidget.v1;

import com.saudi.tourism.core.models.components.contentfragment.CFAdapterFactory;
import java.util.List;
import lombok.Setter;
import org.apache.sling.api.adapter.AdapterFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(
    service = AdapterFactory.class,
    immediate = true,
    configurationPid =
        "com.saudi.tourism.core.models.components.informationlistwidget.v1.InformationListWidgetCFAdapterFactory",
    property = {
        "adaptables=org.apache.sling.api.resource.Resource",
        "adapters=com.saudi.tourism.core.models.components.informationlistwidget.v1.InformationListWidgetCFModel"
    })
public class InformationListWidgetCFAdapterFactory extends CFAdapterFactory<InformationListWidgetCFAdapter> {


  /**
   * InformationListWidgetCFAdapter adapters.
   */
  @Setter
  @Reference(policy = ReferencePolicy.DYNAMIC)
  private volatile List<InformationListWidgetCFAdapter> adapters;

  @Override
  protected List<InformationListWidgetCFAdapter> getAdapters() {

    return adapters;
  }
}
