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
 * End Date Filter.
 */
@Component(service = ThingsToDoFilter.class, immediate = true)
@Slf4j
public class EndDateFilter implements ThingsToDoFilter {
  @Override
  public Boolean meetFilter(final @NonNull FetchThingsToDoRequest request, final @NonNull Resource thingToDo) {
    // Check if the request's end date is provided and thingToDo's end date is available
    Calendar endDate = thingToDo.getValueMap().get("endDate", Calendar.class);
    if (StringUtils.isBlank(request.getEndDate()) || Objects.isNull(endDate)) {
      return Boolean.TRUE;
    }

    try {
      final var endDateFormatter = new SimpleDateFormat(Constants.FORMAT_DATE);
      final var requestEndDate = endDateFormatter.parse(request.getEndDate());
      final var thingToDoEndDate = endDate.getTime();

      // Check if thingToDo's end date is on or before the request end date
      return !thingToDoEndDate.after(requestEndDate);

    } catch (ParseException e) {
      LOGGER.error("Error parsing end date in request", e);
      return Boolean.FALSE;
    }
  }
}
