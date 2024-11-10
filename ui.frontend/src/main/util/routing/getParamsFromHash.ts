import { parse } from 'qs';

// eslint-disable-next-line @typescript-eslint/ban-types
export const getParamsFromHash = <T extends {}>(): T => {
  const hash = window.location.hash?.substring(1);

  return parse(hash) as T;
};
