import React, { useEffect, useRef, useState } from "react";
import Icon from "src/main/components/common/atoms/Icon/Icon";

interface BreadcrumbsProps {
    items: {
        link: {
            url: string;
            targetInNewWindow: boolean;
        };
        title: string;
    }[];
    cssMarginClass: string;
}

const Breadcrumbs: React.FC<BreadcrumbsProps> = ({ items }) => {
    // Inject Home into the array
    // const [RenderedItems, setRenderedItems] = useState([
    //     {
    //         link: {
    //             url: '/',
    //             targetInNewWindow: false,
    //         },
    //         title: 'Home',
    //     },
    //     ...items,
    // ]);
    const [RenderedItems, setRenderedItems] = useState([...items]);

    const containerRef = useRef<HTMLDivElement>(null);
    const [truncatedItems, setTruncatedItems] = useState(RenderedItems);
    const [truncatedIndex, setTruncatedIndex] = useState(0);
    const [bannerOnTopOfBreadcrumbs, setBannerOnTopOfBreadcrumbs] =
        useState(false);

    useEffect(() => {
        let resizeTimeout: NodeJS.Timeout;

        const handleResize = () => {
            clearTimeout(resizeTimeout);
            resizeTimeout = setTimeout(() => {
                setTruncatedItems(RenderedItems);
                const containerWidth = containerRef.current?.offsetWidth || 0;
                const itemElements =
                    containerRef.current?.querySelectorAll(
                        ".breadcrumb-item"
                    ) || [];

                let totalWidth = 0;
                let truncateIndex = itemElements.length;

                for (let index = itemElements.length - 1; index >= 0; index--) {
                    const element = itemElements[index] as HTMLElement;
                    totalWidth += element.offsetWidth + 5;

                    if (totalWidth > containerWidth) {
                        truncateIndex = index;
                        break;
                    }
                }

                if (truncateIndex < itemElements.length) {
                    const truncatedItems = RenderedItems.slice(
                        truncateIndex + 1
                    );
                    setTruncatedItems(truncatedItems);
                    setTruncatedIndex(itemElements.length - truncateIndex - 1);
                } else {
                    setTruncatedItems(RenderedItems);
                    setTruncatedIndex(0);
                }
            }, 100); // Adjust the debounce delay as needed
        };
        const checkBannerOnTop = () => {
            const bannerTop = document.querySelector(
                ".banner-on-top-of-breadcrumbs"
            );
            setBannerOnTopOfBreadcrumbs(!!bannerTop);
        };
        checkBannerOnTop();
        handleResize();
        window.addEventListener("resize", handleResize);
        window.addEventListener("load", handleResize);

        return () => {
            clearTimeout(resizeTimeout);
            window.removeEventListener("resize", handleResize);
        };
    }, [items]);

    return (
        <div
            id="breadcrumb-container"
            className={`flex overflow-hidden items-center text-xs md:text-sm
             whitespace-nowrap px-5 md:px-100 font-primary-regular ${
                 !!bannerOnTopOfBreadcrumbs
                     ? "!-mt-4 md:!-mt-10 md:!pb-10 !pb-8"
                     : "mb-6 mt-24 md:mt-30"
             }`}
            ref={containerRef}
        >
            {truncatedIndex > 0 && (
                <div className="breadcrumb-item flex items-center">
                    <span className="text-[#4B4B4B]">...</span>
                    <Icon
                        name="chevron"
                        svgClass={`w-2 h-2 inline-block mx-1`}
                    />
                </div>
            )}
            {truncatedItems.map((item, index) => (
                <div className="breadcrumb-item flex items-center" key={index}>
                    <>
                        <a
                            href={item.link.url}
                            target={item.link.targetInNewWindow ? "_blank" : ""}
                            className="text-[#4B4B4B] relative after:bg-gray-100 after:absolute after:h-px after:w-0
                                        after:bottom-0 after:left-2/4 hover:after:left-0 hover:after:w-full
                                        after:transition-all after:duration-300"
                        >
                            {item.title}
                        </a>
                        {index < truncatedItems.length - 1 && (
                            <Icon
                                name="chevron"
                                svgClass={`w-2 h-2 inline-block mx-1 rtl:rotate-180`}
                            />
                        )}
                    </>
                </div>
            ))}
        </div>
    );
};

export default Breadcrumbs;

