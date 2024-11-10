package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.about.About;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.affiiliate.Affiliate;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.allow.Allow;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.bookmarks.Bookmarks;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.geo.Geo;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.image.Image;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.lastname.Lastname;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.firstname.FirstName;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.links.Links;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.residenceName.ResidenceName;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.subscription.Subscription;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.visitesPlaces.VisitedPlaces;
import lombok.Getter;

import java.util.List;

/**
 * USER Detail POJO.
 */
public class User {
  /**
   * registrationCode.
   */
  @SerializedName("registration_code")
  @Expose
  @Getter
  private String registrationCode;

  /**
   * residence_name.
   */
  @SerializedName("residence_name")
  @Expose
  @Getter
  private ResidenceName residenceName;

  /**
   * visited_places.
   */
  @SerializedName("visited_places")
  @Expose
  @Getter
  private VisitedPlaces visitedPlaces;

  /**
   * lang.
   */
  @SerializedName("lang")
  @Expose
  @Getter
  private String lang;

  /**
   * about.
   */
  @SerializedName("about")
  @Expose
  @Getter
  private About about;

  /**
   * residence.
   */
  @SerializedName("residence")
  @Expose
  @Getter
  private String residence;

  /**
   * email.
   */
  @SerializedName("email")
  @Expose
  @Getter
  private String email;

  /**
   * verified.
   */
  @SerializedName("verified")
  @Expose
  @Getter
  private Object verified;

  /**
   * phone.
   */
  @SerializedName("phone")
  @Expose
  @Getter
  private Object phone;

  /**
   * unit_system.
   */
  @SerializedName("unit_system")
  @Expose
  @Getter
  private Object unitSystem;

  /**
   * business.
   */
  @SerializedName("business")
  @Expose
  @Getter
  private String business;

  /**
   * id.
   */
  @SerializedName("id")
  @Expose
  @Getter
  private String id;

  /**
   * geoid.
   */
  @SerializedName("geoid")
  @Expose
  @Getter
  private Integer geoid;

  /**
   * bookmarks.
   */
  @SerializedName("bookmarks")
  @Expose
  @Getter
  private Bookmarks bookmarks;

  /**
   * currency.
   */
  @SerializedName("currency")
  @Expose
  @Getter
  private String currency;

  /**
   * geo.
   */
  @SerializedName("geo")
  @Expose
  @Getter
  private Geo geo;

  /**
   * vw_campaign_accept.
   */
  @SerializedName("vw_campaign_accept")
  @Expose
  @Getter
  private Object vwCampaignAccept;

  /**
   * affiliate.
   */
  @SerializedName("affiliate")
  @Expose
  @Getter
  private Affiliate affiliate;

  /**
   * images.
   */
  @SerializedName("images")
  @Expose
  @Getter
  private List<Image> images = null;

  /**
   * anonymous.
   */
  @SerializedName("anonymous")
  @Expose
  @Getter
  private Object anonymous;

  /**
   * mailings.
   */
  @SerializedName("mailings")
  @Expose
  @Getter
  private Integer mailings;

  /**
   * safebutton.
   */
  @SerializedName("safebutton")
  @Expose
  @Getter
  private Object safebutton;

  /**
   * links.
   */
  @SerializedName("links")
  @Expose
  @Getter
  private Links links;

  /**
   * subscription.
   */
  @SerializedName("subscription")
  @Expose
  @Getter
  private Subscription subscription;

  /**
   * firstname.
   */
  @SerializedName("firstname")
  @Expose
  @Getter
  private FirstName firstname;

  /**
   * allow.
   */
  @SerializedName("allow")
  @Expose
  @Getter
  private Allow allow;

  /**
   * registration_special.
   */
  @SerializedName("registration_special")
  @Expose
  @Getter
  private Object registrationSpecial;

  /**
   * email_unconfirmed.
   */
  @SerializedName("email_unconfirmed")
  @Expose
  @Getter
  private Object emailUnconfirmed;

  /**
   * linkSpecial.
   */
  @SerializedName("link_special")
  @Expose
  @Getter
  private Object linkSpecial;
  /**
   * lastname.
   */
  @SerializedName("lastname")
  @Expose
  @Getter
  private Lastname lastname;

  /**
   * phone_unconfirmed.
   */
  @SerializedName("phone_unconfirmed")
  @Expose
  @Getter
  private Object phoneUnconfirmed;

  /**
   * notifications_count.
   */
  @SerializedName("notifications_count")
  @Expose
  @Getter
  private Object notificationsCount;

}
