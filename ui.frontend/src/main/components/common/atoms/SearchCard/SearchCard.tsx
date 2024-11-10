import React, { useEffect } from "react";
import Button from "../Button/Button";
import Icon from "../Icon/Icon";
import Picture from "../Picture/Picture";
import Text from "../Text/Text";

var imagePath = "";

const SearchCard = (props: any) => {
    const { cards, buttonLabel, searchText, isMobile } = props;
    const imageStyle = "max-w-[283px] max-h-[283px] lg:w-full w-[76px] lg:h-[283px] h-[76px]";
    useEffect(() => {
        if (process.env.NODE_ENV === 'development') {
            imagePath = 'https://www.visitsaudi.com';
        } else {
            imagePath = '';
        }
    }, []);

    function getHighlightedText(text: string, highlight: string) {
        // Split on highlight term and include term into parts, ignore case
        if (text && highlight) {
            const parts = text?.split(new RegExp(`(${highlight?.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&")})`, 'gi'));
            return <span> {parts?.map((part, i) =>
                <span className="lg:font-primary-bold font-primary-semibold" key={i} style={part.toLowerCase() === highlight.toLowerCase() ?
                    { fontWeight: 'bold', color: '#000' } : {}}>
                    {part}
                </span>)
            } </span>;
        }

    }
    return (
        <>
            {cards.length != 0 ? <div>
                {cards?.map((item: any, index: any) => <>
                    <div key={item?.title} className="flex items-center w-full lg:mb-0 mb-4">
                        <a href={item?.url} title={item?.title} className={imageStyle}>
                            <Picture
                                image={imagePath + item.featureImage}
                                containerClassName={imageStyle}
                                imageClassNames={`lg:w-full lg:h-[283px] w-full h-full
                             min-w-[76px] img-blk object-cover rounded-2xl scale-200`}
                                pictureClassNames={`lg:w-full lg:h-[283px] w-[76px] h-[76px]`}
                                alt={item?.title}
                            />
                        </a>
                        <a href={isMobile ? item?.url : null} className='flex-1 ltr:lg:pl-[32px] rtl:lg:pr-[32px] ltr:pl-[16px] rtl:pr-[16px] lg:ltr:text-left lg:rtl:text-right'>
                            <div className="flex align-center">

                                {item?.region && <>
                                    <Icon name="location-mark" svgClass="ltr:mr-1 rtl:ml-1 mt-[1px]" /><Text text={item?.region || 'Amman'}
                                        styles="lg:text-sm text-xs uppercase font-primary-semibold text-[#000] lg:mt-0 mt-[1px]" type="span" />
                                    <span className="px-2">|</span>
                                </>}
                                {item?.categories && item?.categories?.slice(0, isMobile ? 1 : 3).map((category: any, index: any) => (
                                    <React.Fragment key={index}>
                                        {index > 0 && <span className="px-2">|</span>}
                                        <Text text={category}
                                            styles="line-clamp-1 lg:text-sm text-xs uppercase font-primary-bold text-[#000] lg:mt-0 mt-[1px]" type="span" />
                                    </React.Fragment>
                                ))}
                            </div>
                            <Text text={getHighlightedText(item.title, searchText) || item.title}
                                styles="lg:text-1.5xl text-base lg:text-[#000] text-[#787878]  lg:font-primary-bold font-primary-semibold lg:line-clamp-2 line-clamp-1 lg:mt-2 mt-1 rtl:pb-[2px]" type="p" />
                            {buttonLabel ?
                                <Button
                                    onclick={() => {
                                        if (item?.url) {
                                            window.location.href = item?.url;
                                        }
                                    }}
                                    title={buttonLabel}
                                    arrows={false}
                                    spanStyle="!px-0 relative top-[1px]"
                                    styles="max-w-[142px] h-[44px] ltr:ml-0 rtl:mr-0 mt-8 w-full flex justify-center text-sm p-2 lg:text-base font-primary-semibold rounded-lg lg:block hidden"
                                >
                                </Button> : null}

                        </a>
                    </div>

                    {cards && (index + 1 != cards.length) ? <hr className='text-[#ddd8d8] my-10 lg:block hidden' /> : null}</>)}

            </div> : ""
            }
        </>
    );
};

export default SearchCard;
