/* eslint-disable max-len */
import React, { Suspense, useEffect, useLayoutEffect, useRef, useState } from 'react';
//import Map from '../CardsAndMap/Map';

import Map from 'src/main/components/common/atoms/Map/Map';

import MapCardsList from '../CardsAndMap/MapCardsList';
import Text from 'src/main/components/common/atoms/Text/Text';
import Dropdown from 'src/main/components/common/atoms/Dropdown/Dropdown';
import { getLanguage } from "src/main/util/getLanguage";
import CategoriesTagsInline from "src/main/components/common/organisms/CategoriesTagsInline/CategoriesTagsInline";

import { AttractionsAndMapProps } from './IAttractionsAndMap';

const AttractionsAndMapComp = (props: AttractionsAndMapProps) => {
    const { enableManualAuthoring, noResultsFoundLabel, mapApiKey, showMapBorder, title, subTitle, link, categoryFilter, destinationFilter, allAttractionsFilter, destinationList, categoryList } = props;

    const [isApisLoadedSuccessfully, setIsApisLoadedSuccessfully] = useState(true);

    const [selectedValue, setSelectedValue] = useState(1);

    const [isLoading, setIsLoading] = useState(false);
    const [categories, setCategories] = useState<Array<any> | null>(null);
    const [destinations, setDestinations] = useState<Array<any> | null>(null);
    const [attractions, setAttractions] = useState(null);


    const [selectedCategory, setSelectedCategory] = useState<number | string | undefined>(1);
    const [selectedDestination, setSelectedDestination] = useState<number>();

    const [updateCategoriesFromApi, setUpdateCategoriesFromApi] = useState<boolean>(true);


    const fetchData = async (url: string) => {
        try {
            setIsLoading(true);
            const response = await fetch(url);
            const responseJson = await response.json();
            const apiData = responseJson.response;
            setIsLoading(false);
            return apiData;
        } catch (err) {
            setIsLoading(false);
        }
    };

    const addDefaultsToDropDowns = (list: any, label: string) => {
        list.unshift({
            'title': label,
        });

        //inject id, needed for dropdown
        list.forEach((item: any, index: number) => {
            item.id = index + 1;
        });
        return list;
    };
    const addDefaultsToCats = (list: any, label: string) => {
        list.unshift({
            'title': label,
            'id': 1,
            'icon': '/content/dam/sauditourism/tag-icons/categories/default.svg'
        });
        return list;
    };


    const getCategories = async () => {
        //don't show the categories if there is only one category
        if (categoryList && categoryList.length > 1) {
            setCategories(addDefaultsToCats(sortArrayAlphabatically(categoryList), categoryFilter.label));
        }
    };
    const getDestinations = async () => {
        //don't show the dropdown if there is only one category
        if (destinationList?.length && destinationList?.length > 1) {
            setDestinations(addDefaultsToDropDowns(sortArrayAlphabatically(destinationList), destinationFilter.label));
        }
    };
    const sortArrayAlphabatically = (arr: any) => {
        if (arr && arr.length != 0) {
            return arr?.sort((a: any, b: any) => {
                const nameA = a?.title?.toUpperCase();
                const nameB = b?.title?.toUpperCase();

                if (nameA < nameB) {
                    return -1;
                }
                if (nameA > nameB) {
                    return 1;
                }

                // Names are equal, compare by other criteria if needed
                return 0;
            });
        }

    }
    const [resetKey, setResetKey]: any = useState(0);
    const resetComponent = () => {
        setResetKey((prevKey: number) => prevKey + 1);
    };

    const getAttractions = async (query: string) => {
        const apiData = await fetchData(`${allAttractionsFilter.apiUrl}?${query}`);
        //inject id, needed for map component
        if (apiData?.data?.length > 0) {

            const apiDataUpdated = apiData.data.map((attraction: any, index: number) => {
                return {
                    ...attraction,
                    card_id: index + 1,
                };
            });
            setAttractions(null);
            setAttractions(apiDataUpdated);

        } else {
            setAttractions(null);
        }

        if (updateCategoriesFromApi && apiData?.categoryList?.length > 1) {
            setCategories(addDefaultsToCats(sortArrayAlphabatically(apiData.categoryList), categoryFilter.label));
        }
        if (apiData.destinationList?.length > 1) {
            setDestinations(addDefaultsToDropDowns(sortArrayAlphabatically(apiData.destinationList), destinationFilter.label));
        }

        setUpdateCategoriesFromApi(true);
        resetComponent();
    };


    const handleDestinationSelect = (selectedValueId: number) => {
        setSelectedValue(selectedValueId);
    };

    const handleCatsChange = (x: number | string) => {
        setSelectedCategory(x);
        setUpdateCategoriesFromApi(false);//limitation in the api: don't update the categories if the category it self has changed
    };

    const handleDestChange = (x: number) => {
        setSelectedCategory(1);//default category to all categories
        setSelectedDestination(x);

        document.getElementById("inlineCategoriesDiv")?.scrollTo({
            left: 0,
            behavior: 'smooth'
        })

    };

    useEffect(() => {
        getCategories();
        getDestinations();
        //getAttractions();
    }, []);




    useLayoutEffect(() => {
        const selectedCatPath = categories?.find((obj) => obj.id === selectedCategory)?.id || null;
        const selectedDestPath = destinations?.find((obj) => obj.id === selectedDestination)?.path || null;

        let queryString = `locale=${getLanguage()}&limit=100&offset=0`;

        // Include categoryList and destinationList if no specific category or destination is selected



        const destinationPaths = destinationList && destinationList.map(dest => dest.path && dest.path);
        const categoryPaths = categoryList && categoryList.map(category => category.id && category.id);

        const params = new URLSearchParams();

        if (categoryPaths) {
            if (selectedCatPath && selectedCatPath != 1) {
                params.append('tag', selectedCatPath);
            } else {
                params.append('tag', categoryPaths.join(','));
            }
        }

        if (selectedDestPath) {
            params.append('destination', selectedDestPath);
        } else {
            destinationPaths && params.append('destination', destinationPaths.join(',').replace(/^,/, ''));
        }

        queryString += `&${params.toString()}`;


        getAttractions(queryString);
        setTimeout(() => {
            setSelectedValue(1)
        }, 300);
    }, [selectedCategory, selectedDestination]);


    const [layout2Cols, setIslayout2Cols] = useState(false);
    useEffect(() => {
        const selector = document.querySelector('.AttractionsAndMapComp');
        const layout2Cols = selector?.closest(".layout2Cols");
        if (layout2Cols) setIslayout2Cols(true);
    }, []);

    return (
        <>
            {isApisLoadedSuccessfully && <>
                {isLoading && <div className="absolute h-full top-0 left-0 right-0 bg-white/30 z-[11] md:mx-5 lg:mx-100 flex items-center justify-center">
                    <span className="relative flex h-10 w-10 opacity-75">
                        <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-theme-100 opacity-75"></span>
                        <span className="relative inline-flex rounded-full h-10 w-10 bg-theme-200"></span>
                    </span>
                </div>}

                <div className='mb-4 md:mb-6 px-5 md:px-0'>
                    <div className="flex flex-col md:flex-row md:justify-between md:items-center ">
                        <Text styles="!mb-2 md:!mb-4" text={title} isTitle />
                        {link && link.url && link.text && <a href={link.url} target={link.targetInNewWindow ? '_blank' : ''} className='font-primary-bold text-theme-100 hover:underline mb-2 md:mb-0'>{link.text}</a>}
                    </div>


                    {subTitle && <div
                        className="richTextContainer font-primary-semibold text-lg md:text-[22px] leading-[22px] md:leading-[27px]"
                        dangerouslySetInnerHTML={{
                            __html: subTitle,
                        }}
                    ></div>}
                </div>

                {categories &&
                    <div id="inlineCategoriesDiv" className='flex px-5 md:px-0 mb-6 overflow-hidden overflow-x-scroll scrollBarHidden'>
                        <CategoriesTagsInline selected={selectedCategory} onSelect={handleCatsChange} tags={categories} wrapperClass={'flex-nowrap'} />
                        {/* give 20px space on mobile to space the tags from the screen border */}
                        <div className="w-5 md:w-0"></div>
                    </div>
                }

                <div className={`flex flex-col md:flex-row md:gap-6 h-[550px] ${layout2Cols ? 'md:h-[450px]' : 'md:h-[672px]'} overflow-hidden relative`}>
                    <div className='w-full md:w-[49%] px-5 md:px-0'>
                        {destinations && <div className={`w-full ${layout2Cols ? 'mb-5' : 'mb-6'} ${!attractions && !isLoading && '!mb-12'}`}>
                            <Dropdown options={destinations} selectedOption={1} onChange={handleDestChange} className='text-base' placeHolder={destinationFilter.label ? destinationFilter.label : getLanguage() == 'ar' ? 'جميع الوجهات' : 'All Destinations'} />
                            {/* 
                            <Suspense fallback={null}>
                                {categories && <Dropdown selectedOption={selectedCategory} options={categories} onChange={handleCatsChange} className='text-base' placeHolder={categoryFilter.label ? categoryFilter.label : getLanguage() == 'ar' ? 'جميع التصنيفات' : 'All Categories'} />}
                            </Suspense> */}
                        </div>}
                        {attractions && <MapCardsList wrapperClass={`${!destinations ? 'md:!h-full' : 'md:!h-[calc(100% - 65px)]'}`} cardType="attraction" cards={attractions} selectedValue={selectedValue} onSelect={handleDestinationSelect} />}
                    </div>

                    {/* the check for !destinations && !attractions && !isLoading is to align the 'no cards' message on mobile */}
                    <div className={`${!destinations && !attractions && !isLoading ? 'mt-6 md:mt-0' : ''} flex-1`}>
                        {!mapApiKey && <div className="bg-gray-100 z-[2] absolute w-full h-full rounded-lg"></div>}
                        {mapApiKey && <Suspense fallback={null}>
                            {<Map
                                key={resetKey}
                                mapApiKey={mapApiKey}
                                showMapBorder={showMapBorder}
                                cards={attractions}
                                selectedValue={selectedValue}
                                onSelect={handleDestinationSelect}
                                markerImage={"svg"} />
                            }
                        </Suspense>}
                    </div>

                    {!attractions && !isLoading && <div className={`absolute ${!destinations ? 'top-[-4px]' : 'top-[10%]'} md:top-[15%] w-full px-5 md:px-0`}>
                        <div className='text-sm md:text-base font-primary-regular text-gray-350'>{noResultsFoundLabel ? noResultsFoundLabel : 'There are no items under this category'}</div>
                    </div>}
                </div>

            </>}
        </>
    );
};

export default AttractionsAndMapComp;
