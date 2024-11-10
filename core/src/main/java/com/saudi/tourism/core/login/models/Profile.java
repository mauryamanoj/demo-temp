package com.saudi.tourism.core.login.models;

import lombok.Data;

/**
 * The type Profile.
 */
@Data
public class Profile {
  /**
   * The Age range.
   */
  private String ageRange;

  /**
   * The First name.
   */
  private String firstName;

  /**
   * The Last name.
   */
  private String lastName;

  /**
   * The Gender code.
   */
  private String genderCode;

  /**
   * The Visa number.
   */
  private String visaNumber;

  /**
   * The Contact method list.
   */
  private ContactMethodList[] contactMethodList;

  /**
   * The Language code.
   */
  private String languageCode;

  /**
   * The Interests.
   */
  private String[] interests;

  /**
   * The Birth date.
   */
  private String birthDate;

  /**
   * The Travel partner.
   */
  private String travelPartner;

}
