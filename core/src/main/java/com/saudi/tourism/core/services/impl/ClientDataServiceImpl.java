package com.saudi.tourism.core.services.impl;

import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import java.sql.Date;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import com.day.commons.datasource.poolservice.DataSourcePool;
import com.saudi.tourism.core.beans.ClientData;
import com.saudi.tourism.core.services.ClientDataService;
import com.saudi.tourism.core.utils.Constants;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This is the implementation of ClientDataService.
 */
@Component
    (service = ClientDataService.class, immediate = true)
@Slf4j
public class ClientDataServiceImpl implements ClientDataService {

  /**
   * The DataSourcePool source.
   */
  @Reference private DataSourcePool source;

  /**
   * Insert ClientData (Like).
   *
   * @param connection Connection
   * @param clientData ClientData
   * @throws SQLException .
   * @return int .
   */
  @Generated
  @Override public int insertClientData(final Connection connection, final ClientData clientData)
      throws SQLException {
    String insert = "INSERT INTO ClientData(startDate,latitude,longitude,ip,addedDate) "
          + "VALUES(?, ?, ?, ?,?);";
    try (PreparedStatement pstmt = connection.prepareStatement(insert)) {
      pstmt.setDate(Constants.ONE, clientData.getStartDate());
      pstmt.setDouble(Constants.TWO, clientData.getLatitude());
      pstmt.setDouble(Constants.THREE, clientData.getLongitude());
      pstmt.setString(Constants.FOUR, clientData.getIp());
      pstmt.setDate(Constants.FIVE, new Date(System.currentTimeMillis()));
      pstmt.execute();
    }
    return Constants.ZERO;
  }


  /**
   * Get Likes count for a given event.
   *
   * @param connection Connection
   * @param clientData ClientData
   * @throws SQLException .
   * @return ClientData .
   */
  @Generated
  @Override public ClientData getClientDataCount(final Connection connection,
      final ClientData clientData) throws SQLException {
    int likesCount = Constants.ZERO;
    String sqlString = "SELECT COUNT(*) as likesCount FROM ClientData WHERE startDate = ? AND "
        + "latitude = ? AND longitude = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(sqlString)) {
      pstmt.setString(Constants.ONE, clientData.getStartDate().toString());
      pstmt.setString(Constants.TWO, clientData.getLatitude().toString());
      pstmt.setString(Constants.THREE, clientData.getLongitude().toString());
      try (ResultSet resultSet = pstmt.executeQuery()) {
        while (resultSet.next()) {
          likesCount = resultSet.getInt(Constants.LIKES_COUNT);
        }
      }
    }
    clientData.setLikesCount(likesCount);

    return clientData;
  }

}
