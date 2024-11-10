/* eslint-disable max-len */
import React, { Suspense, useState, useEffect, CSSProperties, useRef, useMemo, useCallback } from "react";
import Map from "src/main/components/common/atoms/Map/Map";

import MapCardsList from "../CardsAndMap/MapCardsList";
import Text from "src/main/components/common/atoms/Text/Text";

import { DestinationsAndMapProps } from "./IDestinationsAndMap";

const DestinationsAndMapComp = (props: DestinationsAndMapProps) => {
    const { mapApiKey, showMapBorder, title, link, destinations } = props;
    const [isOnlyMedia, setIsOnlyMedia] = useState(window.innerWidth === 768);
    const [margins, setMargins] = useState(true);
    const currentElement = useRef<HTMLDivElement | null>(null);

    useEffect(() => {
        const handleResize = () => {
            setIsOnlyMedia(window.innerWidth === 768);
        };

        window.addEventListener("resize", handleResize);

        return () => {
            window.removeEventListener("resize", handleResize);
        };
    }, []);

    // Inject 'id' into each object
    const updatedDestinations = useMemo(() => (destinations.map((destination, index) => {
        return {
            ...destination,
            card_id: index + 1,
        };
    })), [destinations]);

    const [selectedValue, setSelectedValue] = useState(
        updatedDestinations[0].card_id
    );
    const handleDestinationSelect = useCallback((selectedValueId: number) => {
        setSelectedValue(selectedValueId);
    }, []);

    useEffect(() => {
        if (currentElement.current?.closest(".layout2Cols")) { setMargins(false); }
    }, []);

    return (
        <section className={margins ? "md:px-5 lg:px-100" : undefined}>
            <div ref={currentElement} className={`flex flex-col md:flex-row md:justify-between md:items-center
            ${margins ? " px-5 md:px-0" : ""}`}>
                <Text styles="!mb-0 md:!mb-10" text={title} isTitle />
                {link?.text && (
                    <a
                        href={link.url}
                        target={link.targetInNewWindow ? "_blank" : ""}
                        className="themed hover:underline mb-6 cursor-pointer font-primary-semibold font-bold w-fit"
                    >
                        {link.text.slice(0, 30)}
                    </a>
                )}
            </div>

            <div
                className="flex flex-col md:flex-row md:gap-4 h-[648px] overflow-hidden relative"
                style={
                    isOnlyMedia
                        ? ({ flexDirection: 'column' } as CSSProperties) // if remove this, map ui will crash in 786px media query
                        : undefined
                }
            >
                <div className="md:w-[31%] lg:w-[24%]">
                    <MapCardsList
                        cardType="destination"
                        cards={updatedDestinations}
                        selectedValue={selectedValue}
                        onSelect={handleDestinationSelect}
                    />
                </div>

                <div className="flex-1">
                    {!mapApiKey && <div className="bg-gray-100 z-[2] absolute w-full h-full rounded-lg"></div>}
                    {mapApiKey && <Suspense fallback={null}>
                        <Map
                            mapApiKey={mapApiKey}
                            showMapBorder={showMapBorder}
                            cards={updatedDestinations}
                            selectedValue={selectedValue}
                            onSelect={handleDestinationSelect}
                        />
                    </Suspense>}
                </div>
            </div>
        </section>
    );
};

export default DestinationsAndMapComp;

