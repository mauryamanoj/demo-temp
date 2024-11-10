// a lot of urls come from the backend without the origin, so we process them
export const relativePathToFullPath = (relativePath: string) => {
  const origin =
    process.env.NODE_ENV === 'development' ? 'https://www.visitsaudi.com' : window.location.origin;
  return `${origin}${relativePath}`;
};
