import React from "react";
import dayjs from "dayjs";

interface DateCardProps {
  from: string;
  to?: string;
  className?: string;
}

const dayCss = "text-xl md:text-[32px]";
const monthCss = "uppercase text-base md:text-lg rtl:md:text-base";
const yearCss = "text-base md:text-lg leading-5";

const DateCard = ({ from, to, className = "" }: DateCardProps) => {
  const dateFrom = dayjs(from);
  const dateTo = to && from !== to ? dayjs(to) : undefined;

  return (
    <div className={`w-[62px] text-white text-center child:bg-black/50 child:p-1.5 ${className}`}>
      <div className={`rounded-ss-[20px] leading-6 md:leading-8 ${dateTo ? "" : "rounded-ee-[20px]"}`}>
        <div className={dayCss}>{dateFrom.format("DD")}</div>
        <div className={monthCss}>{dateFrom.format("MMM")}</div>
        <div className={yearCss}>{dateFrom.year()}</div>
      </div>

      {dateTo ? (
        <div className={`rounded-ee-[20px] leading-6 md:leading-8 border-t-[1px] border-gray-50/40`}>
          <div className={dayCss}>{dateTo.format("DD")}</div>
          <div className={monthCss}>{dateTo.format("MMM")}</div>
          <div className={yearCss}>{dateTo.year()}</div>
        </div>
      ) : (
        <></>
      )}
    </div>
  );
};

export default DateCard;
