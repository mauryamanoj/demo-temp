package com.saudi.tourism.core.services.thingstodo.v1.adapters;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.saudi.tourism.core.models.components.contentfragment.attraction.AttractionCFModel;
import com.saudi.tourism.core.models.components.contentfragment.utils.ContentFragmentAwareModel;
import com.saudi.tourism.core.services.thingstodo.v1.ThingToDoModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.Adaptable;
import org.osgi.service.component.annotations.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Adapts Attraction CF to ThingToDoModel. */
@Component(service = ThingToDoCFAdapter.class, immediate = true)
public class ThingToDoAttractionCFAdapter implements ThingToDoCFAdapter, ContentFragmentAwareModel {
  @Override
  public boolean supports(Adaptable adaptable) {
    if (adaptable == null) {
      return false;
    }

    var cf = adaptable.adaptTo(ContentFragment.class);
    if (cf == null) {
      return false;
    }

    return StringUtils.equalsIgnoreCase("attraction", cf.getTemplate().getTitle());
  }

  @Override
  public ThingToDoModel adaptTo(Adaptable adaptable) {
    final var model = adaptable.adaptTo(AttractionCFModel.class);

    if (model == null) {
      return null;
    }

    return ThingToDoModel.builder()
        .type("attraction")
        .id(model.getId())
        .title(model.getTitle())
        .subtitle(model.getSubtitle())
        .description(model.getAboutDescription())
        .authorImage(model.getAuthorImage())
        .bannerImages(
            CollectionUtils.emptyIfNull(model.getBannerImages()).stream()
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
        .freePaid(model.getFreePaid())
        .pageLink(model.getPageLink())
        .favId(model.getFavId())
        .categoriesTags(
            Optional.ofNullable(model.getCategoriesTags())
                .map(Arrays::stream)
                .orElseGet(Stream::empty)
                .collect(Collectors.toList()))
      .sameTimeAcrossWeek(Optional.ofNullable(model.getSameTimeAcrossWeek()).orElse(false))
      .dailyStartTime(model.getDailyStartTime())
      .dailyEndTime(model.getDailyEndTime())
      .timings(new ArrayList<>(CollectionUtils.emptyIfNull(model.getTimings())))
      .ageTag(model.getAgeTag())
      .publishedDate(model.getPublishedDate())
        .pagePath(model.getInternalPagePath())
        .build();
  }
}
