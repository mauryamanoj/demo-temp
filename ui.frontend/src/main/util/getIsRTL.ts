import { htmlDirections } from './constants';

const getIsRTL = (): boolean => {
  return document.dir === htmlDirections.RTL;
};

export default getIsRTL;
