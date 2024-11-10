package com.saudi.tourism.core.models.mobile.components.atoms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/** EventWidget Model. */
@Data
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class EventWidget {

  /** dates. */
  @ChildResource(name = "dates")
  @JsonIgnore
  private List<Date> eventDates;

  /** dates. */
  private List<Calendar> dates;

  /** expired. */
  @ValueMapValue private Boolean expired;

  @PostConstruct
  void init() {
    dates =
        eventDates.stream()
          .filter(Objects::nonNull)
          .map(d -> d.date)
          .collect(Collectors.toList());
  }

  /** Inner class Date represents date item of event widget. */
  @Data
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  public static class Date {
    /** Date. */
    @ValueMapValue private Calendar date;
  }
}
