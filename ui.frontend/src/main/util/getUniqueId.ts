const getUniqueIdGenerator = () => {
  let id = 0;
  return () => {
    return id++;
  };
};

export const getUniqueId = getUniqueIdGenerator();
