package com.saudi.tourism.core.servlets;

import com.day.cq.commons.jcr.JcrConstants;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.jcr.version.VersionManager;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static com.day.cq.replication.ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED;

/**
 * Servlet that retrieves the last published versionId.
 * http://localhost:4502/bin/api/v1/version./content/sauditourism/en/packages/package1
 */
@Component(service = Servlet.class,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Version Servlet",
               ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
                   + HttpConstants.METHOD_GET,
               ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
                   + "/bin/api/v1/version"})
@Slf4j
public class VersionServlet extends SlingAllMethodsServlet {
  @Override protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException {
    String path = request.getRequestPathInfo().getSuffix();
    if (!StringUtils.isEmpty(path)) {
      try {

        Resource page = request.getResourceResolver()
            .resolve(path + Constants.FORWARD_SLASH_CHARACTER + JcrConstants.JCR_CONTENT);
        ValueMap properties = page.getValueMap();
        Date lastReplicated = properties.get(NODE_PROPERTY_LAST_REPLICATED, Date.class);
        if (lastReplicated == null) {
          throw new ServletException("not published");
        }
        String vid = findIdentifierWithMaxCreatedDate(request);
        ResponseMessage responseMessage = new ResponseMessage(MessageType.SUCCESS.getType(), vid);
        if (vid == null) {
          responseMessage.setStatus(MessageType.ERROR.getType());
        }
        CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(), responseMessage);
      } catch (Exception e) {
        CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
            new ResponseMessage(MessageType.ERROR.getType(),
                "No version information for the page"));
      }
    }
  }

  /**
   * Find identifier with max CreatedDate.
   * @param request request
   * @return identifier
   * @throws RepositoryException error
   */
  private String findIdentifierWithMaxCreatedDate(SlingHttpServletRequest request)
      throws RepositoryException {
    String vid = Constants.BLANK;
    Calendar calendar = null;
    String path = request.getRequestPathInfo().getSuffix();
    Session session = request.getResourceResolver().adaptTo(Session.class);
    VersionManager vm = session.getWorkspace().getVersionManager();
    VersionHistory history = vm.getVersionHistory(
        path + Constants.FORWARD_SLASH_CHARACTER + JcrConstants.JCR_CONTENT);
    VersionIterator it = history.getAllVersions();
    while (it.hasNext()) {
      Version version = (Version) it.next();
      if (!JcrConstants.JCR_ROOTVERSION.equals(version.getName())) {
        if (calendar == null) {
          calendar = version.getCreated();
          vid = version.getIdentifier();
        }
        if (version.getCreated().compareTo(calendar) > 0) {
          calendar = version.getCreated();
          vid = version.getIdentifier();
        }
      }
    }
    return vid;
  }
}
