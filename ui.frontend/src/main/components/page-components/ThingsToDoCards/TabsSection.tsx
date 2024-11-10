/* eslint-disable max-len */
import React, { useEffect, useState } from "react";

import Text from "../../common/atoms/Text/Text";
import Button from "../../common/atoms/Button/Button";
import Dropdown from "../../common/atoms/Dropdown/Dropdown";
import { useResize } from "src/main/util/hooks/useResize";
import Icon from "../../common/atoms/Icon/Icon";
import { IThingsTodoTabProps } from './IThingsToDoCard';
import { getLanguage } from "src/main/util/getLanguage";
import CategoriesTagsInline from "src/main/components/common/organisms/CategoriesTagsInline/CategoriesTagsInline";


interface TabsSectionProps {
    tabs: IThingsTodoTabProps[];
    onclick: (category: any) => void;
}

const TabsSection: React.FC<TabsSectionProps> = ({ tabs, onclick }) => {
    const lang = getLanguage();
    const { isMobile } = useResize();
    const [selectedCategory, setSelectedCategory] = useState<any>(0);

    const [updatedTabs, setUpdatedTabs] = useState<IThingsTodoTabProps[]>([]);

    // inject id
    useEffect(() => {
        if (tabs.slice(0, 6).length) {
            tabs.forEach((tab, index) => tab.id = index + 1);
            setUpdatedTabs(tabs);
            setSelectedCategory(1);//the default category, in this case -> All 
        }
    }, [])




    function handleCatsChange(optionID: any) {
        var selected = updatedTabs.filter((e: any) => e?.id == optionID);
        onclick(selected[0]);
        setSelectedCategory(optionID);
    }
    return (
        <div className="">
            {isMobile ? (
                <div id="inlineCategoriesDiv" className='flex -mx-5 px-5 overflow-hidden overflow-x-scroll scrollBarHidden'>
                    <CategoriesTagsInline selected={selectedCategory} onSelect={handleCatsChange} tags={updatedTabs} wrapperClass={'flex-nowrap'} />
                </div>
            ) : (
                <div className="flex justify-center">
                    {updatedTabs.map((tab: any, index: number) => (
                        <div key={index} className=" flex flex-col justify-center items-center py-4 pb-10 cursor-pointer w-52"
                            onClick={() => { handleCatsChange(tab.id) }}>
                            <Icon name={tab.icon} isFetch wrapperClass={`w-[88px] [&_svg]:w-full [&_svg]:h-[88px] ${selectedCategory === tab.id ? "fill-themed-200" : "fill-gray-350"}`} />



                            <Text text={tab.title} styles={`text-base text-center py-4 truncate w-full 
                                    ${selectedCategory === tab.id ? "font-primary-bold text-theme-200" : "font-primary-regular text-black "
                                }`}
                            />
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default TabsSection;

