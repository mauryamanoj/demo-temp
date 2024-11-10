/* eslint-disable max-len */
import React, { FC, SVGProps, useEffect, useRef } from "react";
import api from "../../../../util/api";
export interface IconProps {
  name: string;
  svgClass?: string;
  wrapperClass?: string;
  subfolder?: string;
  isFetch?: boolean;
  isSuccess?: boolean;
  setIsSuccess?: (value: boolean) => void;
}


const Icon: FC<IconProps> = ({ name, svgClass, wrapperClass, subfolder, isFetch, isSuccess, setIsSuccess }): JSX.Element | null => {
  const ImportedIconRef = useRef<FC<SVGProps<SVGSVGElement>> | any>();
  const [loading, setLoading] = React.useState(false);
  useEffect((): void => {
    if (isFetch) {
      fetchIcon();
    } else {
      importIcon();
    }
  }, [name]);

  const isValidSVG = (data: string): boolean => {
    // Check if the data starts with "<svg" to validate it as an SVG
    return data.trim().startsWith('<svg');
  };

  const fetchIcon = async (): Promise<void> => {
    setLoading(true);
    try {
      const url = name;
      const { data } = await api.get({ url });

      if (!isValidSVG(data)) {
        setIsSuccess && setIsSuccess(false);
        return;
      }

      setIsSuccess && setIsSuccess(true);
      const SVGComponent = () => (
        <div dangerouslySetInnerHTML={{ __html: data }} />
      );

      ImportedIconRef.current = SVGComponent;
    } catch (err) {
      setIsSuccess && setIsSuccess(false);
    } finally {
      setLoading(false);
    }
  };

  const importIcon = async (): Promise<void> => {
    setLoading(true);
    try {
      const iconTxt = await import(`!!@svgr/webpack!/src/main/resources/svg/icon/${subfolder ? subfolder + '/' : ''}${name}.svg`);
      ImportedIconRef.current = (iconTxt).default;
    } catch (err) {
      const iconTxt = await import(`!!@svgr/webpack!/src/main/resources/svg/icon/error.svg`);
      if(process.env.NODE_ENV == 'development'){
        ImportedIconRef.current = (iconTxt).default;
        return;
      }
      throw err;
    } finally {
      setLoading(false);
    }
  };

  if (!loading && ImportedIconRef.current) {
    const { current: ImportedIcon } = ImportedIconRef;
    return (<span className={wrapperClass}><ImportedIcon className={svgClass} /></span>
    );
  }
  return null;
};
export default Icon;
