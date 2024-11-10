package com.saudi.tourism.core.services.workflow;

import com.saudi.tourism.core.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.oak.api.PropertyState;
import org.apache.jackrabbit.oak.api.Tree;
import org.apache.jackrabbit.oak.api.Type;
import org.apache.jackrabbit.oak.spi.security.authorization.restriction.RestrictionPattern;

import java.util.Iterator;

/**
 * Restriction Pattern for new restriction - commentAuthor.
 */
@Slf4j
public class WorkItemOwnerPattern implements RestrictionPattern {

  /**
   * userGroupName - commentAuthor value.
   */
  private String userGroupName;

  /**
   * userManager.
   */
  private UserManager userManager;

  /**
   * userService.
   */
  private UserService userService;

  /**
   * constructor.
   *
   * @param group       name
   * @param userService object
   */
  public WorkItemOwnerPattern(String group, UserService userService) {
    userGroupName = group;
    this.userService = userService;
  }

  @Override public boolean matches(final Tree tree, final PropertyState propertyState) {
    if (tree.getName().equals("metaData") && propertyState != null
        && propertyState.getName().equals("comment")) {

      Tree workItem = tree.getParent();
      Tree step = workItem.getParent();

      String assignee = getTreeProperty(workItem, "assignee");
      String path = step.getPath();

      if (assignee != null && inGroup(assignee)) {
        return true;
      } else {
        // if next step assigned to userName
        Tree history = step.getParent();
        Tree nextWorkItem = getNextStep(history, path);
        if (nextWorkItem != null) {
          String nextAssignee = getTreeProperty(nextWorkItem, "assignee");
          if (nextAssignee != null && inGroup(nextAssignee)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Whether user(userName) in the group(userGroupName).
   *
   * @param userName user
   * @return boolean
   */
  private boolean inGroup(String userName) {
    try {
      Authorizable authorizable = getUserManager().getAuthorizable(userGroupName);
      if (authorizable.isGroup()) {
        Iterator<Authorizable> users = ((Group) authorizable).getMembers();
        while (users.hasNext()) {
          Authorizable user = users.next();
          if (!user.isGroup() && user.getPrincipal().getName().equals(userName)) {
            return true;
          }
        }
      }
    } catch (Exception e) {
      LOGGER.error("Error", e);
    }
    return false;
  }

  /**
   * get UserManager from userService.
   *
   * @return UserManager
   */
  private UserManager getUserManager() {
    if (userManager == null) {
      userManager = userService.getResourceResolver().adaptTo(UserManager.class);
    }
    return userManager;
  }

  /**
   * This method is used to get String value of the property.
   *
   * @param tree node
   * @param name property name
   * @return String value of the property or null
   */
  private String getTreeProperty(Tree tree, String name) {
    PropertyState assignee = tree.getProperty(name);
    if (assignee != null) {
      return assignee.getValue(Type.STRING);
    }
    return null;
  }

  /**
   * This method is used to get node of the next step of the workflow history.
   *
   * @param history node
   * @param path    current step path
   * @return Node of the next step or null
   */
  private Tree getNextStep(Tree history, String path) {
    try {
      for (Tree step : history.getChildren()) {
        Tree workItem = step.getChild("workItem");
        String historyEntryPath =
            getTreeProperty(workItem.getChild("metaData"), "historyEntryPath");
        if (historyEntryPath != null && historyEntryPath.equals(path)) {
          return workItem;
        }
      }
    } catch (Exception e) {
      LOGGER.error("Error", e);
    }
    return null;
  }

  @Override public boolean matches(String path) {
    return false;
  }

  @Override public boolean matches() {
    return false;
  }
}
