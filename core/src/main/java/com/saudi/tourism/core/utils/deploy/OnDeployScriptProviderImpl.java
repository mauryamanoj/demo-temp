package com.saudi.tourism.core.utils.deploy;

import com.adobe.acs.commons.ondeploy.OnDeployScriptProvider;
import com.adobe.acs.commons.ondeploy.scripts.OnDeployScript;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.deploy.scripts.ArticlePageMigrationScript;
import com.saudi.tourism.core.utils.deploy.scripts.CardsToTeaserMigrationScript;
import com.saudi.tourism.core.utils.deploy.scripts.EssentialBannerMigrationScript;
import com.saudi.tourism.core.utils.deploy.scripts.EventsAllSeasonsOnDeployScript;
import com.saudi.tourism.core.utils.deploy.scripts.FeatureImageMigrationScript;
import com.saudi.tourism.core.utils.deploy.scripts.MigrateCityCodes;
import com.saudi.tourism.core.utils.deploy.scripts.NoCacheChangeScript;
import com.saudi.tourism.core.utils.deploy.scripts.TeaserDefaultOrnamentsMigrationScript;
import org.osgi.service.component.annotations.Component;

import java.util.Arrays;
import java.util.List;

/**
 * This service provides a list of migration scripts that are executed only once upon package
 * deployment.
 *
 * @see <a href=
 * "//adobe-consulting-services.github.io/acs-aem-commons/features/on-deploy-scripts/index.html">
 * On-Deploy Scripts</a>
 */
@Component(
    service = OnDeployScriptProvider.class,
    immediate = true,
    property = {
        Constants.SERVICE_DESCRIPTION + Constants.EQUAL
            + "Developer service that identifies code scripts to execute upon deployment"
    })
public class OnDeployScriptProviderImpl implements OnDeployScriptProvider {
  @Override
  public List<OnDeployScript> getScripts() {
    return Arrays.asList(
        // List of script instances
        new MigrateCityCodes(),
        new NoCacheChangeScript(),
        new EssentialBannerMigrationScript(),
        new CardsToTeaserMigrationScript(),
        new FeatureImageMigrationScript(),
        new TeaserDefaultOrnamentsMigrationScript(),
        new EventsAllSeasonsOnDeployScript(),
        new ArticlePageMigrationScript()
    );
  }
}
