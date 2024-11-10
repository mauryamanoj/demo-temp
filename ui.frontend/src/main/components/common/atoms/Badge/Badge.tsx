import React, { useEffect, useRef, useState } from 'react';
import { useResize } from 'src/main/util/hooks/useResize';
import { Checkbox } from '../Checkbox/Checkbox';
import Icon from '../Icon/Icon';

interface BadgeProps {
  filters: any,
  type: string,
  selectedValues: string[],
  onSelect: (categories: string[]) => void;
}
export const Badge: React.FC<BadgeProps> = (props) => {
  const {
    filters,
    type,
    selectedValues,
    onSelect
  } = props;

  const { isMobile } = useResize();

  const basePath = process.env.NODE_ENV === 'development' ? 'https://qa-revamp.visitsaudi.com' : window.location.origin;

  const [localFilter, setLocalFilter] = useState(filters);
  const [values, setValues] = useState<string[]>(selectedValues);

  const handleChange = (e: any, code: string) => {

    if (!values.includes(code)) {
      setValues((prevValues) => [...prevValues, code]);
    } else {
      setValues((prevValues) => prevValues.filter((value) => value !== code));
    }
  };

  useEffect(() => {
    setValues(selectedValues);
  }, [selectedValues]);

  useEffect(() => {
    onSelect(values);
  }, [values]);

  return (
    <div className={`${isMobile ? 'mx-[20px]' : ''} flex flex-wrap`}>
      {localFilter && localFilter?.sort((a: any, b: any) => a.value.localeCompare(b.value))?.map((item: any, i: any) => (
        <div
          // ${values.includes(item.code) ? '!bg-theme-100 !text-white !border-white' : ''}
          key={i}
          className={`${values?.length && values.includes(item.code) && '!bg-theme-100 !text-white !border-white white-svg'} flex h-[37px] fill-themed-200 flex-shrink-0 ltr:mr-4 rtl:ml-4 mb-2 bg-white px-4 py-[4px] text-[#4B4B4B] text-sm rounded-[20px] border border-[#AAAAAA]`}
        >
          <Checkbox
            key={i}
            labelStyle={'flex items-center cursor-pointer text-sm font-primary-semibold inline-block hover:cursor-pointer pt-[1px]'}
            item={item}
            name={item?.code}
            handleChange={handleChange}
            style={`themed relative float-left ltr:mr-[6px]  rtl:ml-[6px] mt-[0.15rem] h-[1rem] w-[1rem] hidden`}
          >
            {type === 'categorie' ? (
              <Icon name={basePath + '' + item.icon} isFetch wrapperClass="themed w-4 ltr:mr-1 rtl:ml-1 [&_svg]:w-4" />
            ) : (
              <Icon wrapperClass="themed w-4 ltr:mr-1 rtl:ml-1" name="weather" />
            )}
          </Checkbox>
        </div>
      ))}
    </div>
  );
};