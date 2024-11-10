export const getAllLinks = (): Array<HTMLAnchorElement> => {
  // eslint-disable-next-line no-restricted-properties
  const links = document.querySelectorAll('a');

  // TODO: when you filter it's no longer "all" links, so the name is wrong
  return Array.from(links).filter(
    (link) =>
      link.getAttribute('data-location') !== 'topnav' &&
      link.getAttribute('data-location') !== 'breadcrumb',
  );
};
