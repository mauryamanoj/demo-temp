package com.saudi.tourism.core.services.impl;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.saudi.tourism.core.models.app.entertainer.AppGame;
import com.saudi.tourism.core.services.GameService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.saudi.tourism.core.utils.Constants.ROOT_APP_CONTENT_PATH;
import static com.saudi.tourism.core.utils.Constants.RT_APP_GAME_PAGE;

/**
 * Service for getting games.
 */
@Slf4j
@Component(service = GameService.class, immediate = true)
public class GameServiceImpl implements GameService {
  @Override
  public List<AppGame> getAllGames(ResourceResolver resolver, String locale, String filterStatus) {

    String path = String.format(ROOT_APP_CONTENT_PATH + "/%s/games", locale);
    PageManager pageManager = resolver.adaptTo(PageManager.class);
    Page rootGames = pageManager.getPage(path);
    if (Objects.isNull(rootGames)) {
      throw new IllegalArgumentException("The games path is not accessible");
    }
    List<AppGame> list = new ArrayList<>();
    for (@NotNull Iterator<Page> it = rootGames.listChildren(); it.hasNext();) {
      Page page = it.next();
      if (page.getContentResource().isResourceType(RT_APP_GAME_PAGE)) {
        AppGame appGame = page.getContentResource().adaptTo(AppGame.class);
        if (Objects.nonNull(appGame)) {
          list.add(appGame);
        }
      }
    }

    if (StringUtils.isBlank(filterStatus)) {
      return list;
    } else {
      return list.stream().filter(appGame -> filterStatus.contains(appGame.getStatus())).collect(Collectors.toList());
    }
  }
}
