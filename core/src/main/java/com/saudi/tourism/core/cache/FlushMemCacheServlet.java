package com.saudi.tourism.core.cache;

import com.saudi.tourism.core.models.common.ResponseMessage;
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
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * This Flush MemCache Servlet.
 */
@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Flush MemCache Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/flush/memcache"})
@Slf4j
public class FlushMemCacheServlet extends SlingAllMethodsServlet {
  /**
   * The Mem cache.
   */
  @Reference
  private transient Cache memCache;
  /**
   * The sling repo.
   */
  @Reference
  private SlingRepository repository;

  @Override
  public void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {
    boolean isAuthenticated = CommonUtils.validateBasicAuth(request, repository);
    if (isAuthenticated) {
      memCache.clear();
      LOGGER.info("Memory Cache Flushed");
      response.getWriter().write("Memory Cache Flushed");
    } else {
      LOGGER.error("Memory Cache Flush not Authorized");
      LOGGER.error("Memory Cache Flush not Authorized");
      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
          new ResponseMessage(MessageType.ERROR.getType(), "Not Authorized"));
    }
  }

}
