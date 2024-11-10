package com.saudi.tourism.core.models.components.smallstories.v1;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;



import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


/** WhatToBuy CF Model. */
@Builder
@Getter
public class CardModel {

  /**
   * Title.
   */
  @Expose
  private String title;

  /**
   * Destination.
   */
  @Expose
  @Setter
  private String city;

  /**
   * destinationPath.
   */
  private String destinationPath;

  /**
   * favorite.
   */
  @Expose
  @Setter
  private Boolean hideFavorite;

  /**
   * Card image.
   */
  @Expose
  private Image image;

  /**
   * tag.
   */
  @Expose
  @Setter
  private String tag;

  /**
   * favId.
   */
  @Expose
  @Setter
  private String favId;

  /**
   * Card link.
   */
  @Expose
  @Setter
  private String cardCtaLink;


}
