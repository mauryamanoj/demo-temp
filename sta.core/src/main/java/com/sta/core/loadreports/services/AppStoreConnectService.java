package com.sta.core.loadreports.services;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.PrivateKey;
import java.util.Map;

import com.sta.core.MmCoreException;

/**
 * App Store Connect service.
 */
public interface AppStoreConnectService {

  /**
   * Load a report from App Store Connect.
   *
   * @param path         - report path, ex. '/v1/salesReports'
   * @param reportParams - report params, ex. {"filter[frequency]" -> "DAILY",
   *                     "filter[reportType]"-> "SALES"}
   * @param keyID        - key ID from App Store Connect
   * @param issuer       - issuer from App Store Connect
   * @param privateKey   - private key for JWT
   * @return bytes of reports
   * @throws MmCoreException - exception
   * @throws URISyntaxException - exception
   * @throws IOException - exception
   */
  byte[] loadReport(String path, Map<String, String> reportParams, String keyID, String issuer,
      PrivateKey privateKey) throws MmCoreException, URISyntaxException, IOException;
}
