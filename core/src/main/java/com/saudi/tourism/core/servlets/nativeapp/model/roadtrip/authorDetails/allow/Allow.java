package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.allow;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/**
 * Allow POJO.
 */
public class Allow {
  /**
   * Users.
   */
  @SerializedName("users")
  @Expose
  @Getter
  private Integer users;

  /**
   * venuesDetails.
   */
  @SerializedName("venues_details")
  @Expose
  @Getter
  private Integer venuesDetails;

  /**
   * scenariosCreator.
   */
  @SerializedName("scenarios_creator")
  @Expose
  @Getter
  private Integer scenariosCreator;

  /**
   * superAdmin.
   */
  @SerializedName("super_admin")
  @Expose
  @Getter
  private Integer superAdmin;

  /**
   * viewLogs.
   */
  @SerializedName("view_logs")
  @Expose
  @Getter
  private Integer viewLogs;

  /**
   * roadTravel.
   */
  @SerializedName("road_travel")
  @Expose
  @Getter
  private Integer roadTravel;

  /**
   * scenariosTranslator.
   */
  @SerializedName("scenarios_translator")
  @Expose
  @Getter
  private Integer scenariosTranslator;

  /**
   * wiki4sqEdit.
   */
  @SerializedName("wiki_4sq_edit")
  @Expose
  @Getter
  private Integer wiki4sqEdit;

  /**
   * poiEditor.
   */
  @SerializedName("poi_editor")
  @Expose
  @Getter
  private Integer poiEditor;

  /**
   * adminInterface.
   */
  @SerializedName("admin_interface")
  @Expose
  @Getter
  private Integer adminInterface;

  /**
   * scenariosApprove.
   */
  @SerializedName("scenarios_approve")
  @Expose
  @Getter
  private Integer scenariosApprove;

}
