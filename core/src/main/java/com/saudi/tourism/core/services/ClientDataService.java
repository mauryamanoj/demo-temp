package com.saudi.tourism.core.services;

import com.saudi.tourism.core.beans.ClientData;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This defines all methods ClientDataService.
 */
public interface ClientDataService {

  /**
   * Insert a new client record in the ClientData table (Like).
   *
   * @param connection Connection
   * @param clientData ClientData
   * @throws SQLException .
   * @return int
   */
  int insertClientData(Connection connection, ClientData clientData) throws SQLException;

  /**
   * Retrieves client data count from the ClientData table and returns based on the event
    clientData.
   * @param connection Connection
   * @param clientData ClientData
   * @throws SQLException .
   * @return ClientData
   */
  ClientData getClientDataCount(Connection connection, ClientData clientData) throws SQLException;
}
