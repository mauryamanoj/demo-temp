package com.saudi.tourism.core.models.app.page;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
class SearchTest {
    private static final String CONTENT_PATH = "/content/sauditourism/app/en/makkah";

    @BeforeEach
    public void setUp(AemContext context) {
            context.load().json("/pages/app-location-page"
                + ".json", CONTENT_PATH);
          }
    @Test
    public void testSearchTags(AemContext aemContext) {
        String RESOURCE_PATH = "/content/sauditourism/app/en/makkah/jcr:content/search";

        Search search = aemContext.currentResource(RESOURCE_PATH).adaptTo(Search.class);
        assertEquals(1, search.getSearchTags().length);
    }

}