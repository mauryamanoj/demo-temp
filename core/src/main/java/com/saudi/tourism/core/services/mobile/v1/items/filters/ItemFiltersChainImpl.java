package com.saudi.tourism.core.services.mobile.v1.items.filters;

import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.servlets.mobile.v1.MobileRequestParams;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;

import java.util.List;

/** Section Filter chain implementation. */
@Component(service = ItemFiltersChain.class, immediate = true)
public class ItemFiltersChainImpl implements ItemFiltersChain {

  /** Section Filters. */
  @Setter
  @Reference(policy = ReferencePolicy.DYNAMIC)
  private volatile List<ItemFilter> filters;

  @Override
  public Boolean doFilter(
          final @NonNull MobileRequestParams request, final @NonNull ItemResponseModel item) {

    // If one of the filters returned false
    // Let's stop, this section will not be taken
    if (CollectionUtils.isNotEmpty(filters)) {
      for (ItemFilter filter : filters) {
        if (!filter.meetFilter(request, item)) {
          return Boolean.FALSE;
        }
      }
    }

    return Boolean.TRUE;
  }
}
