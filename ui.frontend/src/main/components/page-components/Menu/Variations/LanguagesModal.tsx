import React, { useEffect, useState } from 'react'
import Icon from 'src/main/components/common/atoms/Icon/Icon';
import api from 'src/main/util/api';
import { getLanguage } from 'src/main/util/getLanguage';
import { useResize } from 'src/main/util/hooks/useResize';
import { ILanguages } from "../IMainMenu";
import getIsRTL from "src/main/util/getIsRTL";
import { trackAnalytics, trackingEvent, extractPageInfoFromEventProperties } from "src/main/util/updateAnalyticsData";


const lang = getLanguage();
const currentPath = window.location.pathname
function LanguagesModal({ languages, setImage }: any) {

    const { isMobile } = useResize();

    const [unAvailableLanguages, setUnAvailableLanguages] = useState([]);
    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {
        if (!unAvailableLanguages?.length) {
            fetchUnAvailableLanguages();
        }
    }, [])

    const fetchUnAvailableLanguages = async () => {
        const basePath = process.env.NODE_ENV == 'development' ? 'https://www.visitsaudi.com' : window.location.origin;
        const url = `${basePath}/bin/api/v1/langswitcher?currentLanguage=${lang}&pagePath=${currentPath.replace('.html', '')}`;

        try {
            setIsLoading(true);
            const { data } = await api.get({ url });
            setIsLoading(false);
            if (data.pageUnavailableLocales) {
                setUnAvailableLanguages(data.pageUnavailableLocales);
            }
        } catch (err) {
            setIsLoading(false);

        }

    }
    const isLangAvailable = (langItem: any) => {
        if (unAvailableLanguages.length) {
            const filteredLocales = unAvailableLanguages.filter(locale => locale == langItem.ctaLink.substring(1)); // compare "/en" with "en"
            if (filteredLocales.length) {
                return false;
            }
            return true;
        }
        return true;
    }

    const handleItemClick = (language: any) => {
        //const pageInfo = extractPageInfo(url);
        const pageInfo = extractPageInfoFromEventProperties();

        trackingEvent({
            event_name: language ? "header_change_language" : "header_navigation",
            title: '',
            page_category: pageInfo?.pageCategory,
            page_subcategory: pageInfo?.pageSubcategory,
            city: pageInfo?.city,
            event_category: "navigation",
            navigation_name: language,
            language: language ? language.ctaLink : getLanguage()
        });

        trackAnalytics({
            trackEventname: language ? "header_change_language" : "header_navigation",
            trackName: "dl_push",
            title: '',
            page_category: pageInfo?.pageCategory,
            page_subcategory: pageInfo?.pageSubcategory,
            city: pageInfo?.city,
            event_category: "navigation",
            navigation_name: language,
            language: language ? language.ctaLink : getLanguage()
        });

        setTimeout(() => {
            window.location.href = language && window.location.href.replace(window.location.pathname.slice(0, 4), language.ctaLink + "/");
        }, 400);
    };

    const handleLanguageClick = (item: any) => {
        if (isLangAvailable(item)) {
            handleItemClick(item);
        } else {
            return false;
        }
    }

    function isElementVisible(element: any) {
        return element.offsetParent !== null && element.offsetWidth > 0 && element.offsetHeight > 0;
    }


    const toggleUnAvalableMsgTooltip = (event: React.MouseEvent) => {
        const element = event.currentTarget as HTMLElement;
        const tooltip = element?.parentElement?.querySelector('.msgTooltipSelector') as HTMLElement;



        if (isElementVisible(tooltip)) {
            document.querySelectorAll('.msgTooltipSelector').forEach(item => item?.classList.add('hidden'));
            return;
        }
        document.querySelectorAll('.msgTooltipSelector').forEach(item => item?.classList.add('hidden'));

        tooltip.classList.remove('hidden');
    }

    return (
        <div className="flex flex-col md:flex-row gap-5 md:max-h-44 min-h-[80vh] md:min-h-[198px] overflow-y-auto relative">
            {/* {isLoading && <div className=' inset-0 absolute bg-white opacity-50'></div>} */}
            <div className="mb-2 md:mb-11 mr-16 pr-16 rtl:mr-0 rtl:ml-16 rtl:pr-0 rtl:pl-16">
                <div className="flex gap-3 cursor-pointer items-center md:text-theme-100">
                    <div className="font-primary-bold text-lg uppercase">{languages?.label}</div>
                </div>
            </div>
            <div className='flex flex-col gap-6 flex-wrap w-full'>
                {languages.data.map((item: any, index: number) =>
                    <div className={`md:border-l-[1px] md:rtl:border-l-0 md:rtl:border-r-[1px] md:border-gray/50 
                                    md:py-1 md:px-2 block text-ellipsis font-primary-bold 
                                    md:font-primary-semibold text-sm md:text-base 
                                    ${item.isSelected && "md:border-theme-100 text-theme-100"}
                           
                           ${isLangAvailable(item) && 'hover:border-theme-100 hover:text-theme-100 cursor-pointer'}
                           ${!isLangAvailable(item) && 'text-disabled'}`}
                        key={index}
                        onClick={() => handleLanguageClick(item)}
                        onMouseEnter={() => {
                            if (setImage) {
                                setImage(null);
                                setTimeout(() => setImage(item.image), 10);
                            }
                        }}
                    >
                        <div className='flex align-items-center justify-between md:justify-normal'>
                            {item.ctaLabel}
                            {item.unAvailableLanMessage && <div className='relative'>
                                {!isLangAvailable(item) && <div onClick={(event) => toggleUnAvalableMsgTooltip(event)}><Icon wrapperClass='fill-disabled2 cursor-pointer' svgClass='h-5 w-5 md:mx-2' name="alertInfoCircle" /></div>}
                                {!isLangAvailable(item) && <div className="msgTooltipSelector flex flex-col gap-2 w-fit whitespace-nowrap z-10 absolute top-[105%] left-auto right-0 md:right-2 rtl:right-auto rtl:left-0 rtl:md:left-2 hidden p-2 bg-white rounded-lg text-xs text-black leading-[17.8px]">
                                    {item.unAvailableLanMessage}
                                    {item.unAvailableLanLink && <a className='text-xxs text-theme-200 flex items-center gap-2' href={item.unAvailableLanLink}>{item.unAvailableLanLinkLabel} <Icon name="arrow-right" svgClass='w-3 h-3 rtl:rotate-180' /></a>}
                                </div>}
                            </div>
                            }
                        </div>
                    </div>

                )}
            </div>


        </div >
    )
}

export default LanguagesModal

