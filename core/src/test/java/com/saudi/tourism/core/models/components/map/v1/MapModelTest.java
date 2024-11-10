package com.saudi.tourism.core.models.components.map.v1;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
public class MapModelTest {
    private static final String CONTENT_PATH = "/content/sauditourism/en/map";


    @BeforeEach
    public void setUp(AemContext context) {
        context.load().json("/components/c-14-map/content.json", CONTENT_PATH);

    }

    @Test
    public void testMapModel(AemContext context) {
        String RESOURCE_PATH = "/content/sauditourism/en/map/jcr:content/root/responsivegrid/c14_map";

        MapModel model = context.currentResource( RESOURCE_PATH).adaptTo(MapModel.class);

        assertEquals("Map Title", model.getTitle());
        assertEquals(-34.586295, model.getLatitude());
        assertEquals(-58.43171212, model.getLongitude());
      assertEquals("google", model.getMapType());

        assertEquals(2, model.getLinks().size());
        assertEquals("Google", model.getLinks().get(0).getCopy());
        assertEquals("https://www.google.com", model.getLinks().get(0).getUrl());
        assertEquals(false, model.getLinks().get(0).isTargetInNewWindow());



    }



}
