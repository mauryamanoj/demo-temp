package com.saudi.tourism.core.services.mobile.v1.items.filters;

import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.servlets.mobile.v1.MobileRequestParams;
import com.saudi.tourism.core.utils.Constants;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;

/** EndDate filter for items. */
@Component(service = ItemFilter.class, immediate = true)
@Slf4j
public class EndDateFilter implements ItemFilter {
  @Override
  public Boolean meetFilter(
          final @NonNull MobileRequestParams request, final @NonNull ItemResponseModel item) {

    if (StringUtils.isBlank(request.getEndDate())) {
      return Boolean.TRUE;
    }

    if (Objects.isNull(item.getDate().getEndDate())) {
      return Boolean.TRUE;
    }

    try {
      final var dateFormatter = new SimpleDateFormat(Constants.FORMAT_DATE);
      final var requestEndDate = dateFormatter.parse(request.getEndDate());
      final var itemEndDate = item.getDate().getEndDate().getTime();

      if (itemEndDate.compareTo(requestEndDate) <= 0) {
        return Boolean.TRUE;
      }

      if (itemEndDate.compareTo(requestEndDate) >= 0
          && StringUtils.isBlank(request.getStartDate())) {
        return Boolean.FALSE;
      }

      if (itemEndDate.compareTo(requestEndDate) >= 0
          && Objects.isNull(item.getDate().getStartDate())) {
        return Boolean.FALSE;
      }

      if (itemEndDate.after(requestEndDate) && Objects.nonNull(item.getDate().getStartDate())) {
        return item.getDate().getStartDate().getTime().compareTo(requestEndDate) <= 0;
      }

      return Boolean.FALSE;

    } catch (ParseException e) {
      LOGGER.error("Error parsing end date in request", e);
      return Boolean.FALSE;
    }
  }
}
