export const isScriptPresent = (url: string): boolean => {
    if (!url) {
      // eslint-disable-next-line no-restricted-properties
      const scripts = document.querySelectorAll('script');
      for (let i = scripts.length; i--; ) {
        if (scripts[i].src == url) {return true;}
      }
    }
    return false;
  };
  
