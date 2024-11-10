package com.saudi.tourism.core.models.components.highlightscards.v1;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;


@Getter
@Builder
public class HighlightsCard {

  /**
   * Title.
   */
  @Expose
  private String title;

  /**
   * subTitle.
   */
  @Expose
  private String subTitle;

  /**
   * Image.
   */
  @Expose
  private Image image;

  /**
   * Logo.
   */
  @Expose
  private Image logo;

  /**
   * Start Date.
   */
  @Expose
  private Calendar startDate;

  /**
   * End Date.
   */
  @Expose
  private Calendar endDate;

  /**
   * Card Cta Link.
   */
  @Expose
  @Setter
  private String cardCtaLink;

  /**
   * Coming Soon Label.
   */
  @Expose
  private String comingSoonLabel;

  /**
   * Expired Label.
   */
  @Expose
  private String expiredLabel;

  /**
   * Indicates whether the link should open in a new window.
   */
  @Expose
  @Setter
  private boolean targetInNewWindow;

}
