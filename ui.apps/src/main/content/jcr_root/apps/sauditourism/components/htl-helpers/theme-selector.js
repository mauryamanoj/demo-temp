"use strict";
use(function () {
  var currentTheme = this.pageTheme;
  var theme = this.theme || currentTheme;
  if (theme && theme.startsWith("theme-")) {
    theme = theme.split("theme-")[1];
  }
  return {
    theme: theme
  };
});