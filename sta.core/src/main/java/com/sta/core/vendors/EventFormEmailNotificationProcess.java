package com.sta.core.vendors;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.jcr.RepositoryException;

import com.adobe.acs.commons.email.EmailService;
import com.adobe.granite.security.user.UserProperties;
import com.adobe.granite.security.user.UserPropertiesManager;
import com.adobe.granite.security.user.util.AuthorizableUtil;
import com.day.cq.commons.Externalizer;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.HistoryItem;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.CommonUtils;

import org.apache.commons.lang.WordUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import static com.sta.core.vendors.ScriptConstants.VENDOR_KEY;

/**
 * The Workflow Process step to send HTML email upon Workflow trigger.
 */
@Component(service = {WorkflowProcess.class},
           property = {
               Constants.SERVICE_DESCRIPTION + "=" + EventFormEmailNotificationProcess.DESCRIPTION,
               "process.label" + "=" + EventFormEmailNotificationProcess.PROCESS_LABEL})
@Slf4j
public class EventFormEmailNotificationProcess implements WorkflowProcess {
  /**
   * The Description.
   */
  static final String DESCRIPTION = "Sends email notification to groups after workflow is finished";

  /**
   * The Process label.
   */
  static final String PROCESS_LABEL = "EventFormEmailNotificationProcess";
  /**
   * The constant PROFILE_EMAIL.
   */
  private static final String PROFILE_EMAIL = "profile/email";

  /**
   * The User service.
   */
  @Reference
  private UserService userService;

  /**
   * The Email service.
   */
  @Reference
  private EmailService emailService;

  /**
   * The read only resource resolver provider.
   */
  @Reference
  private UserService resourceResolverProvider;

  /**
   * The Externalizers.
   */
  @Reference
  protected Externalizer externalizers;

  /**
   * Node path where email templates are stored.
   */
  private static final String CAMPAIGN_EMAIL_TEMPLATES_DIR =
      "/content/campaigns/saudi-tourism/vendor-forms-email-template/en.html";

  @Override
  public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap args)
      throws WorkflowException {
    try (ResourceResolver resolver = userService.getWritableResourceResolver()) {

      String grpId = null;
      List<HistoryItem> historyList = workflowSession.getHistory(workItem.getWorkflow());
      String link = workItem.getWorkflow().getWorkflowData().getPayload().toString();
      if (args.containsKey("PROCESS_ARGS")) {

        grpId = args.get("PROCESS_ARGS", VENDOR_KEY);
        LOGGER.info("grpId: {}", grpId);
        if (VENDOR_KEY.equals(grpId)) { // for dynamic vendors notification
          grpId = CommonUtils.getVendorForPath(link);
        }
      }
      if (grpId != null) {


        Set<Authorizable> users = getParticipants(grpId, resolver);
        for (Authorizable user : users) {

          if (user.hasProperty(PROFILE_EMAIL)) {
            LOGGER.info("email: " + user.getProperty(PROFILE_EMAIL)[0].getString());
            String userEmail = user.getProperty(PROFILE_EMAIL)[0].getString();

            String emailTemplate = null;


            final String checkTemplatePath = CAMPAIGN_EMAIL_TEMPLATES_DIR;
            try (ResourceResolver resourceResolver = resourceResolverProvider
                .getResourceResolver()) {
              //Check the template exists for the specified locale
              if (resourceResolver.getResource(checkTemplatePath) != null) {
                emailTemplate = checkTemplatePath;
              }
            }

            buildEmailTemplateVariables(workItem, args, historyList, link, user, userEmail,
                emailTemplate);

          }
        }
      }

    } catch (Exception e) {
      LOGGER.error("Could not get resource resolver");
    }

  }

  /**
   * Build email template variables.
   *
   * @param workItem      the work item
   * @param args          the args
   * @param historyList   the history list
   * @param link          the link
   * @param user          the user
   * @param userEmail     the user email
   * @param emailTemplate the email template
   */
  private void buildEmailTemplateVariables(final WorkItem workItem, final MetaDataMap args,
      final List<HistoryItem> historyList, final String link, final Authorizable user,
      final String userEmail, final String emailTemplate) {
    ResourceResolver resourceResolver = null;
    try {
      resourceResolver = resourceResolverProvider.getWritableResourceResolver();
      HashMap<String, String> emailParams = new HashMap<>();
      emailParams.put("USEREMAIL", userEmail);

      String step = workItem.getNode().getTitle();
      emailParams.put("STEP", step);

      String lang = CommonUtils.getLanguageForPath(link).toUpperCase();
      emailParams.put("LANG", lang);
      emailParams.put("LINK",
          externalizers.externalLink(resourceResolver,
              "local", "/editor.html" + link + ".html"));
      String sender  = user.getID();
      if (args.containsKey("PROCESS_SUBMIT_GROUP")) {

        sender = args.get("PROCESS_SUBMIT_GROUP", VENDOR_KEY);
        LOGGER.info("sender: {}", sender);

      }
      emailParams.put("SENDER", sender);
      int listSize = historyList.size();
      HistoryItem lastItem = historyList.get(listSize - 1);
      String comment = lastItem.getComment();

      if (Objects.isNull(comment) || comment.isEmpty()) {
        comment = "";
      }
      emailParams.put("COMMENT", comment);

      String[] payloadPath = link.split("/");
      String pageName = payloadPath[payloadPath.length - 1]
          .replace("-", com.saudi.tourism.core.utils.Constants.SPACE);
      pageName = WordUtils.capitalize(pageName);
      emailParams.put("EVENTTITLE", pageName);

      String subject =
          "Package submission - " + pageName + " - "  + lang + " - " + step;
      emailParams.put(com.adobe.cq.mcm.campaign.Constants.PN_MAIL_SUBJECT, subject);

      final List<String> failureList =
          emailService.sendEmail(emailTemplate, emailParams, userEmail);

      if (!failureList.isEmpty()) {
        LOGGER.error("Email for page {} not sent to {}", pageName, failureList);
      }
    } catch (Exception e) {
      LOGGER.error("Error while sending email to {}", userEmail, e);
    } finally {
      if (resourceResolver.isLive()) {
        resourceResolver.close();
      }
    }
  }

  /**
   * Gets participants.
   * Code copied from cq-workflow-impl jar.
   *
   * @param userGroupId the user group id
   * @param resolver    the resolver
   * @return the participants
   */
  private static Set<Authorizable> getParticipants(String userGroupId, ResourceResolver resolver) {
    Set<Authorizable> auths = new HashSet<>();
    if ((userGroupId != null) && (userGroupId.length() > 0)) {
      UserManager um = resolver.adaptTo(UserManager.class);
      if (um != null) {
        Authorizable participant = getAuthorizable(um, userGroupId);
        auths.addAll(getParticipants(participant, resolver));
      }
    }
    return auths;
  }

  /**
   * Gets authorizable.
   * Code copied from cq-workflow-impl jar.
   *
   * @param manager the manager
   * @param userId  the user id
   * @return the authorizable
   */
  private static Authorizable getAuthorizable(UserManager manager, String userId) {
    if ((null != manager) && (null != userId)) {
      if ("system".equals(userId)) {
        userId = "admin";
      }
      try {
        return manager.getAuthorizable(userId);
      } catch (RepositoryException e) {
        LOGGER.warn("User Manager was unable to find the User: {}.", userId, e);
      }
    } else {
      LOGGER.warn("user manager or user id unavailable: {} - {}", manager, userId);
    }
    return null;
  }

  /**
   * Gets participants.
   * Code copied from cq-workflow-impl jar.
   *
   * @param userGroup the user group
   * @param resolver  the resolver
   * @return the participants
   */
  private static Set<Authorizable> getParticipants(Authorizable userGroup,
      ResourceResolver resolver) {
    Set<Authorizable> participants = new HashSet<>();
    if (Objects.isNull(userGroup)) {
      return participants;
    }
    if (!userGroup.isGroup()) {
      try {
        LOGGER.debug("Participant is a user.", userGroup.getID());
        String userID = userGroup.getID();
        String email = getEmailAddress(userGroup.getID(), resolver);

        if (email != null) {
          LOGGER.debug("extracted user {} with email {} from workflow step.", userID, email);
          participants.add(userGroup);
        } else {
          LOGGER.debug(
              "extracted user {} which has no email, not sending notification to this user.",
              userID);
        }
      } catch (RepositoryException e) {
        LOGGER.error("Unable to look up user's ID.", e);
      }
    } else {
      Group group = (Group) userGroup;
      Iterator<Authorizable> members = null;
      try {
        members = group.getMembers();
        LOGGER.debug("Participant is a group: {}.  Will recurse", group.getID());
      } catch (RepositoryException e) {
        LOGGER.error("Unable to query for members of group.", e);
      }

      while ((members != null) && (members.hasNext())) {
        Authorizable member = members.next();
        participants.addAll(getParticipants(member, resolver));
      }
    }
    return participants;
  }

  /**
   * Gets email address.
   * Code copied from cq-workflow-impl jar.
   *
   * @param userID   the user id
   * @param resolver the resolver
   * @return the email address
   * @throws RepositoryException the repository exception
   */
  private static String getEmailAddress(String userID, ResourceResolver resolver)
      throws RepositoryException {
    UserProperties userProperties = readUserProfiles(resolver, userID);

    String mail = null;
    if (userProperties != null) {
      mail = userProperties.getProperty("email");
      if ((mail != null) && (mail.length() > 0)) {
        LOGGER.debug("Found email address: '{}' in profile.", mail);
        return mail;
      }
    } else {
      LOGGER.debug("User {} does not have a profile.  Unable to get email address", userID);
    }
    return mail;
  }

  /**
   * Read user profiles user properties.
   * Code copied from cq-workflow-impl jar.
   *
   * @param resolver the resolver
   * @param userID   the user id
   * @return the user properties
   */
  private static UserProperties readUserProfiles(ResourceResolver resolver, String userID) {
    UserPropertiesManager upm = resolver.adaptTo(UserPropertiesManager.class);
    try {
      UserProperties props = AuthorizableUtil.getProfile(upm, userID);
      if (props != null) {
        return props;
      }
    } catch (RepositoryException e) {
      LOGGER.debug("No user profiles found for: {}", userID, e);
    }
    return null;
  }
}
