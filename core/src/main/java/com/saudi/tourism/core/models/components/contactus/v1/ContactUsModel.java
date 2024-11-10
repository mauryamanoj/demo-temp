package com.saudi.tourism.core.models.components.contactus.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;



import javax.annotation.PostConstruct;

/** Contact us Model. */
@Model(
    adaptables = {Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class ContactUsModel {

  /** The title. */
  @ValueMapValue
  @Expose
  private String title;

  /** The description. */
  @ValueMapValue
  @Expose
  private String description;

  /** The first name text. */
  @ValueMapValue
  @Expose
  private String firstNameText;

  /** The first name placeholder text. */
  @ValueMapValue
  @Expose
  private String firstNamePlaceholderText;

  /** The first name error text. */
  @ValueMapValue
  @Expose
  private String firstNameErrorText;

  /** The first name error text. */
  @ValueMapValue
  @Expose
  private String firstNameValidationErrorText;

  /** The last name text. */
  @ValueMapValue
  @Expose
  private String lastNameText;

  /** The last name placeholder text. */
  @ValueMapValue
  @Expose
  private String lastNamePlaceholderText;

  /** The last name error text. */
  @ValueMapValue
  @Expose
  private String lastNameErrorText;

  /** The first name error text. */
  @ValueMapValue
  @Expose
  private String lastNameValidationErrorText;

  /** The email address text. */
  @ValueMapValue
  @Expose
  private String emailAddressText;

  /** The email address placeholder text. */
  @ValueMapValue
  @Expose
  private String emailAddressPlaceholderText;

  /** The email address error text. */
  @ValueMapValue
  @Expose
  private String emailAddressErrorText;

  /** The first name error text. */
  @ValueMapValue
  @Expose
  private String emailValidationErrorText;

  /** The phone number text. */
  @ValueMapValue
  @Expose
  private String phoneNumberText;

  /** The phone number placeholder text. */
  @ValueMapValue
  @Expose
  private String phoneNumberPlaceholderText;

  /** The phone number error text. */
  @ValueMapValue
  @Expose
  private String phoneNumberErrorText;

  /** The first name error text. */
  @ValueMapValue
  @Expose
  private String phoneValidationErrorText;

  /** The message type text. */
  @ValueMapValue
  @Expose
  private String messageTypeText;

  /** The message text. */
  @ValueMapValue
  @Expose
  private String messageText;

  /** The message placeholder text. */
  @ValueMapValue
  @Expose
  private String messagePlaceholderText;

  /** The message error text. */
  @ValueMapValue
  @Expose
  private String messageErrorText;

  /** The CTA button text. */
  @ValueMapValue
  @Expose
  private String ctaButtonText;

  /** The message success text. */
  @ValueMapValue
  @Expose
  private String messageSuccessText;

  /** The message failure text. */
  @ValueMapValue
  @Expose
  private String messageFailureText;

  /** The message type servlet. */
  @Expose
  private String messageTypeServlet;


  /** The submit form servlet. */
  @Expose
  private String submitFormServlet;

  /** The flags path. */
  @ValueMapValue
  @Expose
  private String flagsPath;

  /** component html id. */
  @ValueMapValue
  @Expose
  private String componentHtmlId;


  /**
   * Reference of Saudi Tourism Configuration.
   */
  @OSGiService
  private transient SaudiTourismConfigs saudiTourismConfigs;



  /** Image. */
  @ChildResource
  @Expose
  private Image image;

  /** Resource resolver. */
  @SlingObject(injectionStrategy = InjectionStrategy.REQUIRED)
  private transient ResourceResolver resolver;

  /** currentResource. */
  @SlingObject
  private transient Resource currentResource;

  /** Post construct init method. */
  @PostConstruct
  protected void init() {
    if (null != image) {
      DynamicMediaUtils.setAllImgBreakPointsInfo(
          image, "crop-460x620", "crop-460x620", "1280", "420", resolver, currentResource);
    }
    messageTypeServlet = saudiTourismConfigs.getContactUsMessageTypeApiEndpoint();
    submitFormServlet = saudiTourismConfigs.getContactUsFormEndpoint();
    if (StringUtils.isEmpty(flagsPath)) {
      flagsPath = saudiTourismConfigs.getFlagsPath();
    }
  }


  /**
   * getJson method for account component.
   *
   * @return json representation.
   */
  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
