package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.customAuthorDetails.allow;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

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
  @Setter
  private Integer users;

  /**
   * venuesDetails.
   */
  @SerializedName("venues_details")
  @Expose
  @Getter
  @Setter
  private Integer venuesDetails;

  /**
   * scenariosCreator.
   */
  @SerializedName("scenarios_creator")
  @Expose
  @Getter
  @Setter
  private Integer scenariosCreator;

  /**
   * superAdmin.
   */
  @SerializedName("super_admin")
  @Expose
  @Getter
  @Setter
  private Integer superAdmin;

  /**
   * viewLogs.
   */
  @SerializedName("view_logs")
  @Expose
  @Getter
  @Setter
  private Integer viewLogs;

  /**
   * roadTravel.
   */
  @SerializedName("road_travel")
  @Expose
  @Getter
  @Setter
  private Integer roadTravel;

  /**
   * scenariosTranslator.
   */
  @SerializedName("scenarios_translator")
  @Expose
  @Getter
  @Setter
  private Integer scenariosTranslator;

  /**
   * wiki4sqEdit.
   */
  @SerializedName("wiki_4sq_edit")
  @Expose
  @Getter
  @Setter
  private Integer wiki4sqEdit;

  /**
   * poiEditor.
   */
  @SerializedName("poi_editor")
  @Expose
  @Getter
  @Setter
  private Integer poiEditor;

  /**
   * adminInterface.
   */
  @SerializedName("admin_interface")
  @Expose
  @Getter
  @Setter
  private Integer adminInterface;

  /**
   * scenariosApprove.
   */
  @SerializedName("scenarios_approve")
  @Expose
  @Getter
  @Setter
  private Integer scenariosApprove;
}
