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

/** StartDate filter for items. */
@Component(service = ItemFilter.class, immediate = true)
@Slf4j
public class StartDateFilter implements ItemFilter {
  @Override
  public Boolean meetFilter(
          final @NonNull MobileRequestParams request, final @NonNull ItemResponseModel item) {

    if (StringUtils.isBlank(request.getStartDate())) {
      return Boolean.TRUE;
    }

    if (Objects.isNull(item.getDate().getStartDate())) {
      return Boolean.FALSE;
    }

    try {
      final var dateFormatter = new SimpleDateFormat(Constants.FORMAT_DATE);
      final var requestStartDate = dateFormatter.parse(request.getStartDate());
      final var itemStartDate = item.getDate().getStartDate().getTime();

      if (itemStartDate.compareTo(requestStartDate) >= 0) {
        return Boolean.TRUE;
      }

      if (itemStartDate.compareTo(requestStartDate) <= 0
          && Objects.isNull(item.getDate().getEndDate())) {
        return Boolean.TRUE;
      }

      if (itemStartDate.compareTo(requestStartDate) <= 0
          && Objects.nonNull(item.getDate().getEndDate())) {
        return item.getDate().getEndDate().getTime().compareTo(requestStartDate) >= 0;
      }

      return Boolean.FALSE;

    } catch (ParseException e) {
      LOGGER.error("Error parsing start date in request", e);
      return Boolean.FALSE;
    }
  }
}
