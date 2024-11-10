/* eslint-disable max-len */
import React, { useEffect, useLayoutEffect } from "react";
import { AnchorsProps, Item } from "./IAnchors";
import { useResize } from "../../../util/hooks/useResize";
import Text from "../../common/atoms/Text/Text";


function isScrolledIntoView(el: any) {
    if (el) {
        var isVisible = el.getBoundingClientRect().top <= 500 && el.getBoundingClientRect().top >= -50;
        return isVisible;
    } else {
        return false
    }
}

const AnchorsComp: React.FC<AnchorsProps> = ({ title, links }) => {
    const { isMobile } = useResize();

    const handleClickScroll = (id: string) => {
        const element = document.getElementById(id);
        if (element) {
            element.scrollIntoView({ behavior: "smooth", block: "center" });
            setTimeout(() => {
                if (!isScrolledIntoView(element)) handleClickScroll(id)
            }, 500)
        }
    };
    const handleSectionClick = (event: React.MouseEvent, item: any): void => {
        if (!!item?.scroll) {
            handleClickScroll(item?.url);
        } else window.open(item?.url, !!item?.targetInNewWindow ? "__blank" : "_self");
    };
    const formatIndex = (index: number) => {
        // format the numbers as 01
        return index < 10 ? "0" + index : index;
    };

    //on mobile anchors should be under the breadcrubs
    const moveSectionIntoLeftCol1 = () => {
        const leftCol1 = document.querySelector('.leftCol1');
        const rightCol1 = document.querySelector('.rightCol1');
        const anchorsContainer = document.querySelector('#anchors-container');
        const anchorsParent = anchorsContainer?.parentNode?.parentNode?.parentNode;
        if (leftCol1 && rightCol1 && anchorsParent) {
            if (isMobile) {
                leftCol1.insertBefore(anchorsParent, leftCol1.firstChild);
            } else {
                rightCol1.insertBefore(anchorsParent, rightCol1.firstChild);
            }
        }
    }
    useEffect(() => {
        moveSectionIntoLeftCol1();
    })
    return (
        <div id="anchors-container">
            {title && (
                <>
                    <Text
                        text={title}
                        styles={"font-primary-bold text-sm uppercase"}
                    />
                    <div className="w-[39px] h-[3px] bg-theme-200 my-4 md:my-2 font-bold" />
                </>
            )}
            <div
                className={`${isMobile ? `flex gap-2 scrollBarHidden overflow-scroll
                child:max-w-full ltr:pr-5 rtl:pl-5 leading-4` : "leading-5"}
                child:cursor-pointer child:flex child:flex-col child:md:flex-row child:md:items-center
                child:gap-0 child:md:gap-4 child:bg-gray-200 child:md:bg-transparent child:px-4 child:py-2
                child:md:p-0 child:leading-normal child:rounded-2xl child:md:rounded-none
                `}
                style={isMobile ? { width: 'calc(100svw - 20px)' } : undefined}
            >
                {links &&
                    links?.length > 0 &&
                    links.map((item: Item, index: number) => {
                        return (
                            <a
                                className={index <= links.length - 2 ? "md:mb-2" : ''}
                                key={item?.number}
                                data-scroll-into={item.number}
                                onClick={(event) =>
                                    handleSectionClick(event, item)
                                }
                            >
                                {item?.number && (
                                    <span className="font-primary-regular text-sm md:text-base text-theme-100 md:w-5">
                                        {formatIndex(index + 1)}
                                    </span>
                                )}
                                <span
                                    className={`font-primary-semibold text-sm md:text-base ${isMobile
                                        ? "max-h-[3.6em] line-clamp-2 w-max"
                                        : "hover:underline"
                                        }`}
                                >
                                    {item.text}
                                </span>
                            </a>
                        );
                    })}
            </div>
        </div>
    );
};

export default AnchorsComp;

