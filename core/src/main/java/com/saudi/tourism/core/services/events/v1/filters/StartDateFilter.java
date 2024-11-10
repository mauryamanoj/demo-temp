package com.saudi.tourism.core.services.events.v1.filters;

import com.saudi.tourism.core.models.components.contentfragment.event.EventCFModel;
import com.saudi.tourism.core.services.events.v1.FetchEventsRequest;
import com.saudi.tourism.core.utils.Constants;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * Start Date Filter.
 */
@Component(service = EventFilter.class, immediate = true)
public class StartDateFilter implements EventFilter {
  @Override
  public Boolean meetFilter(final @NonNull FetchEventsRequest request, final @NonNull EventCFModel event) {
    if (StringUtils.isBlank(request.getStartDate())) {
      return Boolean.TRUE;
    }

    if (Objects.isNull(event.getStartDate())) {
      return Boolean.FALSE;
    }


    try {
      final var requestDateFormater = new SimpleDateFormat(Constants.FORMAT_DATE);
      final var requestStartDate = requestDateFormater.parse(request.getStartDate());

      if (event.getStartDate().getTime().compareTo(requestStartDate) >= 0) {
        return Boolean.TRUE;
      }

      if (event.getStartDate().getTime().compareTo(requestStartDate) <= 0
          && Objects.isNull(event.getEndDate())) {
        return Boolean.TRUE;
      }

      if (event.getStartDate().getTime().compareTo(requestStartDate) <= 0
          && Objects.nonNull(event.getEndDate())) {
        return event.getEndDate().getTime().compareTo(requestStartDate) >= 0;
      }

      return Boolean.FALSE;

    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }
}
