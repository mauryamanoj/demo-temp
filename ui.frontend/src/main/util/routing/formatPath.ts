import { isRelativePath } from './isRelativePath';
import { relativePathToFullPath } from './relativePathToFullPath';

export const formatPath = (path: string): string => {
  return isRelativePath(path) ? relativePathToFullPath(path) : path;
};
