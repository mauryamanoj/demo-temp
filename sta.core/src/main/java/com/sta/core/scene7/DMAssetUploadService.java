package com.sta.core.scene7;

import com.saudi.tourism.core.models.common.ResponseMessage;

/**
 * Dynamic Media Asset Upload Service.
 */
public interface DMAssetUploadService {
  /**
   * Upload assets.
   *
   * @param base64Date the base 64 date
   * @param path       the path
   * @param vendor     the vendor
   * @return response response message
   */
  ResponseMessage uploadAsset(String base64Date, String path, String vendor);
}
