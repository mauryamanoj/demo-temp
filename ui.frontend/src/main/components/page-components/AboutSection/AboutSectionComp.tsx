import React, { useEffect, useRef, useState } from "react";
import Text from "src/main/components/common/atoms/Text/Text";
import CategoriesTagsInline from "src/main/components/common/organisms/CategoriesTagsInline/CategoriesTagsInline";
import Icon from "../../common/atoms/Icon/Icon";
import {
    FacebookShareButton,
    TwitterShareButton,
    WhatsappShareButton,
    EmailShareButton,
} from "react-share";
import { AboutSectionInterface } from "./IAboutSection";
import FavoriteButton from "../../common/atoms/FavoriteButton/FavoriteButton";

const AboutSectionComp: React.FC<AboutSectionInterface> = (props) => {
    const {
        aboutTitle,
        aboutDescription,
        hideReadMore,
        readMoreLabel,
        readLessLabel,
        categoriesTags,
        hideFavorite,
        hideShare,
        updateFavUrl,
        deleteFavUrl,
    } = props;

    const [expanded, setExpanded] = useState(false);
    const [showIconsSection, setShowIconsSection] = useState(false);
    const [isScrollActive, setScrollActive] = useState<boolean>(false);
    const scrollOffset = 70;

    const descriptionRef = useRef<HTMLParagraphElement>(null);
    const [truncatedDescription, setTruncatedDescription] = useState("");
    const wrapperRef = useRef(null);
    useOutsideAlerter(wrapperRef);

    const truncationThereshould = 500;
    useEffect(() => {
        truncateDescription();
        if (window.pageYOffset != 0) {
            setScrollActive(true);
        }
        window.addEventListener("scroll", scrollHhandler);
        return () => {
            window.removeEventListener("scroll", scrollHhandler);
        };

    }, []);

    const truncateDescription = () => {
        if (
            aboutDescription &&
            aboutDescription.length > truncationThereshould &&
            !hideReadMore
        ) {
            setTruncatedDescription(
                `${aboutDescription?.substring(0, truncationThereshould)}...`
            );
        } else {
            setTruncatedDescription(aboutDescription);
        }
    };

    const handleReadMore = () => {
        setExpanded(!expanded);
    };

    const showIcons = () => {
        setShowIconsSection(!showIconsSection);
    };

    function useOutsideAlerter(ref: any) {
        useEffect(() => {
            /**
             * Alert if clicked on outside of element
             */
            function handleClickOutside(event: any) {
                if (ref.current && !ref.current.contains(event.target)) {
                    setShowIconsSection(false);
                }
            }
            // Bind the event listener
            document.addEventListener("mousedown", handleClickOutside);
            return () => {
                // Unbind the event listener on clean up
                document.removeEventListener("mousedown", handleClickOutside);
            };
        }, [ref]);
    }

    const scrollHhandler = () => {
        const hasScrolled = window.pageYOffset > scrollOffset;
        if (hasScrolled) {
            setScrollActive(true);
        } else {
            setScrollActive(false);
        }
    };

    useEffect(()=>{
        const aboutSection = document.querySelectorAll('.about-section');
        if(aboutSection && aboutSection.length !=0){
            for (let index = 0; index < aboutSection.length; index++) {
                const element = aboutSection[index];
                if(element?.closest('.layout2Cols')== null){
                   element?.querySelector('.about-block')?.classList.add('md:px-100',
                   'px-5','!m-auto',/* 'lg:pb-20','pb-16' */);
                }
            }
        }
    },[]);

    useEffect(() => {
        setShowIconsSection(false);
    }, [isScrollActive]);

    return (
        <section className="about-block">
            <div className="flex justify-between items-baseline mb-6">
                <Text text={aboutTitle} isTitle styles="!mb-0 leading-[37.14px] lg:leading-normal lg:mt-[-12px]" />
                <div ref={wrapperRef} className="relative">
                    <div className="gap-4 flex items-center">
                        {!hideFavorite && (
                            <div className="p-2 rounded-md w-[35px] h-[35px]">
                                <FavoriteButton
                                    id={aboutTitle}
                                    wrapperClass="themed bg-transparent hover:bg-transparent !w-4 h-full"
                                    boldIcon
                                    updateFavoriteUrl={updateFavUrl}
                                    deleteFavoriteUrl={deleteFavUrl}
                                />
                            </div>
                        )}
                      {!hideShare &&
                        <div
                            className="p-2 rounded-md w-[35px] h-[35px]"
                            onClick={showIcons}
                        >
                            <Icon
                                name="share-v1"
                                svgClass="cursor-pointer bg-transparent"
                                wrapperClass="fill-themed "
                            />
                        </div>
}
                    </div>
                    {showIconsSection ?
                    <ul
                        className={`absolute top-12 ltr:right-0 rtl:left-0 bg-white border border-[#ededed]
                        p-4 shadow-xl rounded-2xl flex flex-col gap-2 min-h-[50px] z-10`}
                    >
                        <li className="cursor-pointer">
                            <FacebookShareButton url={window.location.href}>
                                <div className="flex items-center gap-2">
                                    <Icon
                                        name="facebook"
                                        svgClass="cursor-pointer w-[18px] h-[18px]"
                                    />
                                    <Text
                                        styles="font-primary-regular"
                                        text="Facebook"
                                        tag="span"
                                    />
                                </div>
                            </FacebookShareButton>
                        </li>
                        <li className="cursor-pointer">
                            <TwitterShareButton url={window.location.href}>
                                <div className="flex items-center gap-2">
                                    <Icon
                                        name="twitter"
                                        svgClass="cursor-pointer w-[18px] h-[18px]"
                                    />
                                    <Text
                                        styles="font-primary-regular ltr:ml-[2px] rtl:mr-[2px]"
                                        text="X"
                                        tag="span"
                                    />
                                </div>
                            </TwitterShareButton>
                        </li>
                        <li className="cursor-pointer">
                            <WhatsappShareButton url={window.location.href}>
                                <div className="flex items-center gap-2">
                                    <Icon
                                        name="whats-up"
                                        svgClass="cursor-pointer w-[18px] h-[18px]"
                                    />
                                    <Text
                                        styles="font-primary-regular ltr:ml-[2px] rtl:mr-[2px]"
                                        text="Whatsapp"
                                        tag="span"
                                    />
                                </div>
                            </WhatsappShareButton>
                        </li>
                        <li className="cursor-pointer">
                            <EmailShareButton url={window.location.href}>
                                <div className="flex items-center gap-2">
                                    <Icon
                                        name="email"
                                        svgClass="cursor-pointer w-[18px] h-[18px]"
                                    />
                                    <Text
                                        styles="font-primary-regular"
                                        text="Email"
                                        tag="span"
                                    />
                                </div>
                            </EmailShareButton>
                        </li>
                    </ul>
                    : <></>}
                </div>
            </div>

            <div
                ref={descriptionRef}
                className="font-primary-regular text-base"
            >
                <div
                    className="text-sm md:text-base font-primary-regular richTextContainer
                               leading-[16.8px] lg:leading-[19.2px]"
                    dangerouslySetInnerHTML={{
                        __html: expanded
                            ? aboutDescription
                            : truncatedDescription,
                    }}
                ></div>
            </div>

            {!hideReadMore &&
                aboutDescription &&
                aboutDescription.length > truncationThereshould && (
                    <a
                        className="text-theme-100 hover:text-theme-200 mt-4 md:mt-5 cursor-pointer font-primary-bold text-sm capitalize"
                        onClick={handleReadMore}
                    >
                        {expanded ? readLessLabel : readMoreLabel}
                    </a>
                )}
                <div className="mt-4 md:mt-5">
            {categoriesTags && <CategoriesTagsInline max={3} tags={categoriesTags} />}
            </div>
        </section>
    );
};

export default AboutSectionComp;