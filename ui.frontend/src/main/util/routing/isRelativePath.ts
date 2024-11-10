export const isRelativePath = (path: string): boolean => {
  const absolutePattern = /^(https?:)?\/\//;

  return !absolutePattern.test(path);
};
