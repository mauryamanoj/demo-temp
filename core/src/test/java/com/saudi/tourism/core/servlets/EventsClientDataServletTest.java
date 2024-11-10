package com.saudi.tourism.core.servlets;

import com.day.commons.datasource.poolservice.DataSourceNotFoundException;
import com.day.commons.datasource.poolservice.DataSourcePool;
import com.saudi.tourism.core.beans.ClientData;
import com.saudi.tourism.core.services.ClientDataService;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class) public class EventsClientDataServletTest {

  private EventsClientDataServlet eventsClientDataServlet;
  private ClientDataService clientDataService;
  DataSourcePool dataSourcePool;
  DataSource dataSource;
  Connection connection;

  @BeforeEach public void setUp(AemContext context) throws SQLException,
      DataSourceNotFoundException {
    clientDataService = mock(ClientDataService.class);
    when(clientDataService.insertClientData(any(), any())).thenReturn(0);
    when(clientDataService.getClientDataCount(any(), any())).thenReturn(new ClientData());

    connection = mock(Connection.class);

    dataSource = mock(DataSource.class);
    when(dataSource.getConnection()).thenReturn(connection);

    dataSourcePool = mock(DataSourcePool.class);
    when(dataSourcePool.getDataSource(any())).thenReturn(dataSource);

    eventsClientDataServlet = new EventsClientDataServlet();
    Utils.setInternalState(eventsClientDataServlet, "clientDataService", clientDataService);
    Utils.setInternalState(eventsClientDataServlet, "dataSourcePool", dataSourcePool);
  }

  @Test public void testNoParams(AemContext context)
      throws IllegalArgumentException, IOException {
    eventsClientDataServlet.doGet(context.request(), context.response());
    Assertions.assertNotNull(context.response());
  }

  @Test public void testCountClientData(AemContext context) throws Exception {

    Map<String, Object> parameters = new HashMap<>();
    parameters.put("mtype", "count");
    parameters.put("startDate", "2019-11-28");
    parameters.put("latitude", "25");
    parameters.put("longitude", "35");
    context.request().setParameterMap(parameters);
    context.request().addHeader(Constants.X_FORWARDED_FOR, "127.0.0.1");

    eventsClientDataServlet.doGet(context.request(), context.response());
    Assertions.assertNotNull(context.response());
  }

  @Test public void testInsertClientData(AemContext context) throws Exception {

    Map<String, Object> parameters = new HashMap<>();
    parameters.put("mtype", "insert");
    parameters.put("startDate", "2019-11-28");
    parameters.put("latitude", "25");
    parameters.put("longitude", "35");
    context.request().setParameterMap(parameters);
    context.request().addHeader(Constants.X_FORWARDED_FOR, "127.0.0.1");

    eventsClientDataServlet.doGet(context.request(), context.response());
    Assertions.assertNotNull(context.response());
  }

  @Test public void testMtypeError(AemContext context) throws IOException {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("startDate", "2019-11-28");
    parameters.put("latitude", "25");
    parameters.put("longitude", "35");
    parameters.put("mtype", "");
    context.request().setParameterMap(parameters);
    context.request().addHeader(Constants.X_FORWARDED_FOR, "127.0.0.1");

    eventsClientDataServlet.doGet(context.request(), context.response());
    Assertions.assertNotNull(context.response());
  }

  @Test public void testWrongMtypeError(AemContext context) throws IOException {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("startDate", "2019-11-28");
    parameters.put("latitude", "25");
    parameters.put("longitude", "35");
    parameters.put("mtype", "delete");
    context.request().setParameterMap(parameters);
    context.request().addHeader(Constants.X_FORWARDED_FOR, "127.0.0.1");

    eventsClientDataServlet.doGet(context.request(), context.response());
    Assertions.assertNotNull(context.response());
  }

  @Test public void testWrongStartDateError(AemContext context) throws IOException {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("startDate", "11-1111-2811");
    parameters.put("latitude", "25");
    parameters.put("longitude", "35");
    parameters.put("mtype", "count");
    context.request().setParameterMap(parameters);
    context.request().addHeader(Constants.X_FORWARDED_FOR, "127.0.0.1");

    eventsClientDataServlet.doGet(context.request(), context.response());
    Assertions.assertNotNull(context.response());

  }

  @Test public void testError(AemContext context) throws IOException {

    eventsClientDataServlet.doGet(context.request(), context.response());
    Assertions.assertNotNull(context.response());
  }

  @Test public void testConnectionError(AemContext context) throws IOException, SQLException {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("mtype", "insert");
    parameters.put("startDate", "2019-11-28");
    parameters.put("latitude", "25");
    parameters.put("longitude", "35");
    context.request().setParameterMap(parameters);
    context.request().addHeader(Constants.X_FORWARDED_FOR, "127.0.0.1");

    when(dataSource.getConnection()).thenThrow(new SQLException());

    eventsClientDataServlet.doGet(context.request(), context.response());

    Assertions.assertNotNull(context.response());
  }

  @Test public void testConnectionCloseError(AemContext context) throws IOException, SQLException {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("mtype", "insert");
    parameters.put("startDate", "2019-11-28");
    parameters.put("latitude", "25");
    parameters.put("longitude", "35");
    context.request().setParameterMap(parameters);
    context.request().addHeader(Constants.X_FORWARDED_FOR, "127.0.0.1");

    doThrow(new SQLException()).when(connection).close();

    eventsClientDataServlet.doGet(context.request(), context.response());

    Assertions.assertNotNull(context.response());
  }
}
