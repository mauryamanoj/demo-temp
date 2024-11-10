package com.saudi.tourism.core.models.components.contentfragment;

import com.saudi.tourism.core.models.common.CFAdapter;
import org.apache.sling.api.adapter.Adaptable;
import org.apache.sling.api.adapter.AdapterFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;

import java.util.List;

@Component(
    service = AdapterFactory.class,
    immediate = true)
public  abstract class CFAdapterFactory<T> implements AdapterFactory {

  @Override
  public <AdapterType> @Nullable AdapterType getAdapter(
      @NotNull Object o, @NotNull Class<AdapterType> aClass) {
    if (o instanceof Adaptable) {
      final var adaptable = (Adaptable) o;
      for (var adapter : getAdapters()) {
        if (adapter.supports(adaptable)) {
          return (AdapterType) adapter.adaptTo(adaptable);
        }
      }
    }
    return null;
  }

  /**
   * Abstract method to be implemented by concrete subclasses that will return the list of available adapters.
   *
   * @return the list of CFAdapter implementations.
   */
  protected abstract List<? extends CFAdapter> getAdapters();


}
