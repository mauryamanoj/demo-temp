package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.customAuthorDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.affiiliate.Affiliate;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.allow.Allow;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.bookmarks.Bookmarks;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.geo.Geo;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.image.Image;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.links.Links;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.subscription.Subscription;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * USER Detail POJO.
 */
public class CustomUserDetails {
  /**
   * registrationCode.
   */
  @SerializedName("registration_code")
  @Expose
  @Getter
  @Setter
  private String registrationCode;

  /**
   * residence_name.
   */
  @SerializedName("residence_name")
  @Expose
  @Getter
  @Setter
  private String residenceName;

  /**
   * visited_places.
   */
  @SerializedName("visited_places")
  @Expose
  @Getter
  @Setter
  private Object visitedPlaces;

  /**
   * lang.
   */
  @SerializedName("lang")
  @Expose
  @Getter
  @Setter
  private String lang;

  /**
   * about.
   */
  @SerializedName("about")
  @Expose
  @Getter
  @Setter
  private String about;

  /**
   * residence.
   */
  @SerializedName("residence")
  @Expose
  @Getter
  @Setter
  private String residence;

  /**
   * email.
   */
  @SerializedName("email")
  @Expose
  @Getter
  @Setter
  private String email;

  /**
   * verified.
   */
  @SerializedName("verified")
  @Expose
  @Getter
  @Setter
  private Object verified;

  /**
   * phone.
   */
  @SerializedName("phone")
  @Expose
  @Getter
  @Setter
  private Object phone;

  /**
   * unit_system.
   */
  @SerializedName("unit_system")
  @Expose
  @Getter
  @Setter
  private Object unitSystem;

  /**
   * business.
   */
  @SerializedName("business")
  @Expose
  @Getter
  @Setter
  private String business;

  /**
   * id.
   */
  @SerializedName("id")
  @Expose
  @Getter
  @Setter
  private String id;

  /**
   * geoid.
   */
  @SerializedName("geoid")
  @Expose
  @Getter
  @Setter
  private Integer geoid;

  /**
   * bookmarks.
   */
  @SerializedName("bookmarks")
  @Expose
  @Getter
  @Setter
  private Bookmarks bookmarks;

  /**
   * currency.
   */
  @SerializedName("currency")
  @Expose
  @Getter
  @Setter
  private String currency;

  /**
   * geo.
   */
  @SerializedName("geo")
  @Expose
  @Getter
  @Setter
  private Geo geo;

  /**
   * vw_campaign_accept.
   */
  @SerializedName("vw_campaign_accept")
  @Expose
  @Getter
  @Setter
  private Object vwCampaignAccept;

  /**
   * affiliate.
   */
  @SerializedName("affiliate")
  @Expose
  @Getter
  @Setter
  private Affiliate affiliate;

  /**
   * images.
   */
  @SerializedName("images")
  @Expose
  @Getter
  @Setter
  private List<Image> images = null;

  /**
   * anonymous.
   */
  @SerializedName("anonymous")
  @Expose
  @Getter
  @Setter
  private Object anonymous;

  /**
   * mailings.
   */
  @SerializedName("mailings")
  @Expose
  @Getter
  @Setter
  private Integer mailings;

  /**
   * safebutton.
   */
  @SerializedName("safebutton")
  @Expose
  @Getter
  @Setter
  private Object safebutton;

  /**
   * links.
   */
  @SerializedName("links")
  @Expose
  @Getter
  @Setter
  private Links links;

  /**
   * subscription.
   */
  @SerializedName("subscription")
  @Expose
  @Getter
  @Setter
  private Subscription subscription;

  /**
   * firstname.
   */
  @SerializedName("firstname")
  @Expose
  @Getter
  @Setter
  private String firstname;

  /**
   * allow.
   */
  @SerializedName("allow")
  @Expose
  @Getter
  @Setter
  private Allow allow;

  /**
   * registration_special.
   */
  @SerializedName("registration_special")
  @Expose
  @Getter
  @Setter
  private Object registrationSpecial;

  /**
   * email_unconfirmed.
   */
  @SerializedName("email_unconfirmed")
  @Expose
  @Getter
  @Setter
  private Object emailUnconfirmed;

  /**
   * linkSpecial.
   */
  @SerializedName("link_special")
  @Expose
  @Getter
  @Setter
  private Object linkSpecial;
  /**
   * lastname.
   */
  @SerializedName("lastname")
  @Expose
  @Getter
  @Setter
  private String lastname;

  /**
   * phone_unconfirmed.
   */
  @SerializedName("phone_unconfirmed")
  @Expose
  @Getter
  @Setter
  private Object phoneUnconfirmed;

  /**
   * notifications_count.
   */
  @SerializedName("notifications_count")
  @Expose
  @Getter
  @Setter
  private Object notificationsCount;

}
