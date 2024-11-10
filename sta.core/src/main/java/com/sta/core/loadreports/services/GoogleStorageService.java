package com.sta.core.loadreports.services;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;

import com.sta.core.MmCoreException;

/**
 * Google Storage client Service, is used to download reports from there.
 */
public interface GoogleStorageService {

  /**
   * Download file from Google Storage.
   * @param bucketName bucket name
   * @param serviceAccount service account
   * @param privateKey private key
   * @param filepath filepath
   * @return bytes
   * @throws GeneralSecurityException error
   * @throws IOException error
   * @throws MmCoreException error
   */
  byte[] download(String bucketName, String serviceAccount, PrivateKey privateKey, String filepath)
      throws GeneralSecurityException, IOException, MmCoreException;
}
