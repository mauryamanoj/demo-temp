import React, { useState, useEffect } from "react";
import { getLanguage } from "src/main/util/getLanguage";
import { useResize } from "src/main/util/hooks/useResize";
import Icon from "../../common/atoms/Icon/Icon";
import Text from "../../common/atoms/Text/Text";
import { FaqAccordion } from "./Components/FaqAccordion";
import { FaqProps, QnTypes, TabItems } from "./IFaq";

const FaqComp: React.FC<FaqProps> = ({ headline, link, faqCategoriesEndpoint, faqArticlesEndpoint }) => {
  const { isMobile } = useResize();
  const [activeIndex, setActiveIndex] = useState(0);
  const [tabItems, setTabItems] = useState<Array<TabItems>>();
  const [qnObj, setQnObj] = useState<Array<QnTypes>>();
  const [categoryCode, setCategoryCode] = useState<number>();
  const [reActive, reSetActive] = useState<boolean>(false);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const lang = getLanguage();



  const getFaqData = () => {
    const apiUrl = `${faqCategoriesEndpoint}?language=${lang}`;
    setIsLoading(true);
    fetch(apiUrl).then((response) => response.json()).then((response) => {
      setIsLoading(false);
      if (
        response && response.response &&
        response.response.categoryDataObj &&
        response.response.categoryDataObj.length > 0
      ) {
        setTabItems(response.response.categoryDataObj);
        getQn(response.response.categoryDataObj[0].Value);
      } else {
        const el = document.querySelector("[data-component=help-faq-v1]")?.parentElement;
        if (el) {
          const id = el.getAttribute("id");
          document.querySelector(`[data-target=${id}]`)?.remove();
          el.remove();
        }
      }
    }).catch((error) => {
      setIsLoading(false);
      console.error("Failed to get FAQ data", error);
      const el = document.querySelector("[data-component=help-faq-v1]")?.parentElement;
      if (el) {
        const id = el.getAttribute("id");
        document.querySelector(`[data-target=${id}]`)?.remove();
        el.remove();
      }
    });
  };
  const getQn = (value: number) => {
    setCategoryCode(value);
    const apiUrl = `${faqArticlesEndpoint}?CategoryCode=${value}&language=${
      lang === 'ar' ? 'ar' : lang === 'zh' ? 'zh' : 'en'}`;
    setIsLoading(true);
    fetch(apiUrl,
      {
        method: "GET",
        headers: {
          "Access-Control-Allow-Origin": "*",
          "Content-Type": "application/json"
        },
      }).then((response) => response.json()).then((response) => {
        setIsLoading(false);
        if (
          response && response.response &&
          response.response.kaDataObj &&
          response.response.kaDataObj.length > 0
        ) {
          setQnObj(response.response.kaDataObj);
          reSetActive(true);
        }
      }).catch((error) => {
        setIsLoading(false);
        console.error("Failed to get quetions data", error);
      });
  };

  const tabAction = (index: number, value: number) => {
    reSetActive(false);
    setActiveIndex(index);
    getQn(value);
  };

  useEffect(() => {
    getFaqData();
  }, []);

  return (
    <div className="relative">

      <div className={headline || (link && link.url && link.copy)
        ? "lg:flex justify-between items-center lg:mb-[40px] mb-0"
        : undefined
      }>
        {headline ? (
          <Text
            text={headline}
            type="h1"
            styles="text-3xl md:text-5xl
        font-primary-bold"
          />
        ) : null}
        {link && link.url && link.copy ? (
          <a
            href={link.url}
            title={link.title}
            target={link?.targetInNewWindow ? "_blank" : "_self"}
            className="text-theme-100 font-primary-bold mb-[24px] lg:mb-0 inline-block"
          >
            {link.copy.slice(0,30)}
          </a>
        ) : null}
      </div>
      <div className="flex flex-col md:flex-row gap-6 md:gap-9 relative min-h-[150px] md:min-h-0">
        {isLoading && <div className='absolute top-0 left-0 right-0 bottom-0 z-10
         bg-opacity-50 flex justify-center items-center'>
          <Icon name="loader-ios" svgClass={`w-5 h-5 animate-spin`} />
        </div>}

        {tabItems && tabItems.length > 0 && (
          <ul
            className={`${isMobile
              ? `flex flex-row items-center whitespace-nowrap scrollbar-none overflow-auto w-[calc(100%+20px)]`
              : "flex-col w-4/12"
              }`}
          >
            {tabItems.map((tab, key) => (
              <li
                className={`cursor-pointer py-4 flex justify-between border-gray-50
              ${isMobile ? "p-2 text-base border-b-2" : "text-1.5xl border-b-[0.25px]"}
              ${key === activeIndex
                    ? `font-primary-semibold text-theme-100${isMobile ? " border-b-2 border-theme-100" : ""}`
                    : "font-primary-regular md:font-primary-semibold"
                  }`}
                key={`${key}`}
                onClick={() => tabAction(key, tab.Value)}
              >
                <div>{lang === "ar" ? tab.ArabicName : lang === "zh" ? tab.ChineseName : tab.Name}</div>
                {!isMobile ? (
                  <div>
                    <Icon
                      name="small-arrow-right"
                      svgClass={`${key === activeIndex ? "child:fill-theme-100" : "child:fill-black"}
                      w-7 h-7 rtl:scale-x-[-1]`}
                    />
                  </div>
                ) : (
                  <></>
                )}
              </li>
            ))}
          </ul>
        )}
        <div className="w-full md:w-8/12">
          {qnObj &&
            qnObj.length > 0 &&
            categoryCode &&
            reActive &&
            qnObj.map((item, key) => (
              <FaqAccordion
                key={key}
                qna={item}
                categoryCode={categoryCode}
                faqArticlesEndpoint={faqArticlesEndpoint}
                lastIndex={key === qnObj.length - 1 ? key : undefined}
              />
            ))}
        </div>
      </div>
    </div>
  );
};
export default React.memo(FaqComp);
