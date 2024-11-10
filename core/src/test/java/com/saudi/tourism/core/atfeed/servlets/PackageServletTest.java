package com.saudi.tourism.core.atfeed.servlets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Source;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
public class PackageServletTest {

	private PackageServlet packageServlet;

	private static final String CONTENT_PATH = "/content/sauditourism/en";
	

	@Inject
	@Source("sling-object")
	private ResourceResolver resourceResolver;

	@BeforeEach
	public void setUp(AemContext context) throws IOException {
		packageServlet = new PackageServlet();
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

		packageServlet.doGet(context.request(), context.response());
		Assertions.assertEquals(200, context.response().getStatus());
	}
}
