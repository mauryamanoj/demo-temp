package com.saudi.tourism.core.servlets;

import com.day.commons.datasource.poolservice.DataSourceNotFoundException;
import com.day.commons.datasource.poolservice.DataSourcePool;
import com.saudi.tourism.core.beans.ClientData;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.ClientDataService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.regex.Pattern;

/**
 * The is used to register likes and get likes count of a given event.
 */
@Component(service = Servlet.class,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Events Client Data "
               + "[Likes] Servlet",
               ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
                   + HttpConstants.METHOD_GET,
               ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
                   + "/bin/api/v1/eventsClientData"})
@Slf4j
public class EventsClientDataServlet extends SlingAllMethodsServlet {

  /**
   * datePattern.
   */
  private static Pattern datePattern = Pattern.compile(
      "^\\d{4}-\\d{2}-\\d{2}$");

  /**
   * The DataSourcePool.
   */
  @Reference
  private transient DataSourcePool dataSourcePool;

  /**
   * The ClientDataService.
   */
  @Reference
  private transient ClientDataService clientDataService;

  /**
   * doGet.
   *
   * @param request SlingHttpServletRequest
   * @param response SlingHttpServletResponse
   * @throws IOException .
   */
  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException {
    Connection connection = null;
    try {

      populateClientData(request);
      if (StringUtils.isEmpty(getClientIp(request))) {
        throw new IllegalArgumentException("Client IP can not be empty.");
      }

      String mtype = request.getParameter(Constants.M_TYPE);
      if (StringUtils.isEmpty(mtype)) {
        throw new IllegalArgumentException("Method type can not be empty.");
      }

      if (!(mtype.equalsIgnoreCase(Constants.INSERT))
          && !(mtype.equalsIgnoreCase(Constants.COUNT))) {
        throw new IllegalArgumentException("Invalid method type has been requested.");
      }

      ClientData clientData = populateClientData(request);
      if (mtype.equalsIgnoreCase(Constants.INSERT)) {
        connection = getConnection(dataSourcePool);
        if (clientDataService.insertClientData(connection, clientData) == 0) {
          CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
              new ResponseMessage(MessageType.SUCCESS.getType(), "Like has successfully been "
                  + "registered."));
        }
      }

      if (mtype.equalsIgnoreCase(Constants.COUNT)) {
        connection = getConnection(dataSourcePool);
        CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
            clientDataService.getClientDataCount(connection, clientData));
      }

    } catch (Exception e) {
      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
          new ResponseMessage(MessageType.ERROR.getType(), e.getLocalizedMessage()));
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException e) {
          LOGGER.error("Error while executing EventsClientDataServlet", e.getMessage());
        }
      }
    }

  }

  /**
   * This method is used to fetch client ip.
   *
   * @param request SlingHttpServletRequest
   * @return String .
   */
  protected String getClientIp(HttpServletRequest request) {
    String remoteAddr = "";
    if (request != null) {
      remoteAddr = request.getHeader(Constants.X_FORWARDED_FOR);
      if (remoteAddr == null || StringUtils.isEmpty(remoteAddr)) {
        remoteAddr = request.getRemoteAddr();
      }
    }
    return remoteAddr;
  }

  /**
   * This method is used to populate ClentData object.
   *
   * @param request SlingHttpServletRequest
   * @return ClientData .
   * @throws IllegalArgumentException exception.
   */
  protected ClientData populateClientData(final SlingHttpServletRequest request) {
    ClientData clientData = new ClientData();
    String ip = getClientIp(request);
    String latitude = request.getParameter(Constants.LATITUDE);
    String longitude = request.getParameter(Constants.LONGITUDE);
    String startDate = request.getParameter(Constants.START_DATE);

    if (StringUtils.isEmpty(ip)
        || StringUtils.isEmpty(latitude)
        || StringUtils.isEmpty(longitude)
        || StringUtils.isEmpty(startDate)) {
      throw new IllegalArgumentException("Invalid parameter found");
    }

    if (!matches(startDate)) {
      throw new IllegalArgumentException("Invalid startDate parameter found");
    }

    clientData.setStartDate(Date.valueOf(startDate));
    clientData.setIp(ip);
    clientData.setLatitude(Double.valueOf(latitude));
    clientData.setLongitude(Double.valueOf(longitude));
    return clientData;
  }

  /**
   * This method is used to get a connection using the configured DataSourcePool.
   *
   * @param dataSourcePool DataSourcePool
   * @return Connection .
   * @throws SQLException .
   * @throws DataSourceNotFoundException .
   */
  protected Connection getConnection(DataSourcePool dataSourcePool)
      throws SQLException, DataSourceNotFoundException {
    try {
      DataSource dataSource = (DataSource) dataSourcePool.getDataSource(Constants.CLIENT_DATA);
      return dataSource.getConnection();
    } catch (SQLException | DataSourceNotFoundException e) {
      LOGGER.error("Error while getting connection for EventsClientDataServlet", e.getMessage());
      throw e;
    }
  }

  /**
   * matches.
   * @param date String
   * @return boolean .
   */
  private static boolean matches(String date) {
    return datePattern.matcher(date).matches();
  }
}
