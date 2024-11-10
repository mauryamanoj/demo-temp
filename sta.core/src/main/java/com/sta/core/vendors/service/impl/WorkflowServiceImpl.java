package com.sta.core.vendors.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.jcr.RepositoryException;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.Route;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.Workflow;
import com.sta.core.vendors.service.WorkflowService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.osgi.service.component.annotations.Component;

/**
 * Service to work with granite workflow.
 */
@Component
@Slf4j
public class WorkflowServiceImpl implements WorkflowService {

  /**
   * Workflow RUNNING status.
   */
  private static final String RUNNING_STATUS = "RUNNING";

  /**
   * Id of the very first node in the workflow.
   */
  private static final String FIRST_NODE = "node1";

  /**
   * Property key for the participant value.
   */
  private static final String PARTICIPANT_KEY = "PARTICIPANT";

  /**
   * Predicate to find routes that leads to the first node in the workflow model.
   */
  private static final Predicate<? super Route> FIRST_NODE_ROUTE_PREDICATE = route ->
      route.getDestinations().stream().anyMatch(destination ->
          destination.getTo().getId().equals(FIRST_NODE));


  /**
   * Gets workflow instance by it's modelId and payload.
   * @param session workflow session
   * @param modelId workflow model id
   * @param payload workflow payload
   * @return workflow instance
   */
  public Optional<Workflow> getWorkflowByPayload(WorkflowSession session,
      String modelId, String payload) {
    try {
      Workflow[] workflows = session.getWorkflows(new String[]{RUNNING_STATUS});
      return Arrays.stream(workflows).filter(workflow ->
          workflow.getWorkflowModel().getId().equals(modelId)
          && workflow.getWorkflowData().getPayload().equals(payload)
              //checking for work items here to filter out workflows which are in RUNNING status,
              //but have no active working items. [Faced this issue in ACC server, and couldn't find
              //an answer for this in the documentation.
          && !workflow.getWorkItems().isEmpty()
      ).findFirst();
    } catch (WorkflowException e) {
      LOGGER.error("Couldn't execute search for running instances for model id = [{}] and payload"
          + " = [{}]", modelId, payload, e);
      return Optional.empty();
    }
  }

  /**
   * Move workflow back to the very first step.
   * @param session workflow session
   * @param workflow workflow instance
   * @param userManager user manager
   * @return true in case of success
   */
  public boolean moveToBeginning(WorkflowSession session, Workflow workflow,
      UserManager userManager) {
    try {
      WorkItem[] workItems = session.getActiveWorkItems();
      Optional<WorkItem> workItem = Arrays.stream(workItems)
          .filter(item -> item.getWorkflow().getId().equals(workflow.getId()))
          .findFirst();
      if (workItem.isPresent()) {
        Optional<Route> routeToBeginning = findFirstNode(workItem.get(), session, userManager);
        if (routeToBeginning.isPresent()) {
          session.complete(workItem.get(), routeToBeginning.get());
          return true;
        } else {
          LOGGER.info("Workflow {} is currently in the beginning. No action required.",
              workflow.getId());
          return false;
        }
      } else {
        LOGGER.error("Unable to find active work item for workflow id = {}.", workflow.getId());
        throw new IllegalArgumentException("Unable to find active work item for workflow id");
      }

    } catch (WorkflowException e) {
      LOGGER.error("Unable to restart the workflow for workflow id = {}.", workflow.getId(), e);
      throw new IllegalArgumentException("Unable to restart the workflow");
    }
  }

  /**
   * Find a route to the first node, preferably, should be assigned to a group, rather than a user.
   * @param workItem active work item
   * @param session workflow session
   * @param userManager user manager
   * @return route to the first node or Optional.empty
   * @throws WorkflowException workflow exception
   */
  private Optional<Route> findFirstNode(WorkItem workItem, WorkflowSession session,
      UserManager userManager)
      throws WorkflowException {
    //getBackRoutes() method returns all routes that are referencing to the previous nodes.
    //So, you don't have to explicitly define back route for each step.
    List<Route> routes = session.getBackRoutes(workItem, true);
    List<Route> routesToBeginning = routes.stream().filter(FIRST_NODE_ROUTE_PREDICATE)
        .collect(Collectors.toList());

    if (CollectionUtils.isEmpty(routesToBeginning)) {
      return Optional.empty();
    }

    Optional<Route> groupRoute = routesToBeginning.stream().filter(route ->
        route.getDestinations().stream().anyMatch(destination -> {
          String participant =
              destination.getTo().getMetaDataMap().get(PARTICIPANT_KEY, StringUtils.EMPTY);
          try {
            return Optional.ofNullable(userManager.getAuthorizable(participant))
                .map(Authorizable::isGroup)
                .orElse(false);
          } catch (RepositoryException e) {
            LOGGER.error("Unable to find an authorizable for : {}", participant, e);
            return false;
          }
        })).findAny();

    if (groupRoute.isPresent()) {
      return groupRoute;
    }

    return Optional.of(routesToBeginning.get(0));
  }

}
