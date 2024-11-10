package com.saudi.tourism.core.models.components.informationsection.v1;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.saudi.tourism.core.services.RunModeService;
import com.saudi.tourism.core.utils.LinkUtils;
import org.apache.sling.api.resource.ResourceResolver;

public class FAQDeserializer  extends StdDeserializer<FAQ> {

  /**
   * Resource Resolver.
   */
  private final ResourceResolver resourceResolver;

  /**
   * RunMode Service.
   */
  private final RunModeService runModeService;

  public FAQDeserializer(Class<?> vc, ResourceResolver resourceResolver, RunModeService runModeService) {
    super(vc);
    this.resourceResolver = resourceResolver;
    this.runModeService = runModeService;
  }

  @Override
  public FAQ deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
    JsonNode node = jp.getCodec().readTree(jp);
    String question = node.get("question").asText();
    String answer = node.get("answer").asText();
    return FAQ.builder()
        .question(question)
        .answer(rewriteLinksInHtml(answer, resourceResolver, runModeService))
        .build();
  }

  public static String rewriteLinksInHtml(String html, ResourceResolver resolver, RunModeService runModeService) {
    return LinkUtils.rewriteLinksInHtml(html, resolver, runModeService.isPublishRunMode());
  }
}
