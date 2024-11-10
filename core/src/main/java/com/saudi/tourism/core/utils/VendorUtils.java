package com.saudi.tourism.core.utils;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.RepositoryException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Utility class for vendor.
 */
@Slf4j
public final class VendorUtils {

  /**
   * Default group that every user belongs to.
   */
  private static final String DEFAULT_GROUP_ID = "everyone";

  /**
   * Event vendors group id.
   */
  private static final String EVENT_VENDORS_GROUP_ID = "event-vendors";

  /**
   * Package editor group id.
   */
  private static final String PACKAGE_VENDORS_GROUP_ID = "package-vendors";

  /**
   * List of groups that every event editor user should have.
   */
  private static final List<String> EVENT_NON_VENDOR_GROUPS =
      Arrays.asList(DEFAULT_GROUP_ID, EVENT_VENDORS_GROUP_ID);

  /**
   * List of groups that every package editor user should have.
   */
  private static final List<String> PACKAGE_NON_VENDOR_GROUPS =
      Arrays.asList(DEFAULT_GROUP_ID, PACKAGE_VENDORS_GROUP_ID);

  /**
   * Get group id with no exceptions.
   * {@link #DEFAULT_GROUP_ID} is returned in case of exception.
   */
  private static final Function<Group, String> TO_GROUP_ID = group -> {
    try {
      return group.getID();
    } catch (RepositoryException e) {
      LOGGER.error("Unable to retrieve group id from {} : ", group.toString(), e);
      return DEFAULT_GROUP_ID;
    }
  };

  /**
   * Constructor.
   */
  private VendorUtils() {
  }

  /**
   * Retrieve vendor name out of user's authorized groups.
   *
   * @param principal        principal
   * @param resourceResolver resource resolver
   * @param nonVendorGroup list of nonVendorGroup
   * @return vendor name
   */
  private static String getVendorName(@NonNull final Principal principal,
      @NonNull final ResourceResolver resourceResolver,
      @NonNull final List<String> nonVendorGroup) {
    try {
      UserManager userManager = resourceResolver.adaptTo(UserManager.class);
      Authorizable currentUser = userManager.getAuthorizable(principal);

      LOGGER.error("currentUser: {}", currentUser);
      if (Objects.isNull(currentUser)) {
        return "testing-pages";
      }
      return CommonUtils.iteratorToStream(currentUser.declaredMemberOf()).map(TO_GROUP_ID)
          .filter(id -> !nonVendorGroup.contains(id)).findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Unauthorized"));
    } catch (RepositoryException e) {
      LOGGER.error("Unable to retrieve vendor group for user: {}", principal.getName(), e);
      throw new IllegalArgumentException("Unauthorized");
    }
  }

  /**
   * Retrieve vendor name out of user's authorized groups. For Events.
   *
   * @param principal        principal
   * @param resourceResolver resource resolver
   * @return vendor name
   */
  public static String getVendorNameForEvents(@NonNull final Principal principal,
      @NonNull final ResourceResolver resourceResolver) {
    return getVendorName(principal, resourceResolver, EVENT_NON_VENDOR_GROUPS);
  }

  /**
   * Retrieve vendor name out of user's authorized groups. For Packages.
   *
   * @param principal        principal
   * @param resourceResolver resource resolver
   * @return vendor name
   */
  public static String getVendorNameForPackages(@NonNull final Principal principal,
      @NonNull final ResourceResolver resourceResolver) {
    return getVendorName(principal, resourceResolver, PACKAGE_NON_VENDOR_GROUPS);
  }
}
