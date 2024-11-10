export const getArabicPlural = (word: string, count: number): string => {
  let translation = '';
  if (word == 'event') {
    translation = count == 2 ? "فعاليتان" : (count == 1 || count && count > 10) ? "فعالية" : "فعاليات";
  }else if(word=='hotel'){
    translation = count == 2 ? "فندقين" : (count == 1 || count && count > 10) ? "فندق" : "فنادق";
  }

    return translation;
  };

