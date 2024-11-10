package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.customAuthorDetails.links;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * Links.
 */
public class Links {

  /**
   * socialmedia link.
   */
  @SerializedName("linkedin")
  @Expose
  @Getter
  @Setter
  private String linkedin;

  /**
   * socialmedia link.
   */
  @SerializedName("instagram")
  @Expose
  @Getter
  @Setter
  private String instagram;

  /**
   * socialmedia link.
   */
  @SerializedName("twitter")
  @Expose
  @Getter
  @Setter
  private String twitter;

  /**
   * socialmedia link.
   */
  @SerializedName("pinterest")
  @Expose
  @Getter
  @Setter
  private String pinterest;

  /**
   * socialmedia link.
   */
  @SerializedName("youtravelme")
  @Expose
  @Setter
  @Getter
  private String youtravelme;

  /**
   * socialmedia link.
   */
  @SerializedName("facebook")
  @Expose
  @Setter
  @Getter
  private String facebook;

  /**
   * socialmedia link.
   */
  @SerializedName("public_email")
  @Expose
  @Getter
  @Setter
  private String publicEmail;

  /**
   * socialmedia link.
   */
  @SerializedName("website")
  @Expose
  @Getter
  @Setter
  private String website;

  /**
   * socialmedia link.
   */
  @SerializedName("telegram")
  @Expose
  @Getter
  @Setter
  private String telegram;

  /**
   * socialmedia link.
   */
  @SerializedName("whatsapp")
  @Expose
  @Getter
  @Setter
  private String whatsapp;

  /**
   * socialmedia link.
   */
  @SerializedName("vk")
  @Expose
  @Getter
  @Setter
  private String vk;
}
