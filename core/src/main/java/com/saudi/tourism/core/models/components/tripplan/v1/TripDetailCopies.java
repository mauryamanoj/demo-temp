package com.saudi.tourism.core.models.components.tripplan.v1;

import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * TripDetailCopies.
 */
@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class TripDetailCopies implements Serializable {
  /**
   * daysCopy.
   */
  @ValueMapValue
  private String daysCopy;
  /**
   * nightsCopy.
   */
  @ValueMapValue
  private String nightsCopy;
  /**
   * departureLabel.
   */
  @ValueMapValue
  private String departureLabel;
  /**
   * addActivityLabel.
   */
  @ValueMapValue
  private String addActivityLabel;
  /**
   * holidayNotice.
   */
  @ValueMapValue
  private String holidayNotice;
  /**
   * moveCtaLabel.
   */
  @ValueMapValue
  private String moveCtaLabel;
  /**
   * deleteCtaLabel.
   */
  @ValueMapValue
  private String deleteCtaLabel;
  /**
   * seeMoreCtaLabel.
   */
  @ValueMapValue
  private String seeMoreCtaLabel;
  /**
   * Day is full notification config.
   */
  @ChildResource
  private TitleDescriptionModel dayFullActivities;
  /**
   * dayFullLabelCopy.
   */
  @ValueMapValue
  private String dayFullLabelCopy;
  /**
   * datePickerPlaceholder.
   */
  @ValueMapValue
  private String datePickerPlaceholder;
  /**
   * addCtaLabel.
   */
  @ValueMapValue
  private String addCtaLabel;
  /**
   * removeCtaLabel.
   */
  @ValueMapValue
  private String removeCtaLabel;
  /**
   * activityAdded.
   */
  @ValueMapValue
  private String activityAdded;
  /**
   * activityRemoved.
   */
  @ValueMapValue
  private String activityRemoved;
  /**
   * dayLabel.
   */
  @ValueMapValue
  private String dayLabel;
  /**
   * weekLabel.
   */
  @ValueMapValue
  private String weekLabel;
  /**
   * nextWeekButtonLabel.
   */
  @ValueMapValue
  private String nextWeekButtonLabel;
  /**
   * previousWeekButtonLabel.
   */
  @ValueMapValue
  private String previousWeekButtonLabel;

  /**
   * emptyDay.
   */
  @ChildResource
  private TitleDescriptionModel emptyDay;
  /**
   * hero.
   */
  @ChildResource
  private Hero hero;
  /**
   * loginModal.
   */
  @ChildResource
  private LoginModal loginModal;
  /**
   * loadingTripModal.
   */
  @ChildResource
  private LoadingTripModal loadingTripModal;
  /**
   * moveScheduleItemModal.
   */
  @ChildResource
  private MoveScheduleItemModal moveScheduleItemModal;
  /**
   * scheduleModal.
   */
  @ChildResource
  private ScheduleModal scheduleModal;
  /**
   * removeActivityModal.
   */
  @ChildResource
  private RemoveActivityModal removeActivityModal;
  /**
   * editYourTripModal.
   */
  @ChildResource
  private EditYourTripModal editYourTripModal;
  /**
   * deleteTripPlanModal.
   */
  @ChildResource
  private DeleteTripPlanModal deleteTripPlanModal;
  /**
   * genericErrorModal.
   */
  @ChildResource
  private GenericErrorModal genericErrorModal;

  /**
   * Titled class.
   */
  @Data
  public static class TitledObject implements Serializable {
    /**
     * Title.
     */
    @ValueMapValue
    private String title;
  }

  /**
   * EmptyDay class.
   */
  @Model(adaptables = {Resource.class},
         defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Data
  public static class TitleDescriptionModel extends TitledObject {
    /**
     * Description.
     */
    @ValueMapValue
    private String description;
  }

  /**
   * Hero class.
   */
  @Model(adaptables = {Resource.class},
         defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Data
  public static class Hero implements Serializable {
    /**
     * editLabel.
     */
    @ValueMapValue
    private String editLabel;
    /**
     * multipleDestinationsCopy.
     */
    @ValueMapValue
    private String multipleDestinationsCopy;
    /**
     * singleDestinationCopy.
     */
    @ValueMapValue
    private String singleDestinationCopy;
  }

  /**
   * LoginModal class.
   */
  @Model(adaptables = {Resource.class},
         defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Data
  public static class LoginModal extends TitledObject {
    /**
     * copy.
     */
    @ValueMapValue
    private String copy;
    /**
     * ctaText.
     */
    @ValueMapValue
    private String ctaText;
  }

  /**
   * LoadingTripModal class.
   */
  @Model(adaptables = {Resource.class},
         defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Data
  public static class LoadingTripModal extends TitledObject {
    /**
     * copy.
     */
    @ValueMapValue
    private String copy;
  }

  /**
   * MoveScheduleItemModal class.
   */
  @Model(adaptables = {Resource.class},
         defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Data
  public static class MoveScheduleItemModal extends TitledObject {
    /**
     * subtitle.
     */
    @ValueMapValue
    private String subtitle;
    /**
     * dayLabel.
     */
    @ValueMapValue
    private String dayLabel;
    /**
     * secondaryButtonLabel.
     */
    @ValueMapValue
    private String secondaryButtonLabel;
    /**
     * primaryButtonLabel.
     */
    @ValueMapValue
    private String primaryButtonLabel;
  }

  /**
   * ScheduleModal class.
   */
  @Model(adaptables = {Resource.class},
         defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Data
  public static class ScheduleModal implements Serializable {
    /**
     * favoritesTabLabel.
     */
    @ValueMapValue
    private String favoritesTabLabel;
    /**
     * attractionsTabLabel.
     */
    @ValueMapValue
    private String attractionsTabLabel;
    /**
     * eventsTabLabel.
     */
    @ValueMapValue
    private String eventsTabLabel;
  }

  /**
   * MoveScheduleItemModal class.
   */
  @Model(adaptables = {Resource.class},
         defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Data
  public static class RemoveActivityModal extends TitleDescriptionModel {
    /**
     * subtitle.
     */
    @ValueMapValue
    private String subtitle;
    /**
     * secondaryButtonLabel.
     */
    @ValueMapValue
    private String secondaryButtonLabel;
    /**
     * primaryButtonLabel.
     */
    @ValueMapValue
    private String primaryButtonLabel;
  }

  /**
   * EditYourTripModal class.
   */
  @Model(adaptables = {Resource.class},
         defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Data
  public static class EditYourTripModal extends TitledObject {
    /**
     * startDayTitle.
     */
    @ValueMapValue
    private String startDayTitle;
    /**
     * maxDaysClarification.
     */
    @ValueMapValue
    private String maxDaysClarification;
    /**
     * saveButtonLabel.
     */
    @ValueMapValue
    private String saveButtonLabel;
    /**
     * regionDeleteClarification.
     */
    @ValueMapValue
    private String regionDeleteClarification;
    /**
     * Label for edit destinations block.
     */
    @ValueMapValue
    private String editDestinationsLabel;
    /**
     * Label for add destinations block.
     */
    @ValueMapValue
    private String addDestinationsLabel;
  }

  /**
   * DeleteTripPlanModal class.
   */
  @Model(adaptables = {Resource.class},
         defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Data
  public static class DeleteTripPlanModal extends TitledObject {
    /**
     * subtitle.
     */
    @ValueMapValue
    private String subtitle;
    /**
     * paragraph.
     */
    @ValueMapValue
    private String paragraph;
    /**
     * primaryButtonLabel.
     */
    @ValueMapValue
    private String primaryButtonLabel;
    /**
     * secondaryButtonLabel.
     */
    @ValueMapValue
    private String secondaryButtonLabel;
  }

  /**
   * GenericErrorModal class.
   */
  @Model(adaptables = {Resource.class},
         defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Data
  public static class GenericErrorModal extends TitledObject {
    /**
     * paragraph.
     */
    @ValueMapValue
    private String paragraph;
    /**
     * primaryButtonLabel.
     */
    @ValueMapValue
    private String primaryButtonLabel;
  }
}
