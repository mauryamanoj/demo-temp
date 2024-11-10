import { relativePathToFullPath } from './relativePathToFullPath';

// TODO - Handle multiple hash params
export const getPath = (url: string, hash?: { key: string; value: string }) => {
  const isDevelopEnviroment = process.env.NODE_ENV === 'development';
  const baseUrl = isDevelopEnviroment ? url : relativePathToFullPath(url);
  const fullUrl = hash ? `${baseUrl}#${hash.key}=${hash.value}` : baseUrl;
  return fullUrl;
};
