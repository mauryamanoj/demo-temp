package com.saudi.tourism.core.models.components.card.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.components.tripplan.v1.Activity;
import com.saudi.tourism.core.services.ActivityService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Model for c03 Card Component with support of activities (Trip Planner activities).
 */
@NoArgsConstructor
@Getter
@Model(adaptables = Resource.class,
       resourceType = CardComponentModel.RT_CARD_COMPONENT,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class CardComponentModel {

  /**
   * Resource type for the c03-card-component.
   */
  static final String RT_CARD_COMPONENT = "sauditourism/components/content/c03-card/v1/c03-card";

  /**
   * Activity model if this card is for displaying activity.
   */
  @Expose
  private Activity activityModel;

  /**
   * Inject card model if this is not for an activity.
   */
  @Self
  @Expose
  private CardModel cardModel;

  /**
   * {@code true} if needs to render link from activity.
   */
  @ValueMapValue
  @Expose
  private Boolean useActivityLink;

  /**
   * Link to be displayed.
   */
  @ChildResource
  @Expose
  private final Link link = new Link();

  /**
   * activityReference.
   */
  @Expose
  private String activityReference;
  /**
   * type.
   */
  @Expose
  private String type;
  /**
   * Injection constructor.
   *
   * @param currentResource the current resource
   * @param activities      activity service for searching the corresponding activity by reference
   */
  @Inject
  public CardComponentModel(
      @Self(injectionStrategy = InjectionStrategy.REQUIRED) final Resource currentResource,
      @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
      final ActivityService activities) {
    // Set activity flag if this card is for displaying activity (visiting point)
    final ValueMap properties = currentResource.getValueMap();
    type = properties.get("type", String.class);
    if (StringUtils.equals(type, Constants.ACTIVITY)) {
      activityReference = properties.get("activityReference", String.class);
      if (StringUtils.isBlank(activityReference)) {
        LOGGER.error("Activity reference is not set for card, please configure");
        return;
      }

      activityModel = activities.getActivityByPath(activityReference,
          CommonUtils.getLanguageForPath(currentResource.getPath()));
      if (activityModel == null) {
        LOGGER.error("Error in adapting activity {}", activityReference);
      }
    }
  }

  /**
   * Post construction initialization.
   */
  @PostConstruct
  private void init() {
    // Initialize links when this card is for activity
    if (this.activityModel != null && Boolean.TRUE.equals(useActivityLink)) {
      if (StringUtils.isBlank(link.getCopy())) {
        link.setCopy(activityModel.getTitle());
      }

      link.setUrl(activityModel.getLink());
      link.setUrlWithExtension(activityModel.getLink());
      link.setUrlSlingExporter(activityModel.getFavId());
    } else if (cardModel != null) {
      // Copy some default link props from CardModel for manual card
      // (we don't need a copy here, other urls must be the same as in the card link automatically)
      link.setAppEventData(cardModel.getLink().getAppEventData());
    }
  }
  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
