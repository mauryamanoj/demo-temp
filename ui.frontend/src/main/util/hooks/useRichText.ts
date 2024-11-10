import { useEffect, useRef } from 'react';

export const useRichText = <E extends HTMLElement>(content: string) => {
  const richTextRef = useRef<E | null>(null);

  const replaceInString = (
    value: string,
    tagToReplace: keyof HTMLElementTagNameMap,
    newTag: keyof HTMLElementTagNameMap,
  ) => {
    const replacedOpenTags = value.split(`<${tagToReplace}>`).join(`<${newTag}>`);
    const replacedCloseTags = replacedOpenTags.split(`</${tagToReplace}>`).join(`</${newTag}>`);

    return replacedCloseTags;
  };

  useEffect(() => {
    if (!richTextRef.current) {return;}

    // Replace <p> tags with <span> to support multi-line/multi-paragraph clamp/ellipsis on Safari
    richTextRef.current.innerHTML = replaceInString(content, 'p', 'span');
  }, [richTextRef.current, content]);

  return { richTextRef };
};
