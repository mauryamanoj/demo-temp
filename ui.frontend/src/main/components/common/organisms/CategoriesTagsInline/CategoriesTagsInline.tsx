import React, { useEffect } from "react";
import Icon from 'src/main/components/common/atoms/Icon/Icon';

type CategoriesTagsProps = {
  tags: {
    title: string;
    icon: string;
    id?: string | number;
  }[];
  wrapperClass?: string
  max?: number;
  onSelect?: (x: number | string) => void;
  selected?: string | number;
}

const CategoriesTagsInline: React.FC<CategoriesTagsProps> = ({ tags, wrapperClass, max, onSelect, selected }) => {

  useEffect(() => {
    console.log(selected)
  }, [selected]);

  return (
    <div className={`${wrapperClass} flex gap-2 flex-wrap items-center`}>
      {tags.slice(0, max).map((tag, index) => (
        <div key={index} onClick={() => onSelect && tag.id && onSelect(tag.id)}
          className={`flex flex-row whitespace-nowrap  h-[37px] px-4 py-2 rounded-3xl border 
                      border-gray-25 items-center gap-1 font-primary-semibold
                      ${selected && selected == tag.id && 'bg-theme-100'}
                      ${onSelect ? 'cursor-pointer' : 'cursor-default'} `}
        >
          <Icon name={tag.icon} isFetch wrapperClass={`${selected && selected == tag.id ? 'white-svg' : 'fill-themed-200'} w-4 [&_svg]:w-4`} />
          <span className={`${selected && selected == tag.id ? 'text-white' : 'text-gray-350'} leading-5`}>{tag.title}</span>
        </div>
      ))}

    </div>
  );
};

export default CategoriesTagsInline;
