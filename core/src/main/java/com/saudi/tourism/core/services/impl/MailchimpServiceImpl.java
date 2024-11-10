package com.saudi.tourism.core.services.impl;

import com.adobe.acs.commons.email.EmailService;
import com.google.gson.Gson;
import com.saudi.tourism.core.beans.MailchimpParams;
import com.saudi.tourism.core.beans.PackageFormParams;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.MailchimpService;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.RestHelper;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.saudi.tourism.core.services.MailchimpService.MailChimpServiceConfig.DESCRIPTION;
import static com.saudi.tourism.core.utils.Constants.DEFAULT_LOCALE;
import static com.saudi.tourism.core.utils.SpecialCharConstants.COLON;

/**
 * Implementation of the Mailchimp service for managing subscriptions.
 */
@Component(service = MailchimpService.class,
           immediate = true,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + DESCRIPTION})
@Designate(ocd = MailchimpService.MailChimpServiceConfig.class)
@Slf4j
public class MailchimpServiceImpl implements MailchimpService {

  /**
   * Any string as a user name for basic authorization.
   */
  private static final String BASIC_AUTH_NAME = "Saudi-Mailchimp";

  /**
   * Mailchimp URL part: "/lists/".
   */
  private static final String URL_PART_LISTS = "/lists/";

  /**
   * Mailchimp URL part: "/members/".
   */
  private static final String URL_PART_MEMBERS = "/members/";

  /**
   * Node path where email templates are stored.
   */
  private static final String CAMPAIGN_EMAIL_TEMPLATES_DIR =
      "/content/campaigns/saudi-tourism/packages-template/";

  /**
   * Mailchimp service url (https://us10.api.mailchimp.com/3.0/lists/{listId}/members/).
   */
  private String url;

  /**
   * Base64 basic authentication token.
   */
  private String basicAuthToken = null;
  /**
   * BnotifyEmail.
   */
  private String notifyEmail;

  /**
   * The Email service.
   */
  @Reference
  private EmailService emailService;

  /**
   * Localization bundle provider.
   */
  @Reference(target = Constants.I18N_PROVIDER_SERVICE_TARGET)
  private ResourceBundleProvider i18nProvider;

  /**
   * The read only resource resolver provider.
   */
  @Reference
  private UserService resourceResolverProvider;

  /**
   * Sets up the service upon activation.
   *
   * @param config the OSGi component context
   */
  @Activate protected void activate(final MailChimpServiceConfig config) {
    String mailchimpUrl = config.url();
    final String mailchimpListId = config.listId();
    final String mailchimpApiKey = config.apiKey();
    notifyEmail = config.notifyEmail();

    if (StringUtils.isAnyBlank(mailchimpUrl, mailchimpListId)) {
      LOGGER.error("Mailchimp service is not configured");
      return;
    }

    this.url = getMailchimpMembersUrl(config.url(), config.listId());
    this.basicAuthToken = new String(Base64.encodeBase64(
        (BASIC_AUTH_NAME + COLON + mailchimpApiKey).getBytes(StandardCharsets.ISO_8859_1)));
  }

  /**
   * Produces correct Mailchimp members url
   * (https://us10.api.mailchimp.com/3.0/lists/{listId}/members/).
   *
   * @param configUrl url specified in configuration
   * @param listId    subscribers list id
   * @return proper url
   */
  String getMailchimpMembersUrl(@NotNull final String configUrl, @NotNull final String listId) {
    String resultUrl = configUrl;

    final int removePos = configUrl.indexOf(URL_PART_LISTS);
    if (removePos >= 0) {
      resultUrl = configUrl.substring(0, removePos);
    } else {
      resultUrl = StringUtils.removeEnd(resultUrl, Constants.FORWARD_SLASH_CHARACTER);
    }

    // Produced url: https://us10.api.mailchimp.com/3.0/lists/{listId}/members/
    return resultUrl + URL_PART_LISTS + listId + URL_PART_MEMBERS;
  }

  @Override
  public ResponseMessage addSubscription(final PackageFormParams formParams, final String vendor)
      throws IOException {
    String locale = formParams.getLocale();
    locale = Optional.ofNullable(locale).filter(StringUtils::isNotBlank).orElse(DEFAULT_LOCALE);
    String subject = "package-form-subject";
    String dmcEmailAddr = null;

    // I18n of necessary strings
    final ResourceBundle i18n = i18nProvider.getResourceBundle(new Locale(locale));
    subject = i18n.getString(subject);

    final String nationality = formParams.getNationality();
    if (StringUtils.isNotBlank(nationality)) {
      formParams.setNationality(i18n.getString(nationality));
    }

    // read DMC vendor name and email from i18n
    dmcEmailAddr = i18n.getString(vendor + "-email");
    formParams.setDmc(i18n.getString(vendor));

    final MailchimpParams mailchimpParams = new MailchimpParams(formParams);
    // Send subscription request to Mailchimp
    final ResponseMessage responseMessage = addSubscriptionInternal(mailchimpParams);

    final String emailTemplate = getEmailTemplatePath(locale);
    // Send email if the contact subscribed successfully
    if (responseMessage.getStatusCode() < HttpStatus.SC_BAD_REQUEST) {

      LOGGER.debug("Subscription was ok - sending {} email", vendor);

      final Map<String, String> emailParams = new HashMap<>(mailchimpParams.getFields());


      StrSubstitutor sub = new StrSubstitutor(emailParams, "{", "}");
      subject = sub.replace(subject);
      // Additional params
      emailParams.put(com.adobe.cq.mcm.campaign.Constants.PN_MAIL_SUBJECT, subject);
      emailParams.put("executedAt", Calendar.getInstance().getTime().toString());

      // Default answer when subscribed & email was sent
      String statusMessage = "Subscribed successfully";
      if (dmcEmailAddr != null && !(vendor + "-email").equals(dmcEmailAddr)) {
        try {
          final List<String> failureList =
              emailService.sendEmail(emailTemplate, emailParams, dmcEmailAddr);

          if (!failureList.isEmpty()) {
            // Subscribed, but email wasn't sent because of failure in EmailService
            statusMessage = "Subscribed successfully, DMC email was not sent (Failure)";
            LOGGER.error(statusMessage + " {}", failureList);
            notify(emailParams, emailTemplate, statusMessage);
          }
        } catch (Exception e) {
          // Subscribed, but email wasn't sent because of exception
          statusMessage = "Subscribed successfully, DMC email was not sent (Exception)";
          notify(emailParams, emailTemplate, statusMessage);
          LOGGER.error(statusMessage, e);
        }
      } else {
        notify(emailParams, emailTemplate, "Skipping the email sending");
      }


      // Answer as success
      return new ResponseMessage(HttpStatus.SC_OK, MessageType.SUCCESS.getType(), statusMessage);
    } else {
      notify(new HashMap<>(), emailTemplate,
          "Error in MailChimp" + formParams.toString() + (new Gson()).toJson(responseMessage));
    }

    // If error happened, pass it unchanged (CHECKME needs to be checked if ok)
    return responseMessage;
  }

  /**
   * Notify.
   *
   * @param emailParams   the email params
   * @param emailTemplate the email template
   * @param error         the error
   */
  private void notify(final Map<String, String> emailParams, final String emailTemplate,
      String error) {
    try {
      LOGGER.error(error);

      emailParams.put(com.adobe.cq.mcm.campaign.Constants.PN_MAIL_SUBJECT, error);
      emailService.sendEmail(emailTemplate, emailParams, notifyEmail);
    } catch (Exception e) {
      LOGGER.error("Error in Notifying");
    }
  }

  /**
   * Send a POST request to configured Mailchimp URL to add a new subscription using provided
   * MailchimpParams instance.
   *
   * @param params email of the user to add a subscription
   * @return response string from the Mailchimp service
   * @throws IOException if can't connect
   */
  private ResponseMessage addSubscriptionInternal(final MailchimpParams params) throws IOException {
    if (StringUtils.isBlank(this.url)) {
      final String errorMessage = "Mailchimp service is not configured";
      LOGGER.error(errorMessage);
      throw new IllegalStateException(errorMessage);
    }

    LOGGER.debug("Subscribing user {} in Mailchimp service", params.getEmail());

    final String json = RestHelper.getObjectMapper().writeValueAsString(params);
    final StringEntity body = new StringEntity(json, StandardCharsets.UTF_8);

    final String emailMd5Hash = DigestUtils.md5Hex(params.getEmail()).toUpperCase();
    return executePut(this.url + emailMd5Hash, body);
  }

  /**
   * Returns path to proper email template for the locale (if not found returns en.txt).
   *
   * @param locale language to check
   * @return proper email templates path
   */
  private String getEmailTemplatePath(final String locale) {
    String emailTemplate = null;

    if (StringUtils.isNotBlank(locale)) {
      final String checkTemplatePath =
          CAMPAIGN_EMAIL_TEMPLATES_DIR + locale + Constants.HTML_EXTENSION;
      try (ResourceResolver resolver = resourceResolverProvider.getResourceResolver()) {
        // Check the template exists for the specified locale
        if (resolver.getResource(checkTemplatePath) != null) {
          emailTemplate = checkTemplatePath;
        }
      }
    }
    // Use default template (en.txt) if locale template is not found or locale wasn't passed
    if (emailTemplate == null) {
      emailTemplate =
          CAMPAIGN_EMAIL_TEMPLATES_DIR + DEFAULT_LOCALE + Constants.HTML_EXTENSION;
    }

    return emailTemplate;
  }

  /**
   * Method executes put request using RestHelper, extracted here to skip it in unit testing.
   *
   * @param url  request url
   * @param body request body
   * @return response message object
   * @throws IOException if can't connect
   */
  @Generated
  @NotNull
  private ResponseMessage executePut(final String url,
      final AbstractHttpEntity body) throws IOException {
    return RestHelper.executeMethodPut(url, body, basicAuthToken, false);
  }
}
