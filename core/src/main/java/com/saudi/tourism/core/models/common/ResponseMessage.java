package com.saudi.tourism.core.models.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;


/**
 * This is to define properties of ResponseMessage.
 */
@ToString
public class ResponseMessage implements Serializable {

  /**
   * The status.
   */
  @Getter
  @Setter
  private String status;

  /**
   * The status code.
   */
  //    @JsonIgnore
  @Getter
  @Setter
  private Integer statusCode;

  /**
   * The message.
   */
  @Getter
  @Setter
  private String message;


  /**
   * only usage for native app setID.
   */
  @Setter
  @Getter
  private String id;

  /**
   * Constructor.
   */
  public ResponseMessage() {
  }

  /**
   * Instantiates a new response message.
   *
   * @param statusCode the status code
   */
  public ResponseMessage(int statusCode) {
    this.statusCode = statusCode;
  }

  /**
   * Instantiates a new response message.
   *
   * @param statusCode the status code
   * @param message    the message
   */
  public ResponseMessage(int statusCode, String message) {
    this.statusCode = statusCode;
    this.message = message;
  }

  /**
   * Constructor.
   *
   * @param status  the status
   * @param message the message
   */
  public ResponseMessage(String status, String message) {
    this.status = status;
    this.message = message;
  }

  /**
   * Constructor.
   *
   * @param statusCode the statusCode
   * @param status     the status
   * @param message    the message
   */
  public ResponseMessage(int statusCode, String status, String message) {
    this.statusCode = statusCode;
    this.status = status;
    this.message = message;
  }
}
