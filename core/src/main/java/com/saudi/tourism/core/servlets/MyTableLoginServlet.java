package com.saudi.tourism.core.servlets;

import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.MyTableLoginService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;
import java.io.PrintWriter;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

/**
 * The type Get all hotels for a locale.
 */
@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "MyTable Login Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_POST,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/v1/mytable/token"})
@Slf4j
public class MyTableLoginServlet extends SlingAllMethodsServlet {

  /**
   * The MyTableLoginConfig service.
   */
  @Reference
  private transient MyTableLoginService myTableLoginService;

  /**
   * The sling repo.
   */
  @Reference
  private SlingRepository repository;

  @Override
  protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException {
    boolean isAuthenticated = CommonUtils.validateBasicAuth(request, repository);
    if (isAuthenticated) {
      try {
        PrintWriter pw = response.getWriter();
        pw.write(myTableLoginService.loginAndGetResponse());
        pw.flush();
      } catch (IOException e) {
        LOGGER.error("Error during login to MyTable: ", e);
        CommonUtils.writeJSON(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
            new ResponseMessage(SC_INTERNAL_SERVER_ERROR, MessageType.ERROR.getType(),
                e.getLocalizedMessage()));
      }
    } else {
      LOGGER.error("mytable/token not Authorized");
      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
          new ResponseMessage(MessageType.ERROR.getType(), "Not Authorized"));
    }
  }
}
