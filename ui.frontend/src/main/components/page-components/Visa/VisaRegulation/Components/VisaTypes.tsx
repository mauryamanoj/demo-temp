import React, { useMemo, useState } from "react";
import Button from "src/main/components/common/atoms/Button/Button";
import Icon from "src/main/components/common/atoms/Icon/Icon";
import Picture from "src/main/components/common/atoms/Picture/Picture";
import { trackAnalytics, trackingEvent } from 'src/main/util/updateAnalyticsData';

// Note:you see the icon locally and on QA
const basePath = process.env.NODE_ENV == "development" ? "https://qa-revamp.visitsaudi.com" : "";

const VisaTypes = ({ sections, visaTypesTitle, informationsLabel, requirementsLabel, selectedCountry }: any) => {

  const [selected, setSelected] = useState(0);
  const tabWidth = sections.length > 3 ? "w-1/4" : sections.length > 2 ? "w-1/3" : "w-1/2";

  const sectionsDesign = useMemo(() => {
    if (
      sections[selected].informations &&
      sections[selected].requirementsAndDocumentation &&
      sections[selected].card?.title
    ) {
      return "all";
    } else if (sections[selected].informations && sections[selected].requirementsAndDocumentation) {
      return "first2";
    } else if (sections[selected].informations && sections[selected].card?.title) {
      return "not2";
    } else if (sections[selected].requirementsAndDocumentation && sections[selected].card?.title) {
      return "last2";
    } else {
      return "one";
    }
  }, [selected]);

  function handleApplyClick(item: any) {
    const info = sections[selected]?.informations;
    var visaPrice = info ? info[0]?.value : "";


    trackingEvent({
      event_name: "eVisa_apply_now",
      title: item?.iconLabel,
      url: sections[selected].card.link.url,
      visaPrice: visaPrice,
      countrySelected: selectedCountry,
      sectionName: sections[selected].title,
      currency: "SAR"
    });

    trackAnalytics({
      trackEventname: "eVisa_apply_now",
      trackName: "dl_push",
      title: item?.iconLabel,
      url: sections[selected].card.link.url,
      visaPrice: visaPrice,
      countrySelected: selectedCountry,
      sectionName: sections[selected].title,
      currency: "SAR"
    });
  }

  function handleTabClick(key: any, label: string) {

    trackingEvent({
      event_name: "visa_type_interaction",
      title: label,
      url: "",
      countrySelected: selectedCountry,
      sectionName: label,
      currency: "SAR"
    });

    trackAnalytics({
      trackEventname: "visa_type_interaction",
      trackName: "dl_push",
      title: label,
      url: "",
      countrySelected: selectedCountry,
      sectionName: label,
      currency: "SAR"
    });
    setSelected(key);
  }

  return (
    <div className="flex flex-col gap-16 bg-theme-100 mt-10 py-10">
      {visaTypesTitle ? (
        <div className="text-white font-primary-bold text-5xl leading-[59px] px-5 md:px-100">{visaTypesTitle}</div>
      ) : (
        <></>
      )}
      <div className="flex border-b-[0.25px] border-b-white px-5 md:px-100 overflow-x-auto scrollbar-none">
        {sections.map((section: any, key: number) => (
          <div
            key={key}
            className={`${tabWidth} ${selected === key ? "bg-white text-theme-100" : "text-white"
              } py-[30px] px-6 rounded-t-2xl font-primary-bold text-[32px] cursor-pointer
            flex items-center gap-4 min-w-[360px] relative`}
            onClick={() => handleTabClick(key, section?.title)}
          >
            {section.icon ? (
              <Icon name={basePath + section.icon} isFetch
                // svgClass="!w-[60px] !h-[60px]"
                wrapperClass={`${selected === key ? "fill-themed" : "white-svg"}
                child:child:!w-[60px] child:child:!h-[60px]`} />
            ) : (
              <></>
            )}
            <div className="leading-10">{section?.title}</div>
          </div>
        ))}
      </div>
      <div className="flex gap-9 px-5 md:px-100">
        <div
          className={`flex gap-9 ${sectionsDesign === "all"
            ? "w-2/3 flex-col"
            : sectionsDesign === "first2"
              ? "w-full child:w-1/2"
              : sectionsDesign !== "one"
                ? "w-1/2 child:w-full"
                : "w-full child:w-full"
            }`}
        >
          {sections[selected].informations ? (
            <div className="bg-white rounded-2xl p-6 flex flex-col gap-2">
              {informationsLabel ? (
                <div className="font-primary-bold text-[22px] leading-8">{informationsLabel}</div>
              ) : (
                <></>
              )}
              {sections[selected].informations.map((info: any, key: number) => (
                <div key={key} className="flex leading-[19px]">
                  <div className="font-primary-regular text-base w-1/2">{info.key}:</div>
                  <div className="font-primary-semibold text-base w-1/2">{info.value}</div>
                </div>
              ))}
            </div>
          ) : (
            <></>
          )}
          {sections[selected].requirementsAndDocumentation ? (
            <div className={`bg-white rounded-2xl p-6 flex flex-col gap-2
            ${sectionsDesign === "all" ? "h-[-webkit-fill-available]" : ""}`}>
              {requirementsLabel && sectionsDesign === "not2" ? (
                <div className="font-primary-bold text-[22px] leading-8">{requirementsLabel}</div>
              ) : (
                <></>
              )}
              <div
                className="richTextContainer font-primary-regular text-base"
                dangerouslySetInnerHTML={{
                  __html: sections[selected].requirementsAndDocumentation,
                }}
              ></div>
            </div>
          ) : (
            <></>
          )}
        </div>

        {sections[selected].card?.title ? (
          <div
            className={`bg-white rounded-2xl px-8 py-6 flex items-center justify-center
          ${sectionsDesign !== "all" ? "w-1/2" : "w-1/3"}`}
          >
            <div className={`flex gap-8 items-center ${sectionsDesign !== "all" ? "w-full" : "flex-col"}`}>
              {sections[selected].card.icon ? (
                <div className={sectionsDesign !== "all" ? "w-[150px]" : "max-w-[286px]"}>
                  <Picture
                    imageClassNames={`h-auto ${sectionsDesign !== "all" ? "w-[150px]" : "max-w-[286px]"}`}
                    containerClassName={sectionsDesign !== "all" ? "!w-[150px]" : "!max-w-[286px]"}
                    image={basePath + sections[selected].card.icon}
                  />
                </div>
              ) : (
                <></>
              )}
              <div className="flex flex-col gap-8">
                {sections[selected].card.title ? (
                  <div
                    className={`font-primary-bold text-3.5xl leading-10
                  ${sectionsDesign !== "all" ? "w-full" : "text-center max-w-[286px]"}`}
                  >
                    {sections[selected].card.title}
                  </div>
                ) : (
                  <></>
                )}

                {sections[selected].card.link ? (
                  <>
                    <a href={sections[selected].card.link.url}
                      target={sections[selected].card.link.targetInNewWindow ? "_blank" : '_self'}
                      onClick={() => handleApplyClick(sections[selected].card)}
                      className="m-auto rounded-lg flex items-center transition-all duration-300 bg-theme-100 hover:bg-theme-200 border border-theme-100 hover:border-theme-200 text-white px-4 text-sm cursor-pointer opacity-1 justify-center h-[39px] !transition-none !py-0 w-full"
                    >{sections[selected].card.link.text}</a>

                    {/* <Button
                      styles={`flex items-center justify-center h-[39px] !transition-none !py-0 ${sectionsDesign !== "all" ? "w-[173px] !m-0" : "w-full"
                        }`}
                      title={
                        <div className="flex gap-2 items-center justify-center rotate">
                          <div className="font-primary-semibold text-sm">{sections[selected].card.link.text}</div>
                        </div>
                      }
                      arrows={false}
                      onclick={() => handleApplyClick(sections[selected].card)}
                    /> */}
                  </>
                ) : null}
              </div>
            </div>
          </div>
        ) : (
          <></>
        )}
      </div>
    </div>
  );
};
export default VisaTypes;
