package com.sta.core.vendors.service;

import java.util.Optional;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.Workflow;

import org.apache.jackrabbit.api.security.user.UserManager;

/**
 * Service to work with granite workflow.
 */
public interface WorkflowService {

  /**
   * Gets workflow instance by it's modelId and payload.
   * @param session workflow session
   * @param modelId workflow model id
   * @param payload workflow payload
   * @return workflow instance
   */
  Optional<Workflow> getWorkflowByPayload(WorkflowSession session,
      String modelId, String payload);

  /**
   * Move workflow back to the very first step.
   * @param session workflow session
   * @param workflow workflow instance
   * @param userManager user manager
   * @return true in case of success
   */
  boolean moveToBeginning(WorkflowSession session, Workflow workflow, UserManager userManager);
}
