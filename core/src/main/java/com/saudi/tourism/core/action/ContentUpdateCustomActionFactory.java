package com.saudi.tourism.core.action;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveActionFactory;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.day.cq.wcm.msm.api.RolloutManager;
import com.day.cq.wcm.msm.commons.FilteredAction;
import com.day.cq.wcm.msm.commons.FilteredActionFactoryBase;
import com.day.cq.wcm.msm.commons.ItemFilterImpl;
import com.day.cq.wcm.msm.commons.ItemFilterUtil;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;

import static com.day.cq.commons.jcr.JcrConstants.JCR_LASTMODIFIED;

/**
 * ContentUpdateActionFactory.
 */
@Slf4j
@Component(immediate = true,
           service = LiveActionFactory.class,
           property = {LiveActionFactory.LIVE_ACTION_NAME + Constants.EQUAL
               + ContentUpdateCustomActionFactory.LIVE_ACTION_CLASS_NAME,
               LiveActionFactory.LIVE_ACTION_NAME + Constants.EQUAL
                   + ContentUpdateCustomActionFactory.LIVE_ACTION_NAME})
@Designate(ocd = ContentUpdateCustomActionFactory.Configuration.class)
public class ContentUpdateCustomActionFactory extends FilteredActionFactoryBase<FilteredAction> {

  /**
   * LiveAction Class name.
   */
  public static final String LIVE_ACTION_CLASS_NAME = "ContentUpdateAction";
  /**
   * LiveActionName.
   */
  public static final String LIVE_ACTION_NAME = "contentUpdate";

  /**
   * The constant relManager.
   */
  @Reference
  private LiveRelationshipManager relManager;

  /**
   * RolloutManager.
   */
  @Getter
  @Reference
  private RolloutManager rolloutManager;


  /**
   * ExcludedParagraphItems.
   */
  @Getter
  private ItemFilterImpl excludedParaItems;

  /**
   * LiveRelationship manager.
   */
  @Reference
  private LiveRelationshipManager relationshipManager;

  /**
   * Configure factory.
   *
   * @param context       componentContext.
   * @param configuration configuration class
   */
  @Activate
  @Modified protected void configure(ComponentContext context,
      Configuration configuration) {
    LOGGER.debug("Begin: Activated/Modified Factory");
    setupFilter(context, this.rolloutManager);
    this.excludedParaItems = ItemFilterUtil
        .createFilter(null, null, PropertiesUtil.toStringArray(configuration.paragraphItems()),
            rolloutManager);
    LOGGER.debug("End: Activated/Modified Factory");
  }

  /**
   * ContentUpdateCustomActionFactory.
   */
  public ContentUpdateCustomActionFactory() {
    this.rolloutManager = null;
  }

  @Override protected ContentUpdateAction newActionInstance(final ValueMap valueMap) {
    return new ContentUpdateAction(valueMap, this);
  }

  @Override public String createsAction() {
    return LIVE_ACTION_NAME;
  }

  /**
   * ContentUpdateAction.
   */
  private final class ContentUpdateAction extends FilteredAction {

    /**
     * Paragraph Items.
     */
    private ItemFilterImpl paragraphItems;

    /**
     * ContentUpdateAction Constructor.
     *
     * @param config  properties
     * @param factory factory
     */
    ContentUpdateAction(ValueMap config, ContentUpdateCustomActionFactory factory) {
      super(config, factory.getPagePropertyFilter(), factory.getExcludedParaItems(), factory);
      this.paragraphItems = factory.getExcludedParaItems();
    }

    /**
     * DoHandle.
     *
     * @param source       source
     * @param target       target
     * @param relation     relation
     * @param resetRollout resetRollout
     * @return if action can be handled
     */
    protected boolean doHandle(Resource source, Resource target, LiveRelationship relation,
        boolean resetRollout) {
      return (relation.getStatus().isSourceExisting() && resourceHasNode(source) && resourceHasNode(
          target));
    }

    /**
     * Execute rollout.
     *
     * @param source       source
     * @param target       target
     * @param relation     relation
     * @param resetRollout resetRollout
     * @throws RepositoryException repoEx
     * @throws WCMException        WCMException
     */
    protected void doExecute(Resource source, Resource target, LiveRelationship relation,
        boolean resetRollout) throws RepositoryException, WCMException {
      updateCustomProps(source, target, relation);

      if (source.getPath().endsWith("jcr:content/root/responsivegrid")) {
        handleBrokenComponentRollouts(source, target);

      }
    }

    /**
     * Handle broken component rollouts.
     *
     * @param source the source
     * @param target the target
     */
    private void handleBrokenComponentRollouts(final Resource source, final Resource target) {
      Iterator<Resource> resIter = source.getChildren().iterator();
      while (resIter.hasNext()) {

        try {
          Resource childSource = resIter.next();
          Resource childTarget = target.getChild(childSource.getName());

          if (childTarget != null && !childTarget.isResourceType("wcm/msm/components/ghost")) {
            updateCustomProps(childSource, childTarget,
                relManager.getLiveRelationship(childTarget, false));
            handleBrokenComponentRollouts(childSource, childTarget);
          }
        } catch (Exception e) {
          LOGGER.error("error in updating property {}", e.getMessage());
        }

      }
    }

    /**
     * Update custom props.
     *
     * @param source   the source
     * @param target   the target
     * @param relation the relation
     * @throws RepositoryException the repository exception
     * @throws WCMException        the wcm exception
     */
    private void updateCustomProps(final Resource source, final Resource target,
        final LiveRelationship relation) throws RepositoryException, WCMException {
      boolean updated = false;
      Node masterNode = source.adaptTo(Node.class);
      Node slaveNode = target.adaptTo(Node.class);

      if (Objects.isNull(masterNode) || Objects.isNull(slaveNode)) {
        return;
      }

      HashSet<String> inspectedProperties = new HashSet<>();
      Iterator<Property> mpi = getFilteredProperties(masterNode);
      while (mpi.hasNext()) {
        Property property = mpi.next();
        inspectedProperties.add(property.getName());

        if (!relation.getStatus().getCanceledProperties().contains(property.getName())) {
          if ((!relation.getStatus().isPage()) && (property.getName().equals(JCR_LASTMODIFIED))) {
            JcrUtil.setProperty(slaveNode, JCR_LASTMODIFIED, Calendar.getInstance());
          } else {
            if (!property.isMultiple() && !property.getValue().getString()
                .startsWith(Constants.ROOT_CONTENT_PATH)) {
              if (property.getValue().getString().startsWith(Constants.ROOT_EXP_FRAGS_PATH)) {
                // Custom logic for Experience fragments rollout
                String slaveLang = CommonUtils.getLanguageForPath(slaveNode.getPath());
                String masterLang = CommonUtils.getLanguageForPath(property.getValue().getString());

                slaveNode.setProperty(property.getName(), property.getValue().getString().replace(
                    Constants.FORWARD_SLASH_CHARACTER + masterLang
                        + Constants.FORWARD_SLASH_CHARACTER,
                    Constants.FORWARD_SLASH_CHARACTER + slaveLang
                        + Constants.FORWARD_SLASH_CHARACTER));
              } else {
                JcrUtil.copy(property, slaveNode, null);
              }

            }
          }
          updated = true;
        }
      }

      Iterator<Property> pItr = getFilteredProperties(slaveNode);
      while (pItr.hasNext()) {
        Property prop = pItr.next();
        if ((!inspectedProperties.remove(prop.getName())) && (!prop.getDefinition().isAutoCreated())
            && (!prop.getDefinition().isProtected()) && (!relation.getStatus()
            .getCanceledProperties().contains(prop.getName()))) {
          prop.remove();
          updated = true;
        }
      }

      if (updated) {
        PageManager pm = source.getResourceResolver().adaptTo(PageManager.class);
        Page page = Objects.requireNonNull(pm).getContainingPage(slaveNode.getPath());
        if (page != null) {
          pm.touch(page.adaptTo(Node.class), true, Calendar.getInstance(), false);
        }
      }
    }
  }


  /**
   * Configuration class.
   */
  @ObjectClassDefinition(name = "Custom ContentUpdateAction Configuration")
  @interface Configuration {
    /**
     * Excluded paragraph items.
     *
     * @return excludedParagraphItems string [ ]
     */
    @AttributeDefinition(name = "Exclude Paragraph Items",
                         type = AttributeType.STRING)
    String[] paragraphItems() default StringUtils.EMPTY;
  }
}
