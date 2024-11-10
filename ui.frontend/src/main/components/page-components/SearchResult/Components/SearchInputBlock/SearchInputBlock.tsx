import React, { useEffect, useState,useRef } from "react";
import Autocomplete from "src/main/components/common/atoms/Autocomplete/Autocomplete";
import { Form } from "src/main/components/common/atoms/Form/Form";
import Icon from "src/main/components/common/atoms/Icon/Icon";
import SearchBox from "src/main/components/common/atoms/SearchBox/SearchBox";
import Text from "src/main/components/common/atoms/Text/Text";
import RecentlySearched from "../RecentlySearched/RecentlySearched";
import { upperFirst } from "lodash";
import { stripHTML, getParameterByName } from "../../Helpers/index";
import {
  handleSearchSugesstions
} from "../../Services/search-service";
import { getLanguage } from "src/main/util/getLanguage";
import { roundedDesktop, searchBoxStyleDesktop, placeholderStyleDesktop } from "../../Style/Desktop";
import {
  roundedMoible, searchBoxStyleMobile,
  placeholderStyleMobile, wrapperStyleMobile, formStyleMobile, clearInputStyleMobile
} from "../../Style/Mobile";

var mergedArrCopy: any = [];

// start search autocomplete styles
var rounded = "";
var searchBoxStyle = "";
var placeholderStyle = "";
var wrapperStyle = "";
var formStyle = "";
var clearInputStyle = "";
// end search autocomplete styles

const SearchInputBlock: React.FC<any> = (props) => {
  const [searchData, setSearchData]: any = useState([]);
  const [sugesstions, setSugesstions]: any = useState([]);
  const [openOverlayout, setOpenOverlayout] = useState(false);
  const [resetInput, setResetInput] = useState(Date.now());
  const [showClear , setShowClear] = useState<boolean>(false);
  const [hideSearchBox, setHideSearchBox] = useState<boolean>(true);
  const [placeholderSuggestion, setPlaceholderSuggestion] = useState<string>("");
  const [zindex, setZindex] = useState("z-[51]");
  const [isInputFocused, setIsInputFocused] = useState<boolean>(false);
  const [hideSpinner, setHideSpinner]: any = useState(false);
  const ref:any = useRef(null);


  const {
    searchForm,
    searchPlaceholderLabel,
    searchPagePath,
    viewAllResultsLabel,
    checkboxHandleChange,
    sortFilterLabel,
    showFilter,
    setShowFilter,
    isWithCloseIcon,
    handleSearchClick,
    trending,
    clearAllHandler,
    recentSearchData,
    clearItemHandler,
    recentSearchLabel,
    clearHistory,
    showHistory,
    searchPlaceholder,
    isOverlayoutClick,
    isChange,
    searchModalTitle,
    withFilterIcon,
    hideOverlay,
    isDisabled,
    inputOnClick,
    showSearchBox,
    onClickOutside,
    isMobile,
    localLocations,
    localContentType

  } = props;
  useEffect(() => {
    if (!isMobile) { // desktop
      rounded = roundedDesktop;
      searchBoxStyle = searchBoxStyleDesktop;
      placeholderStyle = placeholderStyleDesktop;
      removeBodyScroll();
    } else { // mobile
      // stop scroll
      if (!isSearchResult() || (showSearchBox && isMobile)) {
        rounded = roundedMoible;
        searchBoxStyle = searchBoxStyleMobile;
        placeholderStyle = placeholderStyleMobile;
        wrapperStyle = wrapperStyleMobile;
        formStyle = formStyleMobile;
        clearInputStyle = clearInputStyleMobile;
        addBodyScroll();
      } else {
        rounded = roundedDesktop;
        searchBoxStyle = searchBoxStyleDesktop;
        placeholderStyle = placeholderStyleDesktop;
        wrapperStyle = "";
        formStyle = "";
        clearInputStyle = "";
        removeBodyScroll();
      }
    }
  }, [showSearchBox]);

  function addBodyScroll(){
    document.body.classList.add('overflow-y-hidden');
    if(!isMobile) document.body.style.position = "fixed";
    document.querySelector('.header-ele')?.classList.add('min-h-full');
    document.querySelector('.header-ele')?.classList.add('h-full');
  }

  function removeBodyScroll(){
    document.body.classList.remove('overflow-y-hidden');
    document.body.style.position = "initial";

    if(showSearchBox || isSearchResult()){ // check this on iphone
      document.querySelector('.header-ele')?.classList.remove('min-h-full');
      document.querySelector('.header-ele')?.classList.remove('h-full');
    }
  }

  function clearInput() {
    setResetInput(Date.now()); // reset input search
    clearSuggestions();
    searchForm.resetForm();
    setSearchData("");
    setHideSearchBox(true);
    setPlaceholderSuggestion("");
    setShowClear(false);
    setIsInputFocused(true);
    handleOverlayoutClick();
    removeBodyScroll();
  }

  function formatPlaceholder(
    suggestionPlaceholder: string,
    currentValue: string
  ) {
    if (
      suggestionPlaceholder &&
      suggestionPlaceholder.toUpperCase().startsWith(currentValue.toUpperCase())
    ) {
      const regEx = new RegExp(currentValue, "i");
      return upperFirst(suggestionPlaceholder.replace(regEx, currentValue));
    }

    return "";
  }

  function handleAutocomplete(event: any) {

    if(isMobile && isSearchResult() && !showSearchBox){
      event.stopPropagation();
      event.preventDefault();
      return false;
    }
    if (event.target.value.length === 0 && event.keyCode === 8) {
      setResetInput(Date.now()); // reset input search
      setShowClear(false);
      handleOverlayoutClick();
    }
    if (sugesstions.length == 0) {
      setPlaceholderSuggestion("");
      return;
    } else {
      setShowClear(true);
    }

    if (event.key === "Tab") {
      event.preventDefault();
      const newValue = upperFirst(sugesstions[0]);
      searchForm.setFieldValue("search", newValue);
      clearSuggestions();
    }
  }


  function handleSuggestion(event: any) {
    if(isMobile && isSearchResult() && !showSearchBox){
      event.stopPropagation();
      event.preventDefault();
      return false;
    }
    if (event.target.value.length === 0 && event.keyCode === 8) {
      setPlaceholderSuggestion("");
      setShowClear(false);
    } else {
      const search: any = getParameterByName("search");
      if (search != event.target.value) {
        setShowClear(true);
        setOverlay(true, "hidden", "z-[51]");
      } else {
        setOverlay(true, "unset", "z-[1]");
      }
    }
  }

  function handleFilter() {
    window.scrollTo(0, 0);
    setShowFilter(!showFilter);
    setResetInput(Date.now()); // reset input search
    setShowClear(false);
    handleOverlayoutClick();
  }

  function renderTrending() {
    return (
      <>

        {trending && trending.length != 0 ?
          <>
            <Text text={trending?.title}
              styles="lg:text-sm text-xs font-primary-bold line-clamp-1 uppercase" type="p" />
            <div className="text-black mb-4 mt-2">
              {trending
                && trending?.items?.map((item: any, index: number) =>
                  <div className="inline-block ltr:mr-2 rtl:ml-2 mb-2">
                    <a
                      href={item?.ctaLink}
                      key={index}
                      // eslint-disable-next-line max-len
                      className="inline-block rounded-[20px] px-4 lg:py-2 py-1 border border-[#AAAAAA] border-1 font-primary-semibold text-[#4B4B4B]"
                      title={item?.ctaLabel}>
                      <Icon
                        name="arrow-tag"
                        svgClass='ltr:mr-[8px] rtl:ml-[8px] rtl:scale-x-[-1] inline-block' />
                      {item?.ctaLabel}
                    </a>
                  </div>)}
            </div>
          </> : null}
      </>
    );
  }

  function isSearchResult() {
    return window.location.href.includes("search-result");
  }

  function clearSuggestions() {
    setSearchData([]);
    setSugesstions([]);
    setPlaceholderSuggestion("");
  }

  function capitalizeFirstLetter(string: string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
  }

  function handleOverlayoutClick() {
      setOverlay(false, "unset", "z-0");
      setHideSearchBox(true);
      setPlaceholderSuggestion("");
  }

  function searchIconHandler() {
    searchForm.submitForm();
  }

  function setOverlay(show: boolean, bodyOverflow: string, zIndex: string) {
    setOpenOverlayout(show);
    document.body.style.overflow = bodyOverflow;
    setZindex(zIndex);
  }

  function handleChange(event: any) {
    event.stopPropagation();
    var mergedArr: any = [];
    const val = event.currentTarget.querySelector("input");
    if (!val.value || val.value == "") {
      clearInput();
      clearSuggestions();
      handleOverlayoutClick();
    }
    searchForm.setFieldValue("search", capitalizeFirstLetter(val.value));
    setPlaceholderSuggestion(
      formatPlaceholder(placeholderSuggestion, val.value)
    );
    handleSearchSugesstions(stripHTML(val.value), getLanguage()).then(
      (data: any) => {
        setHideSpinner(false);
        if (data.data.status == "error") {
          clearSuggestions();
          clearInput();
          setPlaceholderSuggestion("");

          return;
        }
        mergedArr = data?.data?.results?.concat(data.data.data);
        mergedArrCopy = data?.data?.results?.concat(data.data.data);
        setPlaceholderSuggestion(
          formatPlaceholder(data.data.data[0], val.value)
        );
        setSearchData(mergedArr);
        setSugesstions(data.data.data);
        if (data.data.results) {
          setHideSearchBox(false);
          setTimeout(() => {
            setHideSpinner(true);
          }, 1000);
        } else {
          setHideSearchBox(true);

        }

      }
    );
  }

  function renderRecentlySearched() {
    return (<div>
      <RecentlySearched
        clearAll={clearAllHandler}
        isChange={isChange}
        recentSearchData={recentSearchData}
        searchPagePath={searchPagePath}
        clearItemHandler={clearItemHandler}
        recentSearchLabel={recentSearchLabel}
        clearHistory={clearHistory}
        showHistory={showHistory}
        isSearchResult={isSearchResult}
        isMobile={isMobile}
      >
        {renderTrending()}
      </RecentlySearched>
    </div>);
  }

  function renderAutocomplete() {
    return (<div>
        <Autocomplete
        trending={trending}
        searchData={searchData}
        showSearchBox={hideSearchBox}
        isMobile={isMobile}
        searchPagePath={searchPagePath}
        param={searchForm.values.search}
        viewAllResultsLabel={viewAllResultsLabel}
        showCategory={true}
      >
        {hideSearchBox ? renderTrending() : null}
      </Autocomplete>
    </div>);
  }



  function renderForm() {
    return (<Form
      className={`relative flex align-center w-full ${formStyle}`}
      autoComplete="off"
      onChange={handleChange}
      onSubmit={searchForm.handleSubmit}
    >

      <SearchBox
        tabIndex={1}
        className={searchBoxStyle}
        clearInput={clearInput}
        searchForm={searchForm}
        onKeyDown={handleAutocomplete}
        onKeyUp={handleSuggestion}
        searchIcon={searchIconHandler}
        showClear={showClear}
        withFilterIcon={withFilterIcon}
        searchPlaceholderLabel={searchPlaceholderLabel}
        handleFilter={handleFilter}
        openOverlayout={openOverlayout}
        checkboxHandleChange={checkboxHandleChange}
        sortFilterLabel={sortFilterLabel}
        key={resetInput}
        inputOnClick={inputOnClick}
        disabled={isDisabled}
        clearInputStyle={clearInputStyle}
        showSearchBox={showSearchBox}
        showFilter={showFilter}
        isMobile={isMobile}
        localLocations={localLocations}
        localContentType={localContentType}
        smallIcon={false}
        setIsInputFocused={() => {
          setIsInputFocused(true);
        }}
      >
        {isInputFocused && (
          <span className={placeholderStyle}>
            {placeholderSuggestion}
          </span>
        )}
        {isWithCloseIcon && !isMobile ?
          <span onClick={handleSearchClick}>
            <Icon
              name="close-icon-v1"
              svgClass="mt-[1px] ltr:ml-1 rtl:mr-1 cursor-pointer absolute top-[-12px] ltr:right-[-14px] rtl:left-[-14px] bottom-14 z-50" />
          </span> :
          (isMobile && !isSearchResult()) || showSearchBox ? <span onClick={handleSearchClick}>
            <Icon
              name="close-iconx24"
              svgClass="mt-[1px] ltr:ml-1 rtl:mr-1 cursor-pointer absolute top-[-55px] ltr:right-0 rtl:left-0 bottom-14 z-50" />
          </span> : null
        }

      </SearchBox>
    </Form>);
  }

  useEffect(() => {
    handleOverlayoutClick();
  }, [isOverlayoutClick]);

  useEffect(() => {
    const handleClickOutside = (event:any) => {
      if (ref.current && !ref.current?.contains(event.target)) {
        document.body.style.position = "initial";
        document.body.style.overflow = "unset";
        onClickOutside && onClickOutside();
      }
    };
    document.addEventListener('click', handleClickOutside, true);
    return () => {
      document.removeEventListener('click', handleClickOutside, true);
    };
  }, [ onClickOutside ]);

  return (
    <div ref={ref} className={wrapperStyle} >
      <div className={`relative w-full ${zindex}`}>
        {searchModalTitle ?
          <Text
            text={searchModalTitle}
            styles="text-black text-3xl font-primary-bold"
            type="p"
          /> : null}
        {renderForm()}
        {hideSearchBox ? !isSearchResult() ? renderRecentlySearched() : null : renderAutocomplete()}
      </div>

      {/* start overlayout openOverlayout && */}
      {openOverlayout && !isMobile && !hideSearchBox && !hideOverlay ? (
        <div
          onClick={handleOverlayoutClick}
          className="fixed top-0 bottom-0 right-0 left-0 bg-black z-[50] bg-opacity-50"
        >
          <div
            style={{ display: hideSpinner ? "none" : "flex" }}
            className="text-theme-100 relative top-10 h-full text-center text-lg font-primary-regular w-full">
            <div className="m-auto flex align-center">
              <div className="mr-2 mt-1 w-4 h-4 rounded-full bg-white animate-ping"></div>
              <div className="mr-2 mt-1 w-4 h-4 rounded-full bg-white"></div>
              <p className="text-white">{searchPlaceholder ? searchPlaceholder : ""}</p>
            </div>
          </div>
        </div>
      ) : null}
      {/* end overlayout */}
    </div>
  );
};

export default SearchInputBlock;

