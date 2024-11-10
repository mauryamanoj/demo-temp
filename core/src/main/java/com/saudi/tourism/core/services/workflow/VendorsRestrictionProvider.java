package com.saudi.tourism.core.services.workflow;

import com.saudi.tourism.core.services.UserService;
import org.apache.jackrabbit.oak.api.Tree;
import org.apache.jackrabbit.oak.api.Type;
import org.apache.jackrabbit.oak.spi.security.authorization.restriction.AbstractRestrictionProvider;
import org.apache.jackrabbit.oak.spi.security.authorization.restriction.Restriction;
import org.apache.jackrabbit.oak.spi.security.authorization.restriction.RestrictionDefinition;
import org.apache.jackrabbit.oak.spi.security.authorization.restriction.RestrictionDefinitionImpl;
import org.apache.jackrabbit.oak.spi.security.authorization.restriction.RestrictionPattern;
import org.apache.jackrabbit.oak.spi.security.authorization.restriction.RestrictionProvider;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Restriction Provider for new restriction - commentAuthor.
 */
@Component(service = RestrictionProvider.class,
           immediate = true)
public class VendorsRestrictionProvider extends AbstractRestrictionProvider {

  /**
   * The User service.
   */
  @Reference
  private UserService userService;

  /**
   * Contructor.
   */
  public VendorsRestrictionProvider() {
    super(supportedRestrictions());
  }

  /**
   * This method is used to register new RestrictionDefinition.
   *
   * @return map with new restrictions
   */
  private static Map<String, RestrictionDefinition> supportedRestrictions() {
    RestrictionDefinition commentAuthor =
        new RestrictionDefinitionImpl("commentAuthor", Type.STRING, false);
    return Collections.singletonMap(commentAuthor.getName(), commentAuthor);
  }

  @Override public RestrictionPattern getPattern(String oakPath, Tree tree) {
    return RestrictionPattern.EMPTY;
  }

  @Override
  public RestrictionPattern getPattern(@Nullable String oakPath, Set<Restriction> restrictions) {
    if (oakPath != null) {
      for (Restriction r : restrictions) {
        String name = r.getDefinition().getName();
        if ("commentAuthor".equals(name)) {
          return new WorkItemOwnerPattern(r.getProperty().getValue(Type.STRING),
              userService);
        }
      }
    }
    return RestrictionPattern.EMPTY;
  }
}
