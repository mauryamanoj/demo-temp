import { useEffect, useState } from "react";
import { optionProps } from "src/main/components/common/atoms/Dropdown/Dropdown";
import { getLanguage } from "src/main/util/getLanguage";

export interface ICountry {
  countryName: string;
  flag: string;
  visaGroup: string;
}
const baseUrl =
  window.location.origin === "http://localhost:9000" ? "https://qa-revamp.visitsaudi.com" : window.location.origin;

export const useCountries = () => {
  const [countries, setCountries] = useState<optionProps[]>([]);

  useEffect(() => {
    const fetchData = async () => {
      let finalData: { code: number; message: string; response: ICountry[] } = {
        code: 200,
        message: "empty",
        response: [],
      };

      try {
        const _res = await fetch(`${baseUrl}/bin/api/v1/geo/countries?locale=${getLanguage()}`);

        if (_res) {
          finalData = await _res.json();

          if (finalData.code === 200) {
            setCountries(
              finalData.response.map((country) => ({
                id: country.countryName,
                title: country.countryName,
                src: country.flag,
                visaGroup: country.visaGroup,
              }))
            );
          }
        }
      } catch (error) {
        console.error("Error fetching data:", error);
      }

      return finalData;
    };

    fetchData();
  }, []);

  return countries;
};

interface IEVisaConfig {
  image?: any;
  title?: string;
  selectCountryLabel?: string;
  selectCountryPlaceholder?: string;
  selectQuestionLabel?: string;
  selectQuestionPlaceholder?: string;
  eligibleForLabel?: string;
  visaTypesTitle?: string;
  informationsLabel?: string;
  requirementsLabel?: string;
  questionsByVisaGroup?: [];
  visaTypes?: [];
  viewMoreMobileLabel?:string;
}

export const useEVisaConfig = () => {
  const [config, setConfig] = useState<IEVisaConfig>({});

  useEffect(() => {
    const fetchData = async () => {
      let finalData: { code: number; message: string; response: any } = {
        code: 200,
        message: "empty",
        response: {},
      };

      try {
        const _res = await fetch(`${baseUrl}/bin/api/v1/evisa/config?locale=${getLanguage()}`);

        if (_res) {
          finalData = await _res.json();

          if (finalData.code === 200) {
            const questions = finalData.response.questionsByVisaGroup[0]?.questions;
            if (questions) {
              finalData.response.questionsByVisaGroup = questions.map((ques: any) => ({
                id: ques.code,
                title: ques.label,
              }));
            }
            setConfig(finalData.response);
          }
        }
      } catch (error) {
        console.error("Error fetching data:", error);
      }

      return finalData;
    };

    fetchData();
  }, []);

  return config;
};
