package com.saudi.tourism.core.services.thingstodo.v1.filters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import com.saudi.tourism.core.services.thingstodo.v1.FetchThingsToDoRequest;
import com.saudi.tourism.core.utils.Constants;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Component;

/**
 * Start Date Filter.
 */
@Component(service = ThingsToDoFilter.class, immediate = true)
@Slf4j
public class StartDateFilter implements ThingsToDoFilter {
  @Override
  public Boolean meetFilter(final @NonNull FetchThingsToDoRequest request, final @NonNull Resource thingToDo) {
    if (StringUtils.isBlank(request.getStartDate())) {
      return Boolean.TRUE;
    }

    Calendar startDate = thingToDo.getValueMap().get("startDate", Calendar.class);
    if (Objects.isNull(startDate)) {
      return Boolean.FALSE;
    }

    try {
      final var requestDateFormatter = new SimpleDateFormat(Constants.FORMAT_DATE);
      final var requestStartDate = requestDateFormatter.parse(request.getStartDate());
      final var thingToDoStartDate = startDate.getTime();

      // Check if thingToDo's start date is on or after the request start date
      if (!thingToDoStartDate.before(requestStartDate)) {
        return Boolean.TRUE;
      }

      // If there's an end date, check if it's on or after the request start date
      Calendar endDate = thingToDo.getValueMap().get("endDate", Calendar.class);
      if (Objects.nonNull(endDate) && !endDate.getTime().before(requestStartDate)) {
        return Boolean.TRUE;
      }

      return Boolean.FALSE;

    } catch (ParseException e) {
      LOGGER.error("Error parsing start date in request", e);
      return Boolean.FALSE;
    }
  }
}
