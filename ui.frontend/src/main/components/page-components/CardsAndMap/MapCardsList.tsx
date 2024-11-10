/* eslint-disable max-len */
import React, { useEffect, useRef, useState } from "react";
import { DestinationsAndMapProps } from '../DestinationsAndMap/IDestinationsAndMap';
import DestinationCard from 'src/main/components/page-components/Cards/Destination/DestinationCard';
import AttractionCard from 'src/main/components/page-components/Cards/Attraction/AttractionCard';
import { useResize } from "src/main/util/hooks/useResize";
import './CardsAndMap.css';


interface MapCardsListProps {
    cardType: 'destination' | 'attraction';
    cards: any[],
    selectedValue?: number;
    onSelect: (destination: number) => void;
    wrapperClass?: string;
}
const MapCardsList: React.FC<MapCardsListProps> = ({ cards, cardType, onSelect, selectedValue, wrapperClass }) => {


    const { isMobile } = useResize();

    // when hovering on the destination card we should select the map marker but without scrolling
    const [event, setEvent] = useState('');
    const handleCardHoverOrClick = (selectedValueId: number, event: string) => {
        setEvent(event);
        onSelect(selectedValueId);
        setTimeout(() => {
            setEvent('');
        }, 200);
    }

    const mapCardsListParentDiv = useRef<HTMLDivElement>(null);
    //handle wrapper scroll when selected value is changed
    useEffect(() => {
        const cardsParent = mapCardsListParentDiv.current;

        const selectedCard = cardsParent?.querySelector(`#mapCard-${selectedValue}`) as HTMLElement;
        const offset = isMobile ? selectedCard?.offsetLeft : selectedCard?.offsetTop;

        if (cardsParent && offset !== undefined) {
            console.log(offset)
            if (event != 'hover') {
                if (isMobile) {
                    cardsParent.scrollTo({
                        left: offset - 20, //on mobile there is 20 px of padding
                        behavior: 'smooth'
                    });
                } else {
                    cardsParent.scrollTo({
                        top: offset,
                        behavior: 'smooth'
                    });
                }
            }
        }

    }, [selectedValue]);


    return (
        <div className={`cardsListWrapper ${cardType} ${wrapperClass}`} ref={mapCardsListParentDiv}>
            {cards.map((card, index) =>
                <div id={`mapCard-${card.card_id}`} key={index} onMouseEnter={() => handleCardHoverOrClick(card.card_id, 'hover')} onTouchStart={() => handleCardHoverOrClick(card.card_id, 'hover')} onMouseLeave={() => setEvent('')} onClick={() => handleCardHoverOrClick(card.card_id, 'click')} className=" cursor-pointer">
                    {cardType == 'destination' &&
                        <DestinationCard {...card} className={`w-[258px] md:w-auto p-0 md:p-2 ${selectedValue == card.card_id ? 'md:bg-white' : ''}`} />
                    }
                    {cardType == 'attraction' &&
                        <AttractionCard {...card} className={`w-[318px] md:w-auto ${selectedValue == card.card_id ? 'md:bg-white 0_15px_30px_0px_rgba(0,0,0,0.08)]' : ''}`} />
                    }
                </div>
            )}
        </div>
    );

};
export default MapCardsList;
