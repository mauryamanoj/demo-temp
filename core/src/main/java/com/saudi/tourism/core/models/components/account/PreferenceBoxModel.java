package com.saudi.tourism.core.models.components.account;

import com.google.gson.annotations.Expose;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;
import java.util.List;

/**
 * The Class AccountSlingModel.
 */
@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class PreferenceBoxModel implements Serializable {

  /**
   * sectionTitle.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String sectionTitle;

  /**
   * profileDetails.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String profileDetails;

  /**
   * resetButtonText.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String resetButtonText;

  /**
   * saveButtonText.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String saveButtonText;

  /**
   * promptText.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String promptText;

  /**
   * toastMessages.
   */
  @ChildResource
  @Expose
  private List<ToastMessagesModel> toastMessages;

  /**
   * inputs.
   */
  @ChildResource
  @Expose
  private AccountInputsModel inputs;



}
