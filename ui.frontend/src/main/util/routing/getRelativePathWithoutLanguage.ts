import { fullPathToRelativePath } from './fullPathToRelativePath';

export const getRelativePathWithoutLanguage = (fullPath: string) => {
  return fullPathToRelativePath(fullPath)?.substring(4);
};
