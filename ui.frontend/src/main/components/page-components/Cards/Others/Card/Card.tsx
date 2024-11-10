/* eslint-disable max-len */
import React from 'react';
import Button from 'src/main/components/common/atoms/Button/Button';
import Icon from 'src/main/components/common/atoms/Icon/Icon';
import './Card.css';
import Text from '../../../../common/atoms/Text/Text';
import Picture from 'src/main/components/common/atoms/Picture/Picture';
import { getImage } from 'src/main/util/getImage';
import { gtmCustomEventClick } from "src/main/util/googleTagManager";
import { trackEvents } from 'src/main/util/updateAnalyticsData';
import { getLanguage } from 'src/main/util/getLanguage';

const OtherOrMap: React.FC<any> = (props: any) => {

    const { card, type } = props;

    const setGtmCustomLink = (card: any) => {
        const cardInfo: any = {
            event: card?.ctaData?.ctaEventName,
            section_name: card?.ctaData?.sectionName,
            click_text: card?.ctaData?.cardCtaLink,
            link: card?.ctaData?.cardCtaLink,
            language: getLanguage(),
            page_category: card?.ctaData?.pageCategory,
            page_subcategory: card?.ctaData?.pageSubCategory,
            device_type: window.navigator.userAgent,
        };
        if (card?.ctaData?.ctaEventName) {
            gtmCustomEventClick(cardInfo);
        }

        const dataSet: any = {};
        dataSet.trackEventname = card?.ctaData?.ctaEventName;
        dataSet.trackSection = card?.ctaData?.sectionName;
        dataSet.trackEventname && trackEvents(dataSet);
    };


    function determineBlockType(type: string) {
        if (type === 'Map') {
            return 'map-block max-w-xs text-center rounded overflow-hidden mb-4';
        }
        else if (type === 'GuideCardsItem') {
            return 'map-block max-w-full text-center rounded  bg-white rounded-[20px]';
        } else {
            return 'other-block max-w-lg flex mb-4';
        }
    }

    function determineStyle(type: string) {
        if (type === 'Map') {
            return 'px-6 py-4';
        }
        else if (type === 'GuideCardsItem') {
            return 'px-2 pb-4 lg:px-3 text-left w-full text-center';
        }
        else {
            return 'px-2 py-4 lg:px-3 text-left flex align-item-center justify-center flex-col-reverse w-full';
        }
    }

    function determineImageStyle(type: string) {
        if (type === 'GuideCardsItem') {
            return 'm-auto p-2 max-w-[355px]';
        }
        else {
            return 'w-full img-blk rounded-2xl max-w-[180px] max-h-[180px] lg:max-w-[283px] lg:max-h-[284px] m-auto';
        }
    }

    function determinePicStyle(type: string) {
        if (type === 'GuideCardsItem') {
            return 'h-max';
        }
        return 'picture-element';
    }

    function renderButtonAndText(type: string, card: any) {
        if (type == 'Map') {
            return <>
                <Text text={card?.cardTitle || card?.title} styles="text-lg lg:text-1.5xl mb-2 font-primary-bold mb-0" />
                <a
                    href={card?.cardCtaLink}
                    onClick={() => {
                        setGtmCustomLink(card);
                    }}
                    className="text-sm font-primary-bold text-theme-100 hover:text-theme-200 bg-transparent  hover:bg-transparent"
                >{card.description || card.cardCtaLabel}</a></>;
        }
        else if (type == 'GuideCardsItem') {
            return <>
                <Text text={card?.cardTitle || card?.title} styles="text-lg lg:text-3.5xl font-primary-bold mb-0 mt-[-10px]" />
                <div className="flex align-item-center -ml-1 mb-1 lg:mb-0">
                    <a
                        href={card?.cardCtaLink}
                        onClick={() => {
                            setGtmCustomLink(card);
                        }}
                        className=" m-auto text-sm font-primary-bold text-theme-100 hover:text-theme-200 bg-transparent  hover:bg-transparent"
                    >{card.description || card.cardCtaLabel}</a>
                    <Text text={card.title} styles="text-xs lg:text-xs lg:mb-2 font-primary-bold" type="span" /></div></>;
        }
        else {
            return <>
                <Text text={card?.cardTitle || card?.title} styles="text-lg lg:text-1.5xl mb-2 font-primary-bold mb-0" /><div className="flex align-item-center -ml-1 mb-1 lg:mb-0">
                    <Icon
                        name="location-mark"
                    /> <Text text={card.title} styles="text-xs lg:text-xs lg:mb-2 font-primary-bold" type="span" /></div></>;
        }
    }

    return (

        <div className={determineBlockType(type)}>
            {card?.image ?
                <Picture
                    image={getImage(card?.image)}
                    breakpoints={card?.image?.breakpoints}
                    imageClassNames={determineImageStyle(type)}
                    containerClassName={determinePicStyle(type)}
                    alt={card?.image?.alt}
                />
                : <></>}
            <div className={determineStyle(type)} >

                {renderButtonAndText(type, card)}
            </div>
        </div>

    );
};

export default OtherOrMap;
