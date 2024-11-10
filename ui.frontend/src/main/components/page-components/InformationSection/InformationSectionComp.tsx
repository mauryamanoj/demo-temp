import React, { useEffect, useState, useRef }  from "react";
import Collapsible from "../../common/atoms/Collapsible/Collapsible";
import Text from "../../common/atoms/Text/Text";

const InformationSectionComp = ({ title, data, hideReadMore, readMoreLabel, readLessLabel,link }: any) => {
  const length = data.length;
  const viewMore = !hideReadMore && length > 4;
  const [showMore, setShowMore] = useState(hideReadMore);

  const [isLayout2ColsExist, setIsLayout2ColsExist] = useState(false);
  const informationSectionRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
      const checkLayout2Cols = () => {
          const currentElement = informationSectionRef.current;
          if (currentElement && currentElement.closest('.layout2Cols')) {
              setIsLayout2ColsExist(true);
          }
      };

      checkLayout2Cols();
  }, [informationSectionRef.current]);
  return (
    <section ref={informationSectionRef}  className={`${!isLayout2ColsExist ? "px-5 lg:px-100" : ""}`}>
    <div  className={`lg:flex justify-between items-center mb-6`}>
      <Text isTitle text={title} styles="leading-[37px] lg:leading-[60px] !mb-6 lg:!mb-0"/>
    {link && link.url && link.text ? (
          <a
            href={link.url}
            title={link.title}
            target={link?.targetInNewWindow ? "_blank" : "_self"}
            className="text-theme-100 hover:text-theme-200 font-primary-bold inline-block"
          >
            {link.text.slice(0,30)}
          </a>
        ) : null}
    </div>
    <div>
      {data && length > 0
        ? data.slice(0, showMore ? length : 4).map((quesAns: any, index: any) => (
            <div key={index} className={` ${index===0 ? "border-t-[0.5px] border-t-border-gray-25 md:border-t-0": ""} border-b-[0.25px] border-gray/50`}>
              <Collapsible
                title={quesAns.question.replace(/[\u200B-\u200D\uFEFF\u202F]/g, ' ')}
                className="font-primary-semibold text-lg md:text-1.5xl py-4 normal-case"
                collapseSectionHeight="max-h-fit"
                collapsed={true}
              >
                <div
                  key={index}
                  className="text-base font-primary-regular mb-4 richTextContainer"
                  style={{ wordWrap: 'break-word' }}
                  dangerouslySetInnerHTML={{ __html: quesAns.answer.replace(/[\u200B-\u200D\uFEFF\u202F]/g, ' ') }}
                ></div>
              </Collapsible>
            </div>
          ))
        : null}
      {viewMore ? (
        <div
          className="text-theme-100 py-4 cursor-pointer font-primary-bold text-sm w-fit"
          onClick={() => setShowMore(!showMore)}
        >
          {showMore ? readLessLabel : readMoreLabel}
        </div>
      ) : (
        <></>
      )}
    </div>
  </ section>
  );
};

export default InformationSectionComp;
