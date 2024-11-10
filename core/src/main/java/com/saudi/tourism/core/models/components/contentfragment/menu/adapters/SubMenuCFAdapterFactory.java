package com.saudi.tourism.core.models.components.contentfragment.menu.adapters;

import com.saudi.tourism.core.models.components.contentfragment.CFAdapterFactory;
import java.util.List;
import lombok.Setter;
import org.apache.sling.api.adapter.AdapterFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;

/** SubMenu CF Adapter factory. */
@Component(
    service = AdapterFactory.class,
    immediate = true,
    configurationPid =
        "com.saudi.tourism.core.models.components.contentfragment.menu.adapters.SubMenuCFAdapterFactory",
    property = {
      "adaptables=org.apache.sling.api.resource.Resource",
      "adapters=com.saudi.tourism.core.models.components.contentfragment.menu.SubMenuCF"
    })
public class SubMenuCFAdapterFactory extends CFAdapterFactory<SubMenuCFAdapter> {

  /** SubMenuCFModel adapters. */
  @Setter
  @Reference(policy = ReferencePolicy.DYNAMIC)
  private volatile List<SubMenuCFAdapter> adapters;

  @Override
  protected List<SubMenuCFAdapter> getAdapters() {
    return adapters;
  }
}
