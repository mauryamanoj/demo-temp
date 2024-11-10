package com.saudi.tourism.core.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Bean for the Mailchimp service request (for producing json).
 */
@Data
public class MailchimpParams implements Serializable {

  /**
   * Mailchimp property name for email.
   */
  public static final String PROP_EMAIL = "EMAIL";

  /**
   * Mailchimp property name for first name.
   *
   * @noinspection SpellCheckingInspection
   */
  public static final String PROP_FNAME = "FNAME";

  /**
   * Mailchimp property name for last name.
   *
   * @noinspection SpellCheckingInspection
   */
  public static final String PROP_LNAME = "LNAME";

  /**
   * Mailchimp property name for phone number.
   */
  public static final String PROP_PHONE = "PHONE";

  /**
   * Mailchimp property name for nationality.
   */
  public static final String PROP_NATIONAL = "NATIONAL";

  /**
   * Mailchimp property name for user's comment.
   */
  public static final String PROP_COMMENT = "COMMENT";

  /**
   * Property for requested package URL.
   */
  public static final String PROP_URL = "URL";

  /**
   * Property for DMC Vendor.
   */
  public static final String PROP_DMC = "DMC";

  /**
   * Property for users's locale.
   */
  public static final String PROP_LOCALE = "LOCALE";

  /**
   * Subscriber email.
   */
  @JsonProperty("email_address")
  private String email;

  /**
   * Subscription status: "subscribed", "pending", "unsubscribed" or "cleaned".
   */
  private String status = "subscribed";

  /**
   * Subscription status for edit (put) requests, possible values are the same as for status.
   */
  @JsonProperty("status_if_new")
  private String statusIfNew = "subscribed";

  /**
   * List of the contact properties.
   */
  @JsonProperty("merge_fields")
  private Map<String, String> fields = new HashMap<>();

  /**
   * Constructor that converts PackageFormParams into MailchimpParams.
   *
   * @param formParams PackageFormParams instance
   */
  public MailchimpParams(@NotNull PackageFormParams formParams) {
    final String emailAddress = formParams.getEmail();
    setEmail(emailAddress);

    // Put values into map only if they are not null
    fields.compute(PROP_EMAIL, (k, v) -> emailAddress);
    fields.compute(PROP_FNAME, (k, v) -> formParams.getFirstName());
    fields.compute(PROP_LNAME, (k, v) -> formParams.getLastName());
    fields.compute(PROP_PHONE, (k, v) -> formParams.getPhone());
    fields.compute(PROP_NATIONAL, (k, v) -> formParams.getNationality());
    fields.compute(PROP_COMMENT, (k, v) -> formParams.getComment());
    fields.compute(PROP_DMC, (k, v) -> formParams.getDmc());

    final String url = formParams.getUrl();
    if (StringUtils.isNotBlank(url)) {
      fields.put(PROP_URL, Constants.VISITSAUDI_DOMAIN_COM + url
          .replace(Constants.ROOT_CONTENT_PATH, StringUtils.EMPTY));
      fields.put(PROP_LOCALE, CommonUtils.getLanguageForPath(url));
    }
  }
}
