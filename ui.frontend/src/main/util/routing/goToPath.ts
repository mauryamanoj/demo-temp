import { getPath } from './getPath';

// TODO - Handle multiple hash params
export const goToPath = (url: string, hash?: { key: string; value: string }) => {
  window.location.href = getPath(url, hash);
};
