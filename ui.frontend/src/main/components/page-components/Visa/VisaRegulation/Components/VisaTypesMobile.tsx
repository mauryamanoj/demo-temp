import React, { useEffect, useState } from "react";
import Button from "src/main/components/common/atoms/Button/Button";
import Icon from "src/main/components/common/atoms/Icon/Icon";
import Picture from "src/main/components/common/atoms/Picture/Picture";
import { trackAnalytics, trackingEvent } from 'src/main/util/updateAnalyticsData';

// Note:you see the icon locally and on QA
const basePath = process.env.NODE_ENV == "development" ? "https://qa-revamp.visitsaudi.com" : window.location.origin;

const VisaTypesMobile = ({ sections, visaTypesTitle, requirementsLabel, eligibleForLabel, selectedCountry }: any) => {
  const [selected, setSelected] = useState(-1);
  const [oldSelected, setOldSelected] = useState(selected);

  useEffect(() => {
    if (selected > -1) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "auto";
    }
  }, [selected]);

  function handleTabClick(key: any, label: string) {

    trackingEvent({
      event_name: "visa_type_interaction",
      title: label,
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

  const setPrevious = () => {
    setSelected(selected === 0 ? sections.length - 1 : selected - 1);
    scrollToTop();
  };

  const setNext = () => {
    setSelected(selected + 1 === sections.length ? 0 : selected + 1);
    scrollToTop();
  };

  const handleApplyClick = (label: any) => {
    const info = sections[selected]?.informations;
    var visaPrice = info ? info[0]?.value : "";

    trackingEvent({
      event_name: "eVisa_apply_now",
      title: label,
      url: sections[selected].card.link.url,
      visaPrice: visaPrice,
      countrySelected: selectedCountry,
      sectionName: label,
      currency: "SAR"
    });
    trackAnalytics({
      trackEventname: "eVisa_apply_now",
      trackName: "dl_push",
      title: label,
      url: sections[selected].card.link.url,
      visaPrice: visaPrice,
      countrySelected: selectedCountry,
      sectionName: label,
      currency: "SAR"
    });

    setTimeout(() => {
      window.open(
        sections[selected].card.link.url,
        sections[selected].card.link.targetInNewWindow ? "_blank" : "_self"
      );
    }, 400);
  };

  const scrollToTop = () =>
    setTimeout(() => {
      document.getElementById("visa-mobile-section-popup")?.scrollTo({
        top: 0,
        behavior: "smooth",
      });
    }, 50);

  const NextPrevOrnament = (
    <div
      className="absolute top-0 bottom-0 ltr:right-0 rtl:left-0
        w-6 rounded-r-2xl"
    >
      <Icon name="ornament-v-m4-dark-48" svgClass="w-full h-full ltr:rounded-r-2xl rtl:rounded-l-2xl" />
    </div>
  );

  const updatedOldSelected = () => {
    setTimeout(() => setOldSelected(selected), 10)
    return null;
  }

  return (
    <div className="flex flex-col gap-8 mt-[68px] mb-16 px-5">
      {visaTypesTitle ? <div className="font-primary-bold text-3xl leading-9">{visaTypesTitle}</div> : <></>}
      {sections.map((section: any, key: number) => (
        <div
          key={key}
          className={`bg-theme-100 text-white py-[26px] rounded-2xl flex
                    ltr:pl-6 rtl:pr-6 ltr:pr-[11px] rtl:pl-[11px]
          ltr:h-[183px] rtl:h-[200px] justify-between relative`}
          onClick={() => handleTabClick(key, section?.title)}
        >
          <div className="flex flex-col justify-center gap-5 w-auto max-w-[50%] md:max-w-[70%]">
            <div
              className="font-primary-bold text-3.5xl ltr:leading-9 rtl:leading-10 rtl:pt-1
            break-words line-clamp-2"
            >
              {section.title}
            </div>
            <Button
              spanStyle="text-base !px-0"
              styles="h-[44px] w-fit !m-0 font-primary-semibold text-base"
              transparent
              title={section.card?.viewMoreMobileLabel ?? "view more"}
              arrows={false}
            />
          </div>
          <div className="absolute top-0 bottom-0 ltr:right-6 rtl:left-6 w-20">
            <Icon name="ornament-v-p1-dark-80" svgClass="w-full h-full" />
          </div>
          {section.mobileIcon ? (
            <Picture
              containerClassName="flex justify-center items-center relative !w-[150px]"
              image={basePath + section.mobileIcon}
              imageClassNames={"rtl:scale-x-[-1] w-[150px] h-auto"}
            />
          ) : (
            <></>
          )}
        </div>
      ))}
      <div
        className={`fixed w-screen bottom-0 left-0 z-50 bg-gray-50/40
      ${selected > -1 ? "h-full" : "h-0"} transition-all duration-300`}
      >
        {selected > -1 ? (
          <div className="bg-white rounded-t-[32px] mt-[5px] h-full relative">
            <div className="flex justify-end">
              <div className="px-5 pt-5 pb-2" onClick={() => setSelected(-1)}>
                <Icon name="close" />
              </div>
            </div>

            <div id="visa-mobile-section-popup" className={`pb-[76px] pt-5 bg-white overflow-y-auto h-[93svh]`}>
              {sections[selected].mobileIcon && (
                <div className="h-[80px] ltr:lg:mr-0 ltr:mr-6 rtl:lg:ml-0 rtl:ml-6 ">
                  <div className="relative w-fit">
                    <div>
                      <Icon name="ornament-h-p1-80" />
                    </div>
                    <div className="absolute top-[-20px] ltr:left-[90%] rtl:right-[90%] z-30">
                      {oldSelected !== selected ? updatedOldSelected() : <Picture
                        image={basePath + sections[selected].mobileIcon}
                        imageClassNames={"rtl:scale-x-[-1] object-contain max-w-[120px]"}
                      />
                      }
                    </div>
                  </div>
                </div>
              )}
              <div className="font-primary-bold text-3xl leading-7 rtl:leading-10 mt-6 mx-5">
                {sections[selected].title}
              </div>
              <div className="flex flex-col">
                {sections[selected].informations ? (
                  <div className="py-9 mx-5 flex flex-col gap-2 border-b-[0.25px] border-gray-50">
                    {sections[selected].informations.map((info: any, key: number) => (
                      <div key={key} className="flex">
                        {key + 1 !== sections[selected].informations.length ? (
                          <div className="font-primary-regular text-sm w-1/2">{info.key}:</div>
                        ) : (
                          <></>
                        )}
                        <div
                          className={`font-primary-semibold text-sm ${key + 1 !== sections[selected].informations.length
                            ? "w-1/2 ltr:text-right rtl:text-left"
                            : ""
                            }`}
                        >
                          {info.value}
                        </div>
                      </div>
                    ))}
                  </div>
                ) : (
                  <></>
                )}
                {sections[selected].requirementsAndDocumentation ? (
                  <div className="bg-white rounded-2xl py-9 px-5 flex flex-col gap-2">
                    {requirementsLabel ? (
                      <div className="font-primary-bold text-base leading-8">{requirementsLabel}</div>
                    ) : (
                      <></>
                    )}
                    <div
                      className="richTextContainer font-primary-regular text-sm"
                      dangerouslySetInnerHTML={{
                        __html: sections[selected].requirementsAndDocumentation,
                      }}
                    ></div>
                  </div>
                ) : (
                  <></>
                )}
              </div>
              <div>
                {eligibleForLabel ? (
                  <div className="font-primary-bold text-3xl leading-9 rtl:leading-[45px] px-5 mb-6 mt-1">{eligibleForLabel}</div>
                ) : (
                  <></>
                )}
                <div className="flex h-[87px] gap-2 px-5 font-primary-bold text-xs leading-4 mb-12">
                  {sections.length > 2 ? (
                    <>
                      <div
                        className="w-1/2 h-full bg-theme-100 text-white
                                                rounded-[18px] px-6 flex items-center relative"
                        onClick={setPrevious}
                      >
                        {selected === 0 ? sections[sections.length - 1].title : sections[selected - 1].title}
                        {NextPrevOrnament}
                      </div>
                      <div
                        className="w-1/2 h-full bg-theme-100 text-white rounded-[18px]
                                                 px-6 flex items-center relative"
                        onClick={setNext}
                      >
                        {selected + 1 === sections.length ? sections[0].title : sections[selected + 1].title}
                        {NextPrevOrnament}
                      </div>
                    </>
                  ) : (
                    <div
                      className="w-full h-full bg-theme-100 text-white rounded-[18px]
                                             px-6 flex items-center relative"
                      onClick={setNext}
                    >
                      {selected + 1 === sections.length ? sections[0].title : sections[selected + 1].title}
                      {NextPrevOrnament}
                    </div>
                  )}
                </div>
              </div>
              {sections[selected].card?.link?.text ? (
                <div
                  className={`bg-white px-5 py-4 w-full fixed bottom-0
                                border-y-[0.25px] border-gray-50`}
                >
                  <div>
                    <Button
                      styles="flex w-full items-center justify-center h-[44px] !py-0"
                      title={
                        <div className="flex gap-2 items-center justify-center">
                          <div className="font-primary-semibold text-base">{sections[selected].card.link.text}</div>
                          {/* <Icon name="apply-now" svgClass={`rtl:rotate-180`} /> */}
                        </div>
                      }
                      arrows={false}
                      onclick={() => handleApplyClick(sections[selected].card?.link?.text)}
                    />
                  </div>
                </div>
              ) : (
                <></>
              )}
            </div>
          </div>
        ) : (
          <></>
        )}
      </div>
    </div>
  );
};
export default VisaTypesMobile;
