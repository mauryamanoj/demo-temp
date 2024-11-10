package com.saudi.tourism.core.services.thingstodo.v1.adapters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.saudi.tourism.core.models.components.contentfragment.event.EventCFModel;
import com.saudi.tourism.core.models.components.contentfragment.utils.ContentFragmentAwareModel;
import com.saudi.tourism.core.services.thingstodo.v1.ThingToDoModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.Adaptable;
import org.osgi.service.component.annotations.Component;

/** Adapts Event CF to ThingToDoModel. */
@Component(service = ThingToDoCFAdapter.class, immediate = true)
public class ThingToDoEventCFAdapter implements ThingToDoCFAdapter, ContentFragmentAwareModel {
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
  public ThingToDoModel adaptTo(Adaptable adaptable) {
    final var model = adaptable.adaptTo(EventCFModel.class);

    if (model == null) {
      return null;
    }

    return ThingToDoModel.builder()
      .type("event")
      .id(model.getId())
      .title(model.getTitle())
      .subtitle(model.getSubtitle())
      .description(model.getEventDescription())
      .authorImage(model.getAuthorImage())
      .bannerImages(CollectionUtils.emptyIfNull(model.getBannerImages())
        .stream()
        .map(i -> i.getImage())
        .collect(Collectors.toList()))
      .lng(model.getLng())
      .lat(model.getLat())
      .hideFavorite(Optional.ofNullable(model.getHideFavorite()).orElse(false))
      .hideShareIcon(Optional.ofNullable(model.getHideShareIcon()).orElse(false))
      .destination(ThingToDoModel.Destination.fromCFModel(model.getDestination()))
      .ticketType(model.getTicketType())
      .ticketPrice(model.getTicketPrice())
      .ticketPriceSuffix(model.getTicketPriceSuffix())
      .ticketDetails(model.getTicketDetails())
      .ticketsCtaLink(model.getTicketsCtaLink())
        .favId(model.getFavId())
      .categoriesTags(
        Optional.ofNullable(model.getCategoriesTags())
          .map(Arrays::stream)
          .orElseGet(Stream::empty)
          .collect(Collectors.toList()))
      .publishedDate(model.getPublishedDate())
      .season(ThingToDoModel.Season.fromCFModel(model.getSeason()))
      .startDate(model.getStartDate())
      .endDate(model.getEndDate())
      .periodDays(model.getPeriodDays())
      .sameTimeAcrossWeek(Optional.ofNullable(model.getSameTimeAcrossWeek()).orElse(false))
      .dailyStartTime(model.getDailyStartTime())
      .dailyEndTime(model.getDailyEndTime())
      .timings(new ArrayList<>(CollectionUtils.emptyIfNull(model.getTimings())))
      .ageTag(model.getAgeTag())
      .pageLink(model.getPageLink())
      .build();
  }
}
