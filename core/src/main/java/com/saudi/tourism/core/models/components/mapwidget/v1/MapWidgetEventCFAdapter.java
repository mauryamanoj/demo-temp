package com.saudi.tourism.core.models.components.mapwidget.v1;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.saudi.tourism.core.models.components.contentfragment.utils.ContentFragmentAwareModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.Adaptable;
import org.osgi.service.component.annotations.Component;

@Component(service = MapWidgetCFAdapter.class, immediate = true)
public class MapWidgetEventCFAdapter implements MapWidgetCFAdapter, ContentFragmentAwareModel {

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
  public MapWidgetCFModel adaptTo(Adaptable adaptable) {
    final var cf = adaptable.adaptTo(ContentFragment.class);

    if (cf == null) {
      return null;
    }
    var meetingPointHeader = getElementValue(cf, "meetingPointHeader", String.class);
    var meetingPointLabel = getElementValue(cf, "meetingPointLabel", String.class);
    var meetingPointValue = getElementValue(cf, "meetingPointValue", String.class);
    var getDirectionsLabel = getElementValue(cf, "getDirectionsLabel", String.class);
    var meetingPointLat = getElementValue(cf, "meetingPointLat", String.class);
    var meetingPointLng = getElementValue(cf, "meetingPointLng", String.class);
    String lat;
    if (StringUtils.isNotBlank(meetingPointLat)) {
      lat = meetingPointLat;
    } else {
      lat = getElementValue(cf, "lat", String.class);
    }

    String lng;
    if (StringUtils.isNotBlank(meetingPointLng)) {
      lng = meetingPointLng;
    } else {
      lng = getElementValue(cf, "lng", String.class);
    }

    return MapWidgetCFModel.builder()
        .title(meetingPointHeader)
        .locationLabel(meetingPointLabel)
        .locationValue(meetingPointValue)
        .latitude(lat)
        .longitude(lng)
        .ctaLabel(getDirectionsLabel)
        .build();
  }
}
