interface LoadScriptProps {
    source: string;
    async?: boolean;
  }
  export const loadScript = ({ source, async = false }: LoadScriptProps): Promise<Event> => {
    return new Promise((resolve) => {
      const body = document.body;
      const script = document.createElement('script');
      script.onload = resolve;
      script.src = source;
      script.async = async;
      body.appendChild(script);
    });
  };
  
