export function getLanguage(): string {
  const lang = window.document.documentElement.lang;
  return lang ? lang : 'en';
}

export function getGoogleRegion(): string {
  const lang = window.document.documentElement.lang;
  let region = '';
  switch (lang) {
    case 'en':
      region = 'US';
      break;
    case 'ar':
      region = 'SA';
      break;
    case 'de':
      region = 'DE';
      break;
    case 'es':
      region = 'ES';
      break;
    case 'FR':
      region = 'FR';
      break;
    case 'ja':
      region = 'JP';
      break;
    case 'ru':
      region = 'RU';
      break;
    default:
      region = '';
      break;
  }
  return region;
}

export function i18n(label: string): string {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const i18nLabels = (window as any).i18nLabels;
  if (i18nLabels && i18nLabels[label] !== undefined) {return i18nLabels[label];}
  return label;
}
/**
 * get the last segment of path or url
 */
export const getLastSegment = (): string => {
  const slug = window.location.pathname?.substring(window.location.pathname.lastIndexOf('/') + 1);
  return slug;
  // const pageURL = window.location.href;
  // const lastURLSegment = pageURL.substr(pageURL.lastIndexOf('/') + 1);
  // return lastURLSegment;
};

export const getLastSegmentExcludeParam = (): string => {
  const pageURL = window.location.href;
  const lastURLSegment = pageURL.substr(pageURL.lastIndexOf('/') + 1).split('?')[0];
  return lastURLSegment;
};

export const getQueryParamsFromUrl = () => {
  try {
    const urlSearchParams = new URLSearchParams(window.location.search);
    const params = Object.fromEntries(urlSearchParams.entries());
    return params;
  } catch (error) {}
};

export const removeURLParameter = (url: string, parameter: string) => {
  //prefer to use l.search if you have a location/link object
  const urlparts = url.split('?');
  if (urlparts.length >= 2) {
    const prefix = encodeURIComponent(parameter) + '=';
    const pars = urlparts[1].split(/[&;]/g);

    //reverse iteration as may be destructive
    for (let i = pars.length; i-- > 0; ) {
      //idiom for string.startsWith
      if (pars[i].lastIndexOf(prefix, 0) !== -1) {
        pars.splice(i, 1);
      }
    }

    url = urlparts[0] + '?' + pars.join('&');
    return url;
  } else {
    return url;
  }
};
