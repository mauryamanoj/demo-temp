package com.saudi.tourism.core.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.saudi.tourism.core.services.EmailNotificationService;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.Constants;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * EmailNotificationService Implementation.
 */
@Component(service = EmailNotificationService.class,
    immediate = true)
public class EmailNotificationServiceImpl implements EmailNotificationService {

  /**
   * logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(EmailNotificationServiceImpl.class);

  /**
   * SMS Templates Path.
   */
  public static final String EMAIL_TEMPLATE_PATHS = "/apps/sauditourism/i18n/email/";

  /**
   * File name.
   */

  public static final String FILE_NAME = "template.txt";

  @Override
  public String getEmailTemplate(UserService service, SlingHttpServletResponse response, String locale) {

    String templatePath;
    String templateText = null;
    templatePath = EMAIL_TEMPLATE_PATHS + locale + Constants.FORWARD_SLASH_CHARACTER
        + FILE_NAME + Constants.FORWARD_SLASH_CHARACTER + JcrConstants.JCR_CONTENT;
    try (ResourceResolver resourceResolver = service.getResourceResolver()) {
      Resource resource = resourceResolver.getResource(templatePath);
      if (resource != null) {
        templateText = readTextFromTemplate(resource);
      }
    } catch (Exception ex) {
      LOG.error("Exception while getting email template.", ex);
    }
    return templateText;
  }

  /**
   * Read text from template .
   *
   * @param resource resource.
   * @return String
   */
  private String readTextFromTemplate(Resource resource) {
    StringBuilder strBuilder = null;
    try {
      Node node = resource.adaptTo(Node.class);
      InputStream is = node.getProperty(JcrConstants.JCR_DATA).getBinary().getStream();

      String readLine;
      BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
      strBuilder = new StringBuilder();

      while (((readLine = br.readLine()) != null)) {
        strBuilder.append(readLine);
      }
    } catch (ValueFormatException e) {
      LOG.error("Error Coming at While reading the Email Notification Service {}", e.getStackTrace());
    } catch (PathNotFoundException e) {
      LOG.error("Error Coming at While reading the Email Notification Service {}", e.getStackTrace());
    } catch (RepositoryException e) {
      LOG.error("Error Coming at While reading the Email Notification Service {}", e.getStackTrace());
    } catch (IOException e) {
      LOG.error("Error Coming at While reading the Email Notification Service {}", e.getStackTrace());
    }
    return strBuilder.toString();
  }

}
