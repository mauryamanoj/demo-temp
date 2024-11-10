import React, { useState } from "react";
import Collapsible from "src/main/components/common/atoms/Collapsible/Collapsible";
import { getLanguage } from "src/main/util/getLanguage";
import { QnTypes } from "../IFaq";

export interface FaqTypes {
  qna: QnTypes;
  categoryCode: number;
  faqArticlesEndpoint: string;
  lastIndex?: number;
}

export const FaqAccordion: React.FC<FaqTypes> = ({ qna, categoryCode, faqArticlesEndpoint, lastIndex }) => {
  const [active, setActive] = useState(false);
  const [ans, setAns] = useState<string>();
  const lang = getLanguage();
  const getAnswer = (value: number) => {
    const apiUrl = `${faqArticlesEndpoint}?CategoryCode=${value}&language=${lang === 'ar' ? 'ar' : lang === 'zh' ? 'zh' : 'en'}`;
    fetch(apiUrl,
      {
        method: "GET",
        headers: {
          "Access-Control-Allow-Origin": "*",
          "Content-Type": "application/json"
        },
      }).then((response) => response.json()).then((response) => {
        if (
          response.apidataObj &&
          response.apidataObj.kaDataObj &&
          response.apidataObj.kaDataObj.length > 0
        ) {
          setAns(response.apidataObj.kaDataObj);
        }
      })
      .catch((error) => console.error("Failed to get Answer data", error));
  };
  const toggleAccordion = (value: string) => {
    setActive(!active);
    setAns(value);
  };
  return (
    <div className="border-b-[1px] border-gray/50">
      <Collapsible
        title={qna.Title.replace(/[\u200B-\u200D\uFEFF\u202F]/g, ' ')}
        onToggle={() => toggleAccordion(qna.Content)}
        className="font-primary-semibold text-lg md:text-1.5xl py-4 normal-case leading-[22px] md:leading-[27px]"
        collapseSectionHeight="max-h-fit"
        collapsed={true}
      >
        {active && ans && ans.length > 0 && (
          <div dangerouslySetInnerHTML={{ __html: ans.replace('<li></li>', '') as string }}
            className="text-sm md:text-base font-primary-regular mb-4 richTextContainer leading-[17px] md:leading-[19px]"
          />
        )}
      </Collapsible>
    </div>
  );
};
