/* eslint-disable max-len */
import { Loader } from "@googlemaps/js-api-loader";
import React, {
    Suspense,
    useEffect,
    useLayoutEffect,
    useRef,
    useState,
} from "react";
import { getLanguage } from "src/main/util/getLanguage";
import {
    createGoogleHTMLMapMarker,
    getGoogleRegion,
} from "src/main/util/map/googleMapsHelper";
import { getImage } from 'src/main/util/getImage';
import "./Map.css";

import { fetchSvgFromXHR, fetchColorsFile } from 'src/main/util/SvgFromXHR';

// import googleStyle from "./data/googleStyle.json";

import SaudiGeometry from "./data/layer.geo.json";

import KSA_Boundaries from "./data/KSA_Boundaries.geo.json";

import KSA_kuwait_Boundaries from './data/KSA_kuwait_Boundaries.geo.json';

//import styles from "../DestinationsAndMap.css";
// LazyLoading components

const lang = getLanguage();
// const region = getGoogleRegion();
const region = "SA";

function getComputedCSSVariable(variableName: string) {
    return getComputedStyle(document.documentElement).getPropertyValue(variableName);
}
interface MapProps {
    mapApiKey: string;
    showMapBorder?: boolean;
    cards?: any[] | null;
    selectedValue?: number | null;
    onSelect?: (selectedValueId: number) => void;
    styles?: string;
    radius?: string;
    markerImage?: string;
    latitude?: string;
    longitude?: string;
    draggable?: boolean;
}

const Map = (props: MapProps) => {
    const {
        mapApiKey,
        showMapBorder,
        cards,
        onSelect,
        selectedValue,
        styles,
        radius,
        markerImage,
        latitude,
        longitude,
        draggable
    } = props;

    const mapFrame = useRef<HTMLDivElement>(null);
    const [markers, setMarkers] = useState<Array<
        ReturnType<typeof createGoogleHTMLMapMarker>
    > | null>(null);

    // set active classes on map markers
    useEffect(() => {
        markers?.forEach((marker, index) => {
            marker.div?.classList?.toggle("is-active", false);

            const thisid = marker.div?.children[0].id;
            if (thisid == selectedValue) {
                marker.div?.classList?.toggle("is-active", true);
            }
        });
    }, [selectedValue]);


    // get colors file mapping to color the tags
    let tagsColorMapping: any = [];
    useEffect(() => {
        if (markerImage === 'svg') {
            const fetchColors = async () => {
                if (!tagsColorMapping.length) {
                    const { colorsMapping, success } = await fetchColorsFile();
                    tagsColorMapping = colorsMapping;
                }
            };

            fetchColors();
        }
    }, []);
    const getTagColorFromMapping = (category: { id: string, icon: string, title: string }) => {
        if (tagsColorMapping.length) {
            const mapping = tagsColorMapping.filter((tagColor: any) => tagColor.id === category.id);
            return mapping.length > 0 ? mapping[0].color : '';
        }
    }

    useEffect(() => {



        const initMap = async () => {
            const loader = new Loader({
                apiKey: mapApiKey,
                version: "weekly",
                libraries: ["places"],
                language: lang,
                region: region,
            });

            const latLngBounds = {
                north: 32.4656,
                south: 16.1479,
                west: 32.4506,
                east: 57.1633
            };


            const mapStyles = [
                {
                    elementType: "labels",
                    stylers: [{ visibility: "off" }],
                },
                {
                    featureType: "water",
                    elementType: "labels",
                    stylers: [{ visibility: "on" }],
                },
            ]

            loader.load().then((google) => {
                const saudiLangLat = new google.maps.LatLng(
                    Number(24.774265),
                    Number(43.738586)
                );
                const bounds = new google.maps.LatLngBounds();
                //bounds.extend(saudiLangLat);

                const map = new google.maps.Map(mapFrame.current as HTMLDivElement, {
                    fullscreenControl: false,
                    disableDoubleClickZoom: true,
                    zoom: 6,
                    center: {
                        lat: Number(latitude) || 24.774265,
                        lng: Number(longitude) || 43.738586,
                    },
                    zoomControl: false,
                    draggable: draggable,
                    streetViewControl: false,
                    scrollwheel: false,
                    styles: mapStyles,

                });


                //map.data.addGeoJson(googleStyle);
                map.data.addGeoJson(SaudiGeometry);
                if (showMapBorder) {
                    map.data.addGeoJson(KSA_Boundaries);
                    map.data.addGeoJson(KSA_kuwait_Boundaries);
                }



                map.data.setStyle((feature) => {
                    switch (feature.getProperty('featureSelector')) {
                        case 'KSA_Boundaries':
                            return {
                                strokeWeight: 2,
                                strokeColor: 'rgba(120,0,110,.4)',
                                zIndex: 3,
                            };
                        case 'KSA_kuwait_Boundaries':
                            return {
                                strokeWeight: 2,
                                strokeColor: 'rgba(75, 75, 75,1)',
                                zIndex: 2,
                            };
                        case 'KSAGeometry':
                            return {
                                fillColor: "white",
                                strokeWeight: 0,
                                fillOpacity: 0.5,
                                zIndex: 1,
                            };
                        default:
                            return {
                                fillColor: "green",
                                fillOpacity: 1,
                            };
                    }
                });

                let mapMarkersPromises: Promise<any>[] = []; // Declare mapMarkersPromises array outside the if (cards) block
                if (cards?.length) {
                    mapMarkersPromises = cards.map(async (
                        {
                            latitude,
                            longitude,
                            title,
                            bannerImage,
                            categories,
                            id,
                            card_id,
                        },
                        index
                    ) => {
                        const loc = new google.maps.LatLng(
                            Number(latitude),
                            Number(longitude)
                        );
                        bounds.extend(loc);

                        let toolTipCategoryHtml = "";
                        categories.forEach((element: any, index: number) => {
                            toolTipCategoryHtml += `<span>${element.title}</span>`;
                            if (index < categories.length - 1) {
                                toolTipCategoryHtml += ", ";
                            }
                        });

                        let markerPoint = "";
                        if (markerImage === "svg" && categories.length > 0) {
                            const { success, svgText } = await fetchSvgFromXHR(categories[0].icon);

                            const tagColor = getTagColorFromMapping(categories[0]);
                            markerPoint = `<div class="mapSvgSelector" style='background-color:${tagColor}'>${svgText}</div>`;
                        } else {
                            if (bannerImage) {
                                markerPoint = `<img src="${getImage(bannerImage)}" class="border-2 border-theme-100" />`;
                            } else {
                                markerPoint = `<div class='bg-slate-400 border-2 border-theme-100 h-8 w-8 rounded-full'></div>`;
                            }
                        }

                        const marker = createGoogleHTMLMapMarker({
                            latlng: loc,
                            map: map,
                            index: index,
                            id: id,
                            markerClass: "google-marker",
                            activeClass: `${selectedValue == card_id ? "is-active" : ""}`,
                            html: `
                        <div class="marker-wrapper" id="${card_id}">
                            <div class="marker-tooltop animate-[bounce_1.5s_ease-in-out_infinite] max-w-[125px] text-center">
                                <strong>${title}</strong>
                                <span>${toolTipCategoryHtml}</span>
                            </div>
                        ${markerPoint}
                      </div>`,
                        });

                        marker.addListener("click", () => {
                            markers?.map(async (marker) => {
                                (marker).div.classList.toggle("is-active", false);
                            });

                            marker.div.classList.toggle("is-active", true);
                            if (onSelect) {
                                onSelect(card_id);
                            }
                        });

                        return marker;
                    });

                    // Move the Promise.all call outside the cards.map function
                    Promise.all(mapMarkersPromises).then((mapMarkers) => {
                        setMarkers(mapMarkers);

                        map.panToBounds(bounds);
                        map.fitBounds(bounds);

                    }).catch((error) => {
                        console.log(error);
                    });
                } else {
                    if (latitude && longitude) {
                        const loc = new google.maps.LatLng(
                            Number(latitude),
                            Number(longitude)
                        );
                        const marker = createGoogleHTMLMapMarker({
                            latlng: loc,
                            map: map,
                            markerClass: "google-marker-2",
                            html: `
                            <div class="fill-themed cursor-default -mt-2">
                            <svg width="32" height="32" viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M16 2C13.0836 2.00331 10.2877 3.1633 8.22548 5.22548C6.1633 7.28766 5.00331 10.0836 5 13C5 22.4125 15 29.5212 15.4263 29.8188C15.5944 29.9365 15.7947 29.9997 16 29.9997C16.2053 29.9997 16.4056 29.9365 16.5737 29.8188C17 29.5212 27 22.4125 27 13C26.9967 10.0836 25.8367 7.28766 23.7745 5.22548C21.7123 3.1633 18.9164 2.00331 16 2ZM16 9C16.7911 9 17.5645 9.2346 18.2223 9.67412C18.8801 10.1136 19.3928 10.7384 19.6955 11.4693C19.9983 12.2002 20.0775 13.0044 19.9231 13.7804C19.7688 14.5563 19.3878 15.269 18.8284 15.8284C18.269 16.3878 17.5563 16.7688 16.7804 16.9231C16.0044 17.0775 15.2002 16.9983 14.4693 16.6955C13.7384 16.3928 13.1136 15.8801 12.6741 15.2223C12.2346 14.5645 12 13.7911 12 13C12 11.9391 12.4214 10.9217 13.1716 10.1716C13.9217 9.42143 14.9391 9 16 9Z" fill="#78006E"/>
                            </svg>
                            </div>
                            `
                        });
                        bounds.extend(loc);
                        map.setCenter(loc); // Center the map on the marker
                        map.setZoom(13);
                        return;
                    }
                }


                var allowedBounds = new google.maps.LatLngBounds(
                    new google.maps.LatLng(16.3477, 34.5325),// Southwest corner
                    new google.maps.LatLng(32.1646, 55.6666)// Northeast corner
                );
                var lastValidCenter = map.getCenter();

                google.maps.event.addListener(map, 'center_changed', function () {
                    const currentCenter = map.getCenter();
                    if (currentCenter && allowedBounds.contains(currentCenter)) {
                        // still within valid bounds, so save the last valid position
                        lastValidCenter = currentCenter;
                        return;
                    }

                    // not valid anymore => return to last valid position
                    if (lastValidCenter) {
                        map.panTo(lastValidCenter);
                    }
                });

            })
                .catch((error) => {
                    console.log(error);
                });
        };

        initMap();
    }, []);

    return (
        <div className={`map-wrapper h-full ${styles}`} id="map-wrapper">
            <div className="map">
                <div className="map-outer">
                    <div
                        className={`map-frame h-full ${radius || "md:rounded-2xl"} `}
                        ref={mapFrame}
                    ></div>
                </div>
            </div>
        </div>
    );
};

export default Map;

