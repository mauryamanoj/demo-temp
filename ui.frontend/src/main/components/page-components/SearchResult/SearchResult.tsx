/* eslint-disable max-len */
import React, { useEffect, useState } from "react";
import { useFormik } from "formik";
import Icon from "../../common/atoms/Icon/Icon";
import InfiniteScroll from 'react-infinite-scroll-component';
import SearchBox from "../../common/atoms/SearchBox/SearchBox";
import SearchCard from "../../common/atoms/SearchCard/SearchCard";
import SearchInputBlock from "./Components/SearchInputBlock/SearchInputBlock";
import Text from "src/main/components/common/atoms/Text/Text";
import { Filters } from "../../common/atoms/Filters/Filters";
import { NoResult } from "../../common/atoms/NoResult/NoResult";
import { changeLimit, handleSearch, resetOffset } from "./Services/search-service";
import { getLanguage } from "src/main/util/getLanguage";
import { getParameterByName, handelText, stripHTML, visibleComponent } from "./Helpers/index";
import { locationParam, sortParam, typeParam } from './Static/index';
import { SearchRes, CardInfo } from "./Interface/index";
import { useResize } from "src/main/util/hooks/useResize";

import { gtmCustomEventClick } from "src/main/util/googleTagManager";
import { trackEvents } from 'src/main/util/updateAnalyticsData';

var searchCards: CardInfo[] = [];
var contentType: string[] = [];
var location: string[] = [];
var showMostRecent = false;

/**
 * SearchResult Component
 * This component displays search results with filters, search input, and pagination.
**/
const SearchResult: React.FC<SearchRes> = (props) => {
  const {
    searchPagePath,
    clearAllFiltersLabel,
    viewAllResultsLabel,
    searchPlaceholderLabel,
    resultsCountLabel,
    resultCountLabel,
    regions,
    noResultsFoundLabel,
    noResultFoundDescription,
    clearLabel,
    locationLabel,
    resetAllFiltersLabel,
    modalFiltersTitle,
    applyLabel,
    selectLocationLabel,
    buttonLabel,
    searchLimit,
    searchPlaceholder,
    clearAllResultsLabel,
    contentTypeFilter,
    contentLabel,
    sortFilterLabel,
    searchModalTitle,
    clearInputStyle,
    clearAllButton
  } = props;


  const lang = getLanguage();

  const [updatedRegions, setUpdatedRegions] = useState<any>();
  // Sort regions and inject All
  useEffect(() => {
    if (regions.length) {
      const updatedregions = regions;
      updatedregions.sort((a, b) => a.value.localeCompare(b.value));
      updatedregions.unshift({ code: undefined, value: `${lang == 'ar' ? 'جميع الوجهات' : 'All Destinations'}` });
      setUpdatedRegions(updatedregions);
    }
  }, [])

  const [cards, setCards]: any = useState([]); // note: we need any here
  const [changeKey, setResetKey] = useState(Date.now());
  const [hasMore, setHasMore] = useState(true);
  const [hideLoader, setHideLoader] = useState(false);

  const [isOverlayoutClick, setIsOverlayoutClick] = useState(false);
  const [loading, setLoading] = useState(true);
  const [searchboxValue, setSearchboxValue]: any = useState([]);
  const [resetKey, setResetComponent] = useState(Date.now());
  const [resetSearchBox, setResetSearchBox] = useState(Date.now());
  const [result, setResult]: any = useState({});
  const [saveArr, setSaveArr]: any = useState([]);
  const [searchLocationVal, setSearchLocationVal]: any = useState("");
  const [showFilter, setShowFilter] = useState(false);
  const [showNo, setShowNo] = useState(false);
  const [showSearchBox, setShowSearchBox] = useState(false);

  const [selectedContent, setSelectedContent]: any = useState([]);
  const [selectedLocation, setSelectedLocation]: any = useState([]);


  const { isMobile } = useResize(1024);


  // Function to determine whether loader should be hidden
  function isLoaderHidden() {
    return (
      cards.length == 0 ||
      hideLoader ||
      (result && result?.pagination?.total == cards.length)
    );
  }





  // Function to set search text in local storage
  function setSearchText(searchTerm: string) {
    const arr = saveArr;
    if (searchTerm) {
      arr.push(searchTerm);
      setSaveArr(arr);
      window.localStorage.setItem("searchkeys", saveArr.join());
    }
    if (arr.length > 5) {
      saveArr.shift();
      saveArr.reverse();
      setSaveArr(saveArr);
      window.localStorage.setItem("searchkeys", saveArr.join());
    }
  }

  // Function to handle location change
  function handleSearchLocationChange(event: any) {
    console.log("eventeventevent", event);
    setSearchLocationVal(event.target.value);
  }

  // Function to handle applying filters

  const [isInitialSearchValueSet, setIsInitialSearchValueSet] = useState(false);

  // Function to submit on search box
  const searchForm = useFormik({
    initialValues: {
      search: "",
    },
    enableReinitialize: false,
    onSubmit: (values) => {
      document.body.style.overflow = "unset";
      setSearchText(stripHTML(values.search));
      searchCards = [];
      setCards([]);
      handleApi(stripHTML(searchForm?.values?.search), false);
      setShowSearchBox(false);
      document.querySelector("input")?.blur();
      setTimeout(() => {
        setIsOverlayoutClick(!isOverlayoutClick);
      }, 200);
    },
  });

  useEffect(() => {
    if (!isInitialSearchValueSet) {
      const searchParam = getParameterByName('search');
      if (searchParam !== null) {
        searchForm.setFieldValue('search', searchParam);
        setIsInitialSearchValueSet(true);
      }
    }
  }, [searchForm, isInitialSearchValueSet]);



  // loadmore data each time
  function loadmore() {
    if (hasMore && !loading) {
      setTimeout(async () => {
        await handleApi(searchForm?.values?.search, true);
      }, 600);
    }
  }


  // Function to handle clearing all filters and resetting URL
  function handleClearAll(type?: string) {
    if (type == "all") {
      const url = window.location.href;
      const urlObj = new URL(url);
      urlObj.search = '';
      urlObj.hash = '';
      const result = urlObj.toString();
      window.location.href = result + '?search=';
    }
    location = [];
    searchCards = [];
    setLoading(false);
    resetOffset();
    setCards([]);
    setResetComponent(Date.now()); // reset checkboxes
    searchURL();
  }

  // Function to handle clearing filters
  function handelClearFilter(type?: string) {
    setShouldRunTheApi(true);
    if (type === 'content') {
      setSelectedContent([]);
    } else if (type === 'location') {
      setSelectedLocation([]);
    } else {
      setSelectedContent([]);
      setSelectedLocation([]);
    }
  }

  const setGtmCustomLink = (searchText: string) => {
    const parameters: any = {
      page_type: 'Search',
      searched_term: searchText
    };
    gtmCustomEventClick(parameters);

    const dataSet: any = {};
    dataSet.trackEventname = 'no_result_found';
    dataSet.trackSection = searchText;
    dataSet.trackName = "dl_push";
    dataSet.eventProperties = parameters;
    dataSet.trackEventname && trackEvents(dataSet);
  };

  // Function to make API call based on search text and loadmore flag
  async function handleApi(searchText: string, isLoadmore: boolean) {
    setLoading(true);
    setHideLoader(false);
    if (searchLimit) {
      changeLimit(searchLimit);
    }
    var str = selectedContent && selectedContent.length !== 0 ? `&${typeParam}=${selectedContent.join(",")}` : "";
    var str2 = selectedLocation && selectedLocation.length !== 0 ? `&${locationParam}=${selectedLocation.join(",")}` : "";
    var str3 = showMostRecent ? `&${sortParam}=date` : "";

    await handleSearch(
      searchText,
      lang,
      isLoadmore,
      "",
      str + str2 + str3
    ).then((data: any) => {
      setResult(data.data);
      if (data.data && data.data.data && data.data.data.length == 0) {
        setLoading(false);
        resetOffset();
        setHasMore(false);
        setShowNo(true);
        setGtmCustomLink(searchText);

      } else {
        // push the new cards
        for (let index = 0; index < data.data.data.length; index++) {
          const element = data.data.data[index];
          searchCards.push(element);
        }
        setShowNo(false);

        setHasMore(true);

        // add the cards
        setCards(searchCards);

        // stop loading
        setLoading(false);

      }

    });
  }

  // Function to show no results component if needed
  function showNoResult() {
    const noresult = (
      <NoResult
        title={noResultsFoundLabel ? noResultsFoundLabel : ""}
        description={noResultFoundDescription ? noResultFoundDescription : ""}
      />
    );

    return showNo ? noresult : null;
  }

  const handleLocationChange = (location: any) => {
    setSelectedLocation(location);
  }

  const handleContentChange = (content: any) => {
    setSelectedContent(content);
  }


  const triggerApiCall = () => {
    searchCards = [];
    setCards([]);
    handleApi(searchForm?.values?.search || getParameterByName("search") || '', false);

    //getSelectedFilterNumber();
    setShowFilter(false);

  };
  function handleApply() {
    triggerApiCall();
  }

  // on mobile triggering the api call happens when clicking on a buttton,
  // so shouldRunTheApi state is used to control when the api call should happen.
  const [shouldRunTheApi, setShouldRunTheApi] = useState(true);
  useEffect(() => {
    if (shouldRunTheApi) {
      triggerApiCall();
      setShouldRunTheApi(isMobile ? false : true);
    }
  }, [selectedContent, selectedLocation]);


  // Function to handle search URL
  function searchURL(isEmpty?: boolean) {
    var search: any = isEmpty ? "" : getParameterByName("search");
    if (search) {
      searchForm.setFieldValue("search", search);
    }
    handleApi(search, false);
  }

  // Function to handle checkbox change for sorting
  function checkboxHandleChange(event: any) {
    event.stopPropagation();
    showMostRecent = event.target.checked;

    if (!isMobile) {
      // Only desktop - clear the cards and don't call the API
      searchCards = [];
      setCards([]);
      handleApi(searchForm?.values?.search, false);
    }

  }

  // Function to hide filter based on conditions
  function hideFilter() {
    return true;
    // if (!isMobile) {
    //   if ((localContentType.length == 0 && localLocations.length == 0) && cards && cards.length == 0) {
    //     return true; // hide
    //   }
    //   if ((localContentType.length != 0 || localLocations.length != 0) && cards && cards.length == 0) {
    //     return false; // show
    //   }
    // }
  }

  // Function to remove body scroll classes
  function removeBodyScroll() {
    document.querySelector('.header-ele')?.classList.remove('min-h-full');
    document.body.classList.remove('overflow-y-hidden');
    document.body.style.position = "initial";
    document.querySelector('.header-ele')?.classList.remove('h-full');
  }

  // Function to clear input on search box
  function clearInput() {
    if (showSearchBox) {
      searchForm.resetForm();
    }
  }

  // Function to handle click on search input
  function inputOnClick() {
    setShowSearchBox(!showSearchBox);
    clearInput();
    removeBodyScroll();
  }


  useEffect(() => {

    //searchURL();
    setIsOverlayoutClick(!isOverlayoutClick);
    if (isMobile) {
      setShowFilter(false);
    }

    visibleComponent(".other-components", "none");

    // keep exploring component has margin bottom on search page only
    if (document.querySelector(".keep-exploring-section")) {
      document.querySelector(".keep-exploring-section")?.classList.add('mb-20')
    }
    // const mobileMediaQuery = window.matchMedia('(max-width: 1024px)'); // Adjust the breakpoint as needed
    // const handleMobileChange = (event: any) => {
    //   setIsMobile(event.matches);
    // };

    // mobileMediaQuery.addEventListener('change', handleMobileChange);
    // setIsMobile(mobileMediaQuery.matches);

    // return () => {
    //   mobileMediaQuery.removeEventListener('change', handleMobileChange);
    // };

  }, []);


  useEffect(() => {
    // Select the event cards element
    const otherComponents = document.querySelector('.other-components');
    const breadcrumbComponents = document.querySelector('#breadcrumb-container');
    // Check if event cards element exists
    if (otherComponents) {
      // Add classes to adjust the styling of event cards
      otherComponents.classList.add('lg:mt-[79.5px]');
      otherComponents.classList.add('mt-[64px]');
    }

    // todo: replace this with breadcrumb class
    if (breadcrumbComponents) {
      // Add classes to adjust the styling of breadcrumb Components
      breadcrumbComponents.classList.add(
        'lg:!mt-[108px]',
        '!mt-[84px]',
        '!mb-[16px]',
        'lg:!mb-[26px]',
        'md:px-8',
        'lg:px-8',
        'xl:px-16',
        '2xl:px-[100px]'
      );

      breadcrumbComponents.classList.remove('md:px-100');
    }

    // Check if there are no cards
    if (cards.length === 0) {
      // Show other components
      visibleComponent(".other-components", "block");
    } else {
      // Hide other components
      visibleComponent(".other-components", "none");
    }
  }, [cards]);




  function loadingIcon() {
    return <div className="fixed w-full h-full z-10 left-0 top-20 bottom-0  flex justify-center items-center bg-gray-100/40"><svg aria-hidden="true" className="inline w-8 h-8 text-gray-200 animate-spin dark:text-gray-600 fill-theme-100" viewBox="0 0 100 101" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path d="M100 50.5908C100 78.2051 77.6142 100.591 50 100.591C22.3858 100.591 0 78.2051 0 50.5908C0 22.9766 22.3858 0.59082 50 0.59082C77.6142 0.59082 100 22.9766 100 50.5908ZM9.08144 50.5908C9.08144 73.1895 27.4013 91.5094 50 91.5094C72.5987 91.5094 90.9186 73.1895 90.9186 50.5908C90.9186 27.9921 72.5987 9.67226 50 9.67226C27.4013 9.67226 9.08144 27.9921 9.08144 50.5908Z" fill="currentColor" />
      <path d="M93.9676 39.0409C96.393 38.4038 97.8624 35.9116 97.0079 33.5539C95.2932 28.8227 92.871 24.3692 89.8167 20.348C85.8452 15.1192 80.8826 10.7238 75.2124 7.41289C69.5422 4.10194 63.2754 1.94025 56.7698 1.05124C51.7666 0.367541 46.6976 0.446843 41.7345 1.27873C39.2613 1.69328 37.813 4.19778 38.4501 6.62326C39.0873 9.04874 41.5694 10.4717 44.0505 10.1071C47.8511 9.54855 51.7191 9.52689 55.5402 10.0491C60.8642 10.7766 65.9928 12.5457 70.6331 15.2552C75.2735 17.9648 79.3347 21.5619 82.5849 25.841C84.9175 28.9121 86.7997 32.2913 88.1811 35.8758C89.083 38.2158 91.5421 39.6781 93.9676 39.0409Z" fill="currentFill" />
    </svg>
    </div>;
  }
  return (
    <>
      {loading && loadingIcon()}
      <div className="relative px-[20px] lg:px-8 xl:px-16 2xl:px-[100px] lg:pt-[0px] lg:!pb-[0] lg:mt-0 mt-0 lg:mb-0 mb-4 lg:min-h-[800px] min-h-[500px]">
        {/* start search input */}
        {!showSearchBox ? <div>
          <SearchInputBlock
            searchForm={searchForm}
            searchPlaceholderLabel={searchPlaceholderLabel}
            isMobile={isMobile}
            searchPagePath={searchPagePath}
            viewAllResultsLabel={viewAllResultsLabel}
            searchPlaceholder={searchPlaceholder}
            isOverlayoutClick={isOverlayoutClick}
            setShowFilter={setShowFilter}
            showFilter={showFilter}
            checkboxHandleChange={checkboxHandleChange}
            sortFilterLabel={sortFilterLabel}
            withFilterIcon={true}
            clearInputStyle={clearInputStyle}
            isDisabled={true}
            inputOnClick={isMobile ? inputOnClick : null}
            localContentType={selectedContent}
            localLocations={searchboxValue}
          />
        </div>
          : <SearchInputBlock
            isWithCloseIcon={true}
            searchForm={searchForm}
            searchPlaceholderLabel={searchPlaceholderLabel}
            searchPagePath={searchPagePath}
            searchPlaceholder={searchPlaceholder}
            setShowFilter={setShowFilter}
            searchModalTitle={searchModalTitle}
            withFilterIcon={false}
            hideOverlay={true}
            isMobile={isMobile}
            showSearchBox={showSearchBox}
            handleSearchClick={() => { setShowSearchBox(false); }}
          />}

        {/* end search input */}


        {/* start result number section */}
        <div className="flex align-center">
          {cards && cards.length != 0 ? <Text
            text={
              resultsCountLabel
                ? handelText(
                  resultsCountLabel,
                  result && "pagination" in result
                    ? result.pagination.total : 0,
                  resultCountLabel
                )
                : null
            }
            styles="lg:text-3.5xl  font-primary-bold text-[#000] lg:mt-10 mt-4"
            type="h2"
          /> : null}
          {clearAllResultsLabel ?
            <span className="ltr:ml-auto rtl:mr-auto" onClick={() => handleClearAll('all')}>
              <Text
                text={clearAllResultsLabel}
                styles="cursor-pointer pt-[14px] hidden lg:block font-primary-bold text-sm text-theme-100 lg:mt-10 mt-4"
                type="a"
              />
            </span>
            : null}

        </div>
        {/* end result number section */}

        {/* start tag section */}
        <div className="flex gap-2 mt-4">
          {isMobile && selectedLocation && selectedLocation.length != 0 ?
            <div className="flex align-center gap-2">
              <div className="flex align-center bg-theme-100 justify-center px-4 py-2 text-white text-sm rounded-[20px]"><span className="pt-[1px]">{selectedLocation.length} {locationLabel}</span>
                <span onClick={() => handelClearFilter('location')} className="pt-[1px]">
                  <Icon name="close-circle" svgClass="mt-[3px] ltr:ml-1 rtl:mr-1 cursor-pointer" /></span> </div>
            </div> : null}

          {isMobile && selectedContent && selectedContent.length != 0 ?
            <div className="flex align-center gap-2">
              <div className="flex align-center justify-center bg-theme-100 px-4 py-2 text-white text-sm rounded-[20px]"><span className="pt-[1px]">{selectedContent.length} {contentLabel}</span>
                <span onClick={() => handelClearFilter('content')} className="pt-[1px]">
                  <Icon name="close-circle" svgClass="mt-[3px] ltr:ml-1 rtl:mr-1 cursor-pointer" /></span> </div>
            </div> : null}
        </div>
        {/* start end section */}

        {/* start grid   */}
        <div className="flex lg:mt-10 mt-6">
          {/* start filters */}
          {
            <div className={`lg:max-w-sm max-w-full w-full ${showFilter ? 'fixed overflow-scroll max-w-full w-full h-full left-0 top-0 bg-[#F8F8F8] z-50 lg:pb-0 pb-[100px]' : 'lg:block hidden'}`}>
              <div className={`flex justify-between`}>
                {modalFiltersTitle &&
                  <Text text={modalFiltersTitle}
                    styles="lg:text-sm text-lg mb-4 uppercase lg:p-0 pt-10 px-[20px] font-primary-bold text-[#000]" type="span" />
                }
                {clearAllFiltersLabel && !isMobile && (selectedContent?.length != 0 || selectedLocation?.length != 0) ? <div onClick={() => handelClearFilter()}>
                  <Text text={clearAllFiltersLabel}
                    styles="cursor-pointer text-sm font-primary-semibold text-theme-100" type="span" />
                </div> : null}
                {isMobile ? <span onClick={() => {
                  setShowFilter(false);
                }} className="relative top-[38px] ltr:mr-4 rtl:ml-4">
                  <Icon name="close-iconx24" />
                </span> : null}

              </div>
              {/* start content type Filter   */}
              <Filters
                modalFiltersTitle={modalFiltersTitle}
                selectLocationLabel={selectLocationLabel}
                applyLabel={applyLabel}
                filters={contentTypeFilter.sort()}
                clearAllButton={clearAllButton}
                clearAllFiltersLabel={resetAllFiltersLabel}
                clearLabel={clearLabel}
                label={contentLabel}
                onSelect={handleContentChange}
                selectedValues={selectedContent}
                isMobile={isMobile}
                handelClearAllFilter={() => handelClearFilter()}
                handleApply={handleApply}
                searchLocationVal={searchLocationVal}
                formKey={changeKey}
                isExpanded={true}
                sortFilterLabel={sortFilterLabel}
                checkboxHandleChange={checkboxHandleChange}
              />
              {/* end content type Filter */}

              {updatedRegions?.length &&
                <Filters
                  modalFiltersTitle={modalFiltersTitle}
                  selectLocationLabel={selectLocationLabel}
                  applyLabel={applyLabel}
                  filters={updatedRegions}
                  handelClearAllFilter={() => handelClearFilter()}
                  clearAllButton={clearAllButton}
                  clearAllFiltersLabel={resetAllFiltersLabel}
                  clearLabel={clearLabel}
                  label={locationLabel}
                  onSelect={handleLocationChange}
                  selectedValues={selectedLocation}
                  isMobile={isMobile}
                  handleApply={handleApply}
                  searchLocationVal={searchLocationVal}
                  formKey={resetKey}
                  isExpanded={false}
                  type={'location'}
                >
                  <SearchBox
                    className={'font-primary-regular text-base w-full py-2 px-10 border rounded-lg border-theme-100 outline-none'}
                    searchPlaceholderLabel={searchPlaceholderLabel}
                    onKeyUp={() => handleSearchLocationChange(event)}
                    isMobile={isMobile}
                    smallIcon={true}
                    onKeyDown={() => { console.log(); }}
                    setIsInputFocused={() => { console.log(); }}
                    key={resetSearchBox}
                  />
                </Filters>}
            </div>
          }
          {/* end filters */}

          {/* start cards */}


          <div className={`${hideFilter() ? 'w-full max-w-full lg:mx-10' : 'w-full max-w-7xl lg:mx-10'}`}>
            <InfiniteScroll
              dataLength={cards?.length}
              next={loadmore}
              hasMore={hasMore}
              refreshFunction={loadmore}
              scrollThreshold={0.4}
              loader={<div style={{ display: isLoaderHidden() ? "none" : "block" }} className="rounded-md mt-10 mb-10 max-w-full w-full">
                <div className="animate-pulse flex space-x-4 items-center">
                  <div className="rounded-s-lg rounded-e-lg bg-theme-100  max-w-[283px] max-h-[283px] lg:w-full w-[76px] lg:h-[283px] h-[76px]"></div>
                  <div className="flex-1 space-y-6 py-1">
                    <div className="h-2 bg-theme-100 rounded lg:w-40 w-10"></div>
                    <div className="space-y-3 lg:w-80 w-30">
                      <div className="h-2 bg-theme-100 rounded"></div>
                    </div>
                  </div>
                </div>
              </div>}
              pullDownToRefreshThreshold={50}

            >
              <div>
                {cards && cards.length !== 0 ? (
                  <SearchCard
                    cards={cards}
                    buttonLabel={buttonLabel}
                    isMobile={isMobile}
                    searchText={searchForm?.values?.search} />) : showNoResult()}
              </div>
            </InfiniteScroll>
          </div>

          {/* end cards */}
        </div>
        {/* end grid   */}
      </div>

    </>
  );
};

export default SearchResult;
