/* eslint-disable max-len */
import React, { useEffect, useLayoutEffect, useRef, useState } from "react";
import { format, startOfWeek, addDays } from 'date-fns';
import Icon from "../../common/atoms/Icon/Icon";
import InfiniteScroll from 'react-infinite-scroll-component';
import Calendar from "react-calendar";
import SearchBox from "../../common/atoms/SearchBox/SearchBox";
import Text from "src/main/components/common/atoms/Text/Text";
import { getLanguage } from "src/main/util/getLanguage";
import { handleSearch, resetOffset, changeLimit } from "./Services/service";
import { Filters } from "../../common/atoms/Filters/Filters";
import { NoResult } from "../../common/atoms/NoResult/NoResult";
import { Badge } from "../../common/atoms/Badge/Badge";
import { DropdownFilters } from "../../common/atoms/DropdownFilters/DropdownFilters";
import Button from "../../common/atoms/Button/Button";
import "./Style/react-calendar.css";
import { FilterExplorerInterface } from "./Interface/index";
import { useResize } from "src/main/util/hooks/useResize";

const ThingsToDoCard = React.lazy(() => import('src/main/components/page-components/ThingsToDoCards/ThingsToDoCard'));
const MainCard = React.lazy(() => import('src/main/components/page-components/Cards/Main/Card/MainCard'));

interface Type {
  id?: string;
  label: string;
}

const getInitialQueryParams = (filter: string) => {
  const urlParams = new URLSearchParams(window.location.search);
  const queryParams: {
    [key: string]: string[] | null;
  } = {};

  urlParams.forEach((value, key) => {
    if (queryParams[key]) {
      queryParams[key]?.push(value);
    } else {
      queryParams[key] = [value];
    }
  });

  if (queryParams[filter]) {
    return queryParams[filter]!;
  }

  return [];
};


const FilterExplorerComp: React.FC<FilterExplorerInterface> = (props) => {
  const {
    filterType,
    title,
    searchPlaceholderLabel,
    noResultsFoundLabel,
    noResultFoundDescription,
    clearLabel,
    resetAllFiltersLabel,
    modalFiltersTitle,
    applyLabel,
    selectLocationLabel,
    searchLimit,
    clearAllButton,
    types,
    filtersLabel,
    apiUrl,
    loadingLabel,
    typeLabel,
    hideOrnament = false

  } = props;

  const startDateParam = "startDate";
  const endDateParam = "endDate";

  var typeParam = "type";
  var locationParam = "destinations";
  var categorieParam = "categories";
  var seasonsParam = "seasons";
  var sortByParam = "sortBy";

  const initialDate = () => {
    const currentDate = new Date();
    const nextSunday: any = startOfWeek(addDays(currentDate, 5), { weekStartsOn: 0 });
    return {
      startDate: '',
      endDate: ''
    }
  }




  const [resetKey, setResetKey]: any = useState(0);
  const resetComponent = () => {
    setResetKey((prevKey: number) => prevKey + 1);
  };

  const [selectedCategory, setSelectedCategory]: any = useState(getInitialQueryParams(categorieParam) || []);
  const [selectedSeasons, setSelectedSeasons]: any = useState(getInitialQueryParams(seasonsParam) || []);
  const [selectedContentType, setSelectedContentType]: any = useState(getInitialQueryParams(typeParam) || []);
  const [selectedLocations, setSelectedLocation]: any = useState(getInitialQueryParams(locationParam) || []);
  const [selectedSortBy, setSelectedSortBy]: any = useState(getInitialQueryParams(sortByParam) || []);

  const [calendarDate, setCalendarDate] = useState<any>();
  const [selectedDate, setSelectedDate] = useState(initialDate);

  const [result, setResult]: any = useState({});
  const [filters, setFilters]: any = useState({});
  const [cards, setCards] = useState<any[]>([]);
  const [showNo, setShowNo]: any = useState(false);
  const [loading, setLoading]: any = useState(true);
  const [hideLoader, setHideLoader]: any = useState(false);
  const [hasMore, setHasMore]: any = useState(true);
  const [resetSearchBox, setResetSearchBox] = useState(Date.now());
  const [resetCalender, setResetCalender] = useState(Date.now());
  const [showFilter, setShowFilter]: any = useState(false);

  const { isMobile } = useResize(1024);
  const [showCalender, setShowCalender] = useState<true>();
  const [searchLocationVal, setSearchLocationVal]: any = useState([]);

  const lang = getLanguage();

  const [updatedDestinations, setUpdatedDestinations] = useState<any>([]);
  // Sort regions and inject All
  useEffect(() => {
    if (filters?.destinations?.data.length) {
      const updatedDestinations = filters.destinations.data;
      updatedDestinations.sort((a: any, b: any) => a.value.localeCompare(b.value));
      updatedDestinations.unshift({ code: undefined, value: `${lang == 'ar' ? 'جميع الوجهات' : 'All Destinations'}` });
      setUpdatedDestinations(updatedDestinations);
    }
  }, [filters])


  function isLoaderHidden() {
    return (
      cards.length == 0 ||
      hideLoader ||
      (result && result?.pagination?.total == cards.length)
    );
  }


  const handleCategoryChange = (values: any) => {
    setSelectedCategory(values);
  }
  const handleSeasonsChange = (values: any) => {
    setSelectedSeasons(values);
  }
  const handleLocationChange = (values: any) => {
    setSelectedLocation(values);

  }

  function handleSrchLocationChange(event: any) {
    setSearchLocationVal(event.target.value);
  }
  function handleTypesChange(types: any) {
    setSelectedContentType(types);
    setSelectedSeasons([]); //Clear the selected seasons as they are dependent on the selected type
  }

  const triggerApiCall = () => {
    setCards([])
    handleApi(false);
    setShouldRunTheApi(isMobile ? false : true);
    getSelectedFilterNumber();
    setShowFilter(false);
    preventBodyScrolling(false);
  };
  function handleApplyButton() {
    triggerApiCall();
  }

  // on mobile triggering the api call happens when clicking on a buttton,
  // so shouldRunTheApi state is used to control when the api call should happen.
  const [shouldRunTheApi, setShouldRunTheApi] = useState(true);
  useEffect(() => {
    if (shouldRunTheApi) {
      triggerApiCall();
    }
  }, [selectedLocations, selectedCategory, selectedSeasons, selectedContentType, selectedDate]);


  useEffect(() => {
    if (isMobile) {
      setShowFilter(false);
    }
  }, []);


  function handleTypes() {
    if (types && types.length != 0) {
      for (let index = 0; index < types.length; index++) {
        const element: any = types[index];
        if (!('id' in element)) {
          element.checked = true;
          element.defaultChecked = true;
        } else {
          element.checked = false;

        }
      }
    }
  }

  function loadmore() {
    if (hasMore && !loading) {
      setTimeout(async () => {
        await handleApi(true);
      }, 600);
    }
  }


  function clearAll() {
    resetComponent();
    setSelectedCategory([]);
    setSelectedLocation([]);
    setSelectedSeasons([]);
    setSelectedContentType([]);
    setSelectedDate(initialDate);
    setCalendarDate(null);
    setResetCalender(Date.now());
    setResetSearchBox(Date.now());
    setSearchLocationVal("");

    setShowFilter(false);
    preventBodyScrolling(false);

    setShouldRunTheApi(true);
  }

  function handleLocations(locations: any) {
    locationParam = locations?.key;
    if (locations && locations?.data.length != 0) {
      for (let index = 0; index < locations?.data.length; index++) {
        const element = locations?.data[index];
        element.code = element.id;
      }
    }
  }

  function handleCategories(categories: any) {
    categorieParam = categories?.key;
    if (categories && categories?.data.length != 0) {
      for (let index = 0; index < categories?.data.length; index++) {
        const element = categories?.data[index];
        element.code = element.id;
      }
    }
  }

  function handleSeasons(seasons: any) {
    seasonsParam = seasons?.key;
    if (seasons && seasons?.data.length != 0) {
      for (let index = 0; index < seasons?.data.length; index++) {
        const element = seasons?.data[index];
        element.code = element.id;
      }
    }
  }

  function arrayToQueryString(array: string[], paramName: string) {
    return array.map(value => `${encodeURIComponent(paramName)}=${encodeURIComponent(value)}`).join('&');
  }

  async function handleApi(isLoadmore: boolean) {
    setLoading(true);
    setHideLoader(false);
    if (searchLimit) {
      changeLimit(searchLimit);
    }




    const itemToRemove = 'All Things To Do';
    const contentTypes = selectedContentType?.length && selectedContentType.filter((item: any) => item !== itemToRemove);
    var str1 = contentTypes && contentTypes.length !== 0 ? `&${arrayToQueryString(contentTypes, typeParam)}` : "";

    var str2 = selectedLocations && selectedLocations.length !== 0 ? `&${arrayToQueryString(selectedLocations, locationParam)}` : "";
    var str3 = selectedSortBy?.length ? `&${arrayToQueryString(selectedSortBy, sortByParam)}` : "";
    var str4 = "";
    if (selectedDate.startDate) {
      str4 = startDateParam ? `&${startDateParam}=${selectedDate.startDate}` : "";
    }
    if (selectedDate.endDate) {
      str4 += endDateParam ? `&${endDateParam}=${selectedDate.endDate}` : "";

    }
    var str5 = selectedCategory?.length ? `&${arrayToQueryString(selectedCategory, categorieParam)}` : "";
    var str6 = selectedSeasons?.length ? `&${arrayToQueryString(selectedSeasons, seasonsParam)}` : "";

    const allFilterStr = str1 + str2 + str3 + str4 + str5 + str6;

    setInitialQueryParams(allFilterStr)
    await handleSearch(
      apiUrl,
      lang,
      isLoadmore,
      "",
      allFilterStr
    ).then((data: any) => {
      handleLocations(data?.filters?.destinations);
      handleCategories(data?.filters?.categories);
      handleSeasons(data?.filters?.seasons);
      setShowCalender(data?.filters?.date);
      setResult(data?.data);
      setFilters(data?.filters);

      if (data.data && data.data.length == 0) {
        setLoading(false);
        resetOffset();
        setHasMore(false);
        setShowNo(true);

      } else {
        if (isLoadmore) {
        setCards(prevCards => [...prevCards, ...data.data]);
      } else {
        setCards(data.data)
      }

        setShowNo(false);
        setHasMore(true);
        setLoading(false);

      }

    });
  }
  const [totalFitlersCounter, setTotalFitlersCounter] = useState(0);
  function getSelectedFilterNumber() {
    setTotalFitlersCounter(selectedLocations.length + selectedCategory.length + selectedSeasons.length + selectedContentType.length);
  }

  const setInitialQueryParams = (allfilterstring: string) => {
    const currentURL = new URL(window.location.href);
    currentURL.search = '';
    currentURL.search += allfilterstring;
    window.history.replaceState(null, '', currentURL.toString());
  };

  function showNoResult() {
    const noresult = (
      <div className="mt-20">
        <NoResult
          title={noResultsFoundLabel ? noResultsFoundLabel : ""}
          description={noResultFoundDescription ? noResultFoundDescription : ""}
        />
      </div>
    );

    if (showNo) {
      return noresult;
    }
  }

  // todo:enhance this function
  function handleDefaultType() {
    if (types && types.length) {
      var typesArr = types.filter((e: any) => !e.defaultChecked);
      if (typesArr.length == selectedContentType.length || selectedContentType.length == 0) {
        for (let index = 0; index < types.length; index++) {
          const element = types[index];
          if (element.defaultChecked) {
            const ele: any = document.querySelector(`input[type="checkbox"][name="${element.label}"]`);
            ele.checked = true;
          }
        }
      }
      if (selectedContentType.length > 0 && typesArr.length != selectedContentType.length) {
        for (let index = 0; index < types.length; index++) {
          const element = types[index];
          if (element.defaultChecked) {
            const ele: any = document.querySelector(`input[type="checkbox"][name="${element.label}"]`);
            ele.checked = false;
          }
        }
      }


    }
  }

  function hideFilter() {
    if (!isMobile) {
      if ((selectedContentType.length == 0 && selectedLocations.length == 0) && cards && cards.length == 0) {
        return true; // hide
      }
      if ((selectedContentType.length != 0 || selectedLocations.length != 0) && cards && cards.length == 0) {
        return false; // show
      }
    }
  }

  function showMobileFilter() {
    setShowFilter(true);
    preventBodyScrolling(true);
  }

  const preventBodyScrolling = (show: boolean) => {
    const htmlElement = document.getElementsByTagName('html')[0];
    const bodyElement = document.body;
    if (show) {
      htmlElement.style.overflow = "hidden";
      bodyElement.style.overflow = "hidden";
      htmlElement.style.height = "100%";
      bodyElement.style.height = "100%";
    } else {
      htmlElement.style.overflow = "";
      bodyElement.style.overflow = "";
      htmlElement.style.height = "";
      bodyElement.style.height = "";
    }
  }

  function loadingIcon() {
    return <svg aria-hidden="true" className="inline w-8 h-8 text-gray-200 animate-spin dark:text-gray-600 fill-theme-100" viewBox="0 0 100 101" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path d="M100 50.5908C100 78.2051 77.6142 100.591 50 100.591C22.3858 100.591 0 78.2051 0 50.5908C0 22.9766 22.3858 0.59082 50 0.59082C77.6142 0.59082 100 22.9766 100 50.5908ZM9.08144 50.5908C9.08144 73.1895 27.4013 91.5094 50 91.5094C72.5987 91.5094 90.9186 73.1895 90.9186 50.5908C90.9186 27.9921 72.5987 9.67226 50 9.67226C27.4013 9.67226 9.08144 27.9921 9.08144 50.5908Z" fill="currentColor" />
      <path d="M93.9676 39.0409C96.393 38.4038 97.8624 35.9116 97.0079 33.5539C95.2932 28.8227 92.871 24.3692 89.8167 20.348C85.8452 15.1192 80.8826 10.7238 75.2124 7.41289C69.5422 4.10194 63.2754 1.94025 56.7698 1.05124C51.7666 0.367541 46.6976 0.446843 41.7345 1.27873C39.2613 1.69328 37.813 4.19778 38.4501 6.62326C39.0873 9.04874 41.5694 10.4717 44.0505 10.1071C47.8511 9.54855 51.7191 9.52689 55.5402 10.0491C60.8642 10.7766 65.9928 12.5457 70.6331 15.2552C75.2735 17.9648 79.3347 21.5619 82.5849 25.841C84.9175 28.9121 86.7997 32.2913 88.1811 35.8758C89.083 38.2158 91.5421 39.6781 93.9676 39.0409Z" fill="currentFill" />
    </svg>;
  }


  useEffect(() => {
    if (calendarDate) {
      setSelectedDate({
        startDate: calendarDate[0] ? format(new Date(calendarDate[0]), 'yyyy-MM-dd') : '',
        endDate: calendarDate[1] ? format(new Date(calendarDate[1]), 'yyyy-MM-dd') : ''
      })
      // if (!isMobile) {
      //   setCards([]);
      //   handleApi(false);
      // }
    }
  }, [calendarDate]);




  useEffect(() => {
    // Select the event cards element
    if (selectedContentType && selectedContentType == 0) {
      handleTypes();
    }
    const breadcrumbComponents = document.querySelector('#breadcrumb-container');


    if (breadcrumbComponents) {
      // Add classes to adjust the styling of breadcrumb Components
      breadcrumbComponents.classList.add(
        'lg:!mt-[114px]',
        '!mt-[84px]',
        '!pb-6',
      );

    }
  }, [cards]);

  const showClearAllButton = () => {
    const selectedValues = (selectedLocations.length || selectedCategory.length || selectedSeasons.length || selectedContentType.length || calendarDate) ? true : false;
    return clearAllButton && selectedValues && !isMobile;
  }
  return (
    <>
      {/* start filter button on mobile */}
      {filtersLabel && isMobile ? <Button
        onclick={showMobileFilter}
        title={`${filtersLabel} ${totalFitlersCounter > 0 ? '(' + totalFitlersCounter + ')' : ''}`}
        arrows={false}
        spanStyle="!px-0 relative top-[1px]"
        styles="block mb-5 gap-2 bg-transparent fill-themed !text-theme-100 w-full h-[44px] ltr:ml-0 rtl:mr-0 w-full flex justify-center p-2 !text-base font-primary-semibold rounded-lg"
      >
        <Icon name="filter-v1" />
      </Button> : null}
      {/* end filter button on mobile */}

      <div className="flex justify-between items-center">
        {title && <Text text={title} styles="text-5.5xl lg:text-8xl font-secondary-bold mb-12 lg:p-0 pt-10 px-[20px] text-[#000] lg:block hidden leading-none" type="span" />}

        {showClearAllButton() &&
          <span onClick={() => clearAll()}>
            <Text
              text={clearAllButton}
              styles="cursor-pointer pt-[14px] text-base  font-primary-semibold text-theme-100 lg:mt-10 mt-4"
              type="a"
            />
          </span>
        }
      </div>


      <div className="flex gap-9">
        {<div className={`${showFilter ? 'fixed overflow-scroll max-w-full w-full h-full left-0 top-0 bg-[#F8F8F8] z-50 lg:pb-0 pb-[100px]' : 'lg:block hidden'} ${hideFilter() ? 'hidden' : ''} w-full lg:max-w-[27.15vw] lg:mb-20 mb-0`}>

          {isMobile ? <div className={`flex justify-between mb-4`}>
            {modalFiltersTitle ? <>
              <Text text={modalFiltersTitle}
                styles="text-3xl lg:p-0 pt-10 px-[20px] font-primary-bold text-[#000]" type="span" />
            </> : null}

            {isMobile ? <span onClick={() => {
              setShowFilter(false);
              preventBodyScrolling(false);
            }} className="relative top-[38px] ltr:mr-4 rtl:ml-4 h-[32px]">
              <Icon name="close32x32" />
            </span> : null}
          </div> : null}

          {types &&
            <DropdownFilters
              modalFiltersTitle={modalFiltersTitle}
              selectLocationLabel={selectLocationLabel}
              applyLabel={applyLabel}
              filters={types?.sort()}
              clearLabel={clearLabel}
              label={typeLabel}
              selectedValues={selectedContentType}
              onSelect={handleTypesChange}
              isOpened={false}
              hideOrnament={hideOrnament}
            />
          }

          {/* mobile tags */}
          {isMobile && filterType == 'thingsToDo' && filters?.categories?.data && filters?.categories && filters?.categories?.data.length != 0 ?
            <div className={`bg-white lg:px-4 lg:py-6 p-4 mb-4 rounded-2xl ${isMobile ? 'mx-[20px]' : ''}`}>
              <Text text={filters?.categories?.name}
                styles="lg:text-sm text-xs uppercase font-primary-bold text-[#000] mb-2" type="h3" />

              <Badge
                filters={filters?.categories?.data.sort()}
                onSelect={handleCategoryChange}
                type={'categorie'}
                selectedValues={selectedCategory}
              />
            </div> : null}
          {/* mobile tags */}

          <div
            className={`w-full`}>
            {/* start location Filter */}
            {updatedDestinations.length ?
              <Filters
                key={resetKey}
                clearAllButton={clearAllButton}
                applyLabel={applyLabel}
                filters={filters?.destinations?.data.sort()}
                handelClearAllFilter={clearAll}
                handleApply={handleApplyButton}
                clearAllFiltersLabel={resetAllFiltersLabel}
                clearLabel={clearLabel}
                label={filters?.destinations?.name}
                onSelect={handleLocationChange}
                isMobile={isMobile}
                searchLocationVal={searchLocationVal}
                isExpanded={false}
                selectedValues={selectedLocations}
                type={'location'}
              >
                <SearchBox
                  className={'font-primary-regular text-base w-full py-2 px-10 border rounded-lg border-theme-100 outline-none'}
                  searchPlaceholderLabel={searchPlaceholderLabel}
                  onKeyUp={() => handleSrchLocationChange(event)}
                  key={resetSearchBox}
                  isMobile={isMobile}
                  smallIcon={true}
                  onKeyDown={() => { console.log(); }}
                  setIsInputFocused={() => { console.log(); }}
                />
              </Filters> : null}

            {/* end location Filter */}
          </div>



          {filterType == 'stories' && <div className={`w-full`}>
            {filters?.categories?.data.length ?
              <Filters
                clearAllButton={clearAllButton}
                applyLabel={applyLabel}
                filters={filters?.categories?.data.sort()}
                handelClearAllFilter={clearAll}
                handleApply={handleApplyButton}
                clearAllFiltersLabel={resetAllFiltersLabel}
                clearLabel={clearLabel}
                label={filters?.categories?.name}
                onSelect={handleCategoryChange}
                isMobile={isMobile}
                isExpanded={true}
                selectedValues={selectedCategory}
                type={'location'}
              >

              </Filters> : null}
          </div>}



          {/* start Seasons and Festivals variation */}
          {filters?.seasons && filters?.seasons?.data.length != 0 ? <div className={`bg-white lg:px-4 lg:py-6 p-4 mt-4 rounded-2xl ${isMobile ? 'mx-[20px]' : ''}`}>
            {filters?.seasons?.name ? <Text text={filters?.seasons?.name}
              styles="lg:text-sm text-xs uppercase font-primary-bold text-[#000] mb-6" type="h3" /> : null}
            <>
              {filters?.sessions?.data.length != 0 ?
                <div>
                  {filters?.categories && filters?.seasons?.data.length != 0 ?
                    <Badge
                      filters={filters?.seasons?.data}
                      onSelect={handleSeasonsChange}
                      selectedValues={selectedSeasons}
                      type={'seasons'}
                    /> : null}
                </div> : null}
            </>
          </div>
            : null}

          {/* end Seasons and Festivals variation */}

          {/* start calender */}
          {showCalender ? <Calendar
            calendarType="US"
            selectRange={true}
            navigationAriaLive={'polite'}
            onClickDay={(date) => {
              setCalendarDate([new Date(date)])
            }}
            className={`p-6 border-none mt-4 rounded-2xl ${isMobile ? 'mx-[20px]' : ''}`}
            onChange={setCalendarDate}
            tileDisabled={({ activeStartDate, date, view }) => view === 'month' && date.getTime() < new Date().setHours(0, 0, 0, 0)}
            defaultValue={null}
            defaultView="month"
            key={resetCalender}
            locale={lang}
            next2Label={null}
            prev2Label={null}
            nextLabel={
              <Icon
                name="chevron"
                wrapperClass="flex items-center justify-center h-full hover:bg-white"
                svgClass="w-[16px] h-[16px] rtl:scale-x-[-1]"
              />
            }
            prevLabel={
              <Icon
                name="chevron"
                wrapperClass="flex items-center justify-center h-full hover:bg-white"
                svgClass="-rotate-180 w-[16px] h-[16px] rtl:scale-x-[-1]"
              />
            } /> : null}

          {/* end calender */}




        </div>
        }
        {/* end filters */}

        {/* start cards */}

        <div className={`${hideFilter() ? 'w-full max-w-full' : 'w-full max-w-7xl'}`}>
          {filterType == 'thingsToDo' &&
            <div className="gap-2 hidden lg:block mb-7">
              {filters?.categories && filters?.categories?.data.length != 0 ?
                <Badge
                  filters={filters?.categories?.data.sort()}
                  //label={filters?.categories?.name}
                  onSelect={handleCategoryChange}
                  type={'categorie'}
                  selectedValues={selectedCategory}
                /> : null}
            </div>}
          <InfiniteScroll
            dataLength={cards?.length}
            next={loadmore}
            hasMore={hasMore}
            refreshFunction={loadmore}
            scrollThreshold={0.4}
            loader={<div role="status" style={{ display: isLoaderHidden() ? "none" : "block" }} className="text-center mt-10 mb-20">
              {loadingIcon()}
              {loadingLabel ? <span className="!text-theme-100 ltr:ml-2 rtl:mr-2 font-primary-semibold">{loadingLabel}</span> : null}
            </div>}
            pullDownToRefreshThreshold={50}
          >
            {!showNoResult() ? < div style={{ display: !isLoaderHidden() ? "none" : "block" }} role="status" className="text-center mt-10 mb-20">
              {loadingIcon()}
              {loadingLabel ? <span className="!text-theme-100 ltr:ml-2 rtl:mr-2 font-primary-semibold">{loadingLabel}</span> : null}
            </div> : null}
            <div>
              {cards && cards.length !== 0 ? (
                <div className="grid md:grid-cols-2 grid-cols-1 lg:gap-8 gap-6">
                  {cards.map((card: any, index: any) =>
                    filterType == 'stories' ?
                      <MainCard
                        variant="What-to-buy"
                        {...card}
                        imageClass={'md:h-[47vw] lg:h-[27.1vw]'}
                        key={index}
                      /> :
                      <ThingsToDoCard
                        {...card}
                        imageClass={'md:h-[47vw] lg:h-[30vw]'}
                        cardSize={'xlarge'}
                        titleClass={`${'text-lg'}`}
                        key={index}
                        buttonStyle={'!py-[6px]'}
                      />
                  )}</div>) : showNoResult()}
            </div>
          </InfiniteScroll>
        </div>

        {/* end cards */}
      </div>
    </>
  );
};

export default FilterExplorerComp;
