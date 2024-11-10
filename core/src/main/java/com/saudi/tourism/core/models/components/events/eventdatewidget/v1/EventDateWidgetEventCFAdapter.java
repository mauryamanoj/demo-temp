package com.saudi.tourism.core.models.components.events.eventdatewidget.v1;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.saudi.tourism.core.models.components.contentfragment.utils.ContentFragmentAwareModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.Adaptable;
import org.osgi.service.component.annotations.Component;

import java.util.Calendar;

@Component(service = EventDateWidgetCFAdapter.class, immediate = true)
public class EventDateWidgetEventCFAdapter implements EventDateWidgetCFAdapter, ContentFragmentAwareModel {

  @Override
  public boolean supports(Adaptable adaptable) {
    if (adaptable == null) {
      return false;
    }

    var cf = adaptable.adaptTo(ContentFragment.class);
    if (cf == null) {
      return false;
    }

    return StringUtils.equalsIgnoreCase("event", cf.getTemplate().getTitle());
  }

  @Override
  public EventDateWidgetCFModel adaptTo(Adaptable adaptable) {
    final var cf = adaptable.adaptTo(ContentFragment.class);

    if (cf == null) {
      return null;
    }
    var title = getElementValue(cf, "eventWidgetTitle", String.class);
    var comingSoonLabel = getElementValue(cf, "comingSoonLabel", String.class);
    var expiredLabel = getElementValue(cf, "expiredLabel", String.class);
    var startDate = getElementValue(cf, "startDate", Calendar.class);
    var endDate = getElementValue(cf, "endDate", Calendar.class);

    return EventDateWidgetCFModel.builder()
        .title(title)
        .comingSoonLabel(comingSoonLabel)
        .expiredLabel(expiredLabel)
        .startDate(startDate)
        .endDate(endDate)
        .build();
  }

}
