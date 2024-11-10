package com.saudi.tourism.core.models.common;


import lombok.Getter;
import lombok.Setter;

/**
 * The type Object response.
 *
 * @param <T> the type parameter
 */
public class ObjectResponse<T> extends ResponseMessage {

  /**
   * The Data.
   */
  @Getter
  @Setter
  private T data;

  /**
   * Instantiates a new car response.
   *
   * @param status the status
   * @param data   the car model
   */
  public ObjectResponse(String status, T data) {
    this.setStatus(status);
    this.data = data;
  }

}
