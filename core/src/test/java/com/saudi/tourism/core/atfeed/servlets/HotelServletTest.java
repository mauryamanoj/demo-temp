package com.saudi.tourism.core.atfeed.servlets;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;

import org.apache.sling.api.adapter.Adaptable;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import com.adobe.cq.wcm.core.components.models.Page;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.PageManager;
import com.saudi.tourism.core.atfeed.servlets.HotelServlet;
import com.saudi.tourism.core.services.EventService;
import com.saudi.tourism.core.utils.Utils;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
public class HotelServletTest {

	private HotelServlet hotelServlet;

	private static final String CONTENT_PATH = "/content/sauditourism/en";
	

	@Inject
	@Source("sling-object")
	private ResourceResolver resourceResolver;

	@BeforeEach
	public void setUp(AemContext context) throws IOException {
		hotelServlet = new HotelServlet();
		Session session = mock(Session.class);
		ResourceResolver mockResourceResolver = spy(context.resourceResolver());
//		doReturn(context.resourceResolver()).when(context.request().getResourceResolver());
//		when(context.request().getResourceResolver()).thenReturn(context.resourceResolver());
		lenient().when(mockResourceResolver.adaptTo(Session.class)).thenReturn(session);
		//doReturn(session).when(context.resourceResolver().adaptTo(Session.class));
	}

	@Test
	public void test(AemContext context)
			throws IllegalArgumentException, ServletException, IOException, RepositoryException {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("locale", "en");
		parameters.put("path", CONTENT_PATH);
		
		context.load().json("/components/header-config-model/content.json", CONTENT_PATH);
		context.request().setParameterMap(parameters);

		hotelServlet.doGet(context.request(), context.response());
		Assertions.assertEquals(200, context.response().getStatus());
	}

}
