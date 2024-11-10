package com.saudi.tourism.core.servlets;

import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * This is used to get all APP CLip content to be used by Mobil App Sample URL:
 * http://localhost:4502/.well-known/apple-app-site-association .
 */
@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "App Clip  Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
        + "/.well-known/apple-app-site-association" })
@Slf4j
public class AppClipServlet extends SlingAllMethodsServlet {

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {

    CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
        "{\n" + "    \"appclips\": {\n"
            + "        \"apps\": [\"WL4DKY7XSB.sa.sauditourism.iPhone.Clip\","
            + "\"WL4DKY7XSB.sa.sauditourism.app.dev.clip\", "
            + "\"WL4DKY7XSB.sa.sauditourism.iPhone.acc.clip\" ]\n" + "    }\n" + "} \n" + "");

  }

}
