package com.saudi.tourism.core.models.components.reachus.v1;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.List;

/** Phone Card Model. */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Setter
public class PhoneCardModel {
  /**
   * COUNTRIES CARD TYPE.
   */
  private static final String WITH_NUMBERS = "WITH_NUMBERS";

  /**
   * SAUDI NUMBERS CARD TYPE.
   */
  private static final String WITH_COUNTRIES = "WITH_COUNTRIES";
  /**
   * Current Resource.
   */
  @Self
  private Resource resource;

  /**
   * Call US CTA Label.
   */
  @ValueMapValue
  @Expose
  private String callUsCta;

  /**
   * Live Label.
   */
  @ValueMapValue
  @Expose
  private String liveLabel;

  /**
   * Offline Label.
   */
  @ValueMapValue
  @Expose
  private String offlineLabel;

  /**
   * Phone Number for small card.
   */
  @ValueMapValue
  @Expose
  private String phoneNumber;

  /**
   * Contact With Countries.
   */
  @ChildResource
  private List<ContactListModel> contactWithFlag;

  /**
   * Contact With Saudi Numbers.
   */
  @ChildResource
  private List<ContactListModel> contactWithoutFlag;
  /**
   * List of Contacts.
   */
  @Expose
  private List<ContactListModel> contactItems;

  /**
   * Init method.
   */
  @PostConstruct
  protected void init() {
    final var parentResource = resource.getParent();
    final var parentModel = parentResource.adaptTo(ReachUsModel.class);

    if (parentModel != null && WITH_COUNTRIES.equals(parentModel.getVariation())) {
      this.contactItems = contactWithFlag;
    }

    if (parentModel != null && WITH_NUMBERS.equals(parentModel.getVariation())) {
      this.contactItems = contactWithoutFlag;
    }
  }

  /**
   * Countries Model.
   */
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Getter
  public static class ContactListModel {
    /**
     * Contact Name.
     */
    @ValueMapValue
    @Expose
    private String contactName;
    /**
     * Country Flag.
     */
    @ValueMapValue
    @Expose
    private String countryFlag;
    /**
     * Phone Number.
     */
    @ValueMapValue
    @Expose
    private String phoneNumber;
  }
}
