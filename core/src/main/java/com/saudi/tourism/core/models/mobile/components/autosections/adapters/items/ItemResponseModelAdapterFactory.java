package com.saudi.tourism.core.models.mobile.components.autosections.adapters.items;

import lombok.Setter;
import org.apache.sling.api.adapter.Adaptable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;

import java.util.List;

@Component(service = ItemResponseModelAdapterFactory.class, immediate = true)
public class ItemResponseModelAdapterFactory {

  /**
   * adapters list.
   */
  @Setter
  @Reference(policy = ReferencePolicy.DYNAMIC)
  private volatile List<ItemResponseModelAdapter> adapters;

  public <AdapterType> @Nullable AdapterType getAdapter(@NotNull Object o, @NotNull Class<AdapterType> aClass) {
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
