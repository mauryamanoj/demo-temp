package com.saudi.tourism.core.models.components.header;

import lombok.Setter;
import org.apache.sling.api.adapter.Adaptable;
import org.apache.sling.api.adapter.AdapterFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;

import java.util.List;


@Component(
    service = AdapterFactory.class,
    immediate = true,
    configurationPid =
      "com.saudi.tourism.core.models.components.header.HeaderSectionCFAdapterFactory",
    property = {
      "adaptables=org.apache.sling.api.resource.Resource",
      "adapters=com.saudi.tourism.core.models.components.header.HeaderSectionCFModel"
    })
public class HeaderSectionCFAdapterFactory implements AdapterFactory {

  /**
   * HeaderSectionCFCFModel adapters.
   */
  @Setter
  @Reference(policy = ReferencePolicy.DYNAMIC)
  private volatile List<HeaderSectionCFAdapter> adapters;

  @Override
  public <AdapterType> @Nullable AdapterType getAdapter(
      @NotNull Object o, @NotNull Class<AdapterType> aClass) {
    if (o instanceof Adaptable) {
      final var adaptable = (Adaptable) o;
      for (var adapter : adapters) {
        if (adapter.supports(adaptable)) {
          return (AdapterType) adapter.adaptTo(adaptable);
        }
      }
    }
    return null;
  }
}
