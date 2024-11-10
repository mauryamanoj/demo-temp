export const fullPathToRelativePath = (fullPath: string) => {
  return fullPath.replace(window.location.origin, '');
};
