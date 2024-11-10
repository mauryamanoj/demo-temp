import { pick } from 'lodash';
import { getParamsFromHash } from './getParamsFromHash';

export const pickParamsFromHash = <Key extends string>(keys: Array<Key>) => {
  return pick(getParamsFromHash(), keys) as Record<Key, string | undefined>;
};
