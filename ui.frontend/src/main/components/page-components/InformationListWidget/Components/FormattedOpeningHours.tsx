import React from "react";
import Text from "../../../common/atoms/Text/Text";
import { OpeningHour } from "../IInformationListWidget";
import { getLanguage } from "src/main/util/getLanguage";
import dayjs from "dayjs";

export const FormatOpeningHours = (
    openingHoursValue: OpeningHour[],
    toLabel: string,
    sameTimeAcrossWeek: boolean
): React.ReactNode => {
    const lang = getLanguage();

    const daysOfWeek = ["sun", "mon", "tue", "wed", "thu", "fri", "sat"];
    const translations: Record<string, Record<string, string>> = {
        en: {
            sun: "Sunday",
            mon: "Monday",
            tue: "Tuesday",
            wed: "Wednesday",
            thu: "Thursday",
            fri: "Friday",
            sat: "Saturday",
        },
        ar: {
            sun: "الأحد",
            mon: "الاثنين",
            tue: "الثلاثاء",
            wed: "الأربعاء",
            thu: "الخميس",
            fri: "الجمعة",
            sat: "السبت",
        },
        de: {
            sun: "Sonntag",
            mon: "Montag",
            tue: "Dienstag",
            wed: "Mittwoch",
            thu: "Donnerstag",
            fri: "Freitag",
            sat: "Samstag",
        },
        es: {
            sun: "domingo",
            mon: "lunes",
            tue: "martes",
            wed: "miércoles",
            thu: "jueves",
            fri: "viernes",
            sat: "sábado",
        },
        fr: {
            sun: "dimanche",
            mon: "lundi",
            tue: "mardi",
            wed: "mercredi",
            thu: "jeudi",
            fri: "vendredi",
            sat: "samedi",
        },
        ja: {
            sun: "日曜日",
            mon: "月曜日",
            tue: "火曜日",
            wed: "水曜日",
            thu: "木曜日",
            fri: "金曜日",
            sat: "土曜日",
        },
        ru: {
            sun: "воскресенье",
            mon: "понедельник",
            tue: "вторник",
            wed: "среда",
            thu: "четверг",
            fri: "пятница",
            sat: "суббота",
        },
        zh: {
            sun: "星期日",
            mon: "星期一",
            tue: "星期二",
            wed: "星期三",
            thu: "星期四",
            fri: "星期五",
            sat: "星期六",
        },
    };

    if (!openingHoursValue || openingHoursValue.length === 0) {
        return null;
    }

    // Function to group dates by timing
    const groupDatesByTiming = (dates: OpeningHour[]) => {
        const groupedDates: OpeningHour[][] = [];


        const prevDate = dates[0]
        let prevTimingKey = `${formatTime(prevDate.startTimeLabel)} ${toLabel} ${formatTime(prevDate.endTimeLabel)}`;
        let subGroupDates = [prevDate];
        for (let i = 1; i < dates.length; i++) {
            const date = dates[i];
            const timingKey = `${formatTime(date.startTimeLabel)} ${toLabel} ${formatTime(date.endTimeLabel)}`;

            if (prevTimingKey === timingKey) {
                subGroupDates.push(date)
            } else {
                groupedDates.push(subGroupDates)
                subGroupDates = [date]
            }

            prevTimingKey = timingKey
        }
        groupedDates.push(subGroupDates);

        return groupedDates;
    };

    // Function to format time
    const formatTime = (time: string) => {
        const [hours, minutes] = time.split(":");
        const formattedHours = parseInt(hours);
        let period;

        if (lang === "ar") {
            period = formattedHours < 12 ? "صباحا" : "مساء";
        } else {
            period = formattedHours < 12 ? "AM" : "PM";
        }

        return `${formattedHours % 12 || 12}:${minutes} ${period}`;
    };

    // Function to slice word based on language
    const sliceWord = (word: string) => {
        if (lang === "en") {
            return word.toLocaleLowerCase();
        } else {
            return translateDayLabel(word);
        }
    };

    const getDayIndex = (day: string): number => {
        return daysOfWeek.indexOf(day.toLowerCase());
    };

    // Function to translate day label
    const translateDayLabel = (dayLabel: string) => {
        const dayIndex = getDayIndex(dayLabel);
        const langTranslations = translations[lang];

        if (langTranslations) {
            const dayTranslation = langTranslations[dayLabel.toLowerCase()];
            return dayTranslation || dayLabel;
        }

        return dayLabel;
    };

    // Function to check if days are consecutive
    const areConsecutiveDays = (days: string[]) => {
        const sortedDays = days.sort((a, b) => getDayIndex(a) - getDayIndex(b));

        for (let i = 1; i < sortedDays.length; i++) {
            if (
                getDayIndex(sortedDays[i]) !==
                getDayIndex(sortedDays[i - 1]) + 1
            ) {
                return false;
            }
        }

        return true;
    };

    const generateGroupedDays = (group: any) => {
        const separator = lang === 'ar' ? <span className='font-sans'>، </span> : <span>, </span>;
        const lastIndex = group.length - 1;
        const toLabelTxt = toLabel ? ` ${toLabel} ` : separator;
        let groupLabel = group.map((date: any, index: number) =>
            index < lastIndex ? sliceWord(date.dayLabel) + separator : null
        );
        // sunday, tuesday and wednesday -> the last item is seperated by the toLabel prop
        groupLabel = groupLabel + toLabelTxt + sliceWord(group[lastIndex].dayLabel)
        return (
            <>{
                group.map((date: any, index: number) =>
                    <React.Fragment>
                        {index < lastIndex ? sliceWord(date.dayLabel) : null}
                        {index < lastIndex - 1 && separator}
                    </React.Fragment>

                )}
                <React.Fragment>
                    {toLabelTxt}
                    {sliceWord(group[lastIndex].dayLabel)}
                </React.Fragment>
            </>
        );
    }
    const formattedGroups = groupDatesByTiming(openingHoursValue)?.map(
        (group, index) => {
            const consecutiveDays = areConsecutiveDays(
                group.map((date) => date.dayLabel)
            );

            const commonClass =
                index < groupDatesByTiming(openingHoursValue).length - 1
                    ? "flex items-center py-2"
                    : "flex items-center pt-2";

            if (!!sameTimeAcrossWeek) {
                return (
                    <div key={index} className={commonClass}>
                        <Text
                            text={`${translateDayLabel(
                                openingHoursValue?.[0]?.dayLabel
                            )}: `}
                            styles="font-primary-regular text-sm md:text-base capitalize"
                        />
                        <Text
                            text={
                                <>
                                    <span>
                                        {formatTime(group[0].startTimeLabel)}
                                    </span>
                                    <span className=" px-1"> {toLabel} </span>
                                    <span>
                                        {formatTime(group[0].endTimeLabel)}
                                    </span>
                                </>
                            }
                            styles="font-primary-regular text-sm md:text-base  flex flex-row  text-[#4B4B4B] ltr:pl-1 rtl:pr-1"
                        />
                    </div>
                );
            } else if (consecutiveDays === true) {
                const startDay = group[0].dayLabel;
                const endDay = group[group.length - 1].dayLabel;
                const startTime = formatTime(group[0].startTimeLabel);
                const endTime = formatTime(group[0].endTimeLabel);

                return (
                    <div key={index} className={commonClass}>
                        <Text
                            text={`${startDay === endDay
                                ? sliceWord(startDay)
                                : sliceWord(startDay) +
                                " " +
                                toLabel +
                                " " +
                                sliceWord(endDay)
                                }: `}
                            styles="font-primary-regular text-sm md:text-base capitalize"
                        />
                        <Text
                            text={
                                <>
                                    <span>{startTime} </span>
                                    <span className=" px-1"> {toLabel} </span>
                                    <span>{endTime}</span>
                                </>
                            }
                            styles="font-primary-regular text-sm md:text-base  flex flex-row text-[#4B4B4B] ltr:pl-1 rtl:pr-1"
                        />
                    </div>
                );
            } else {
                return (
                    <div key={index} className={commonClass}>
                        <Text text={generateGroupedDays(group)}
                            styles="font-primary-regular text-sm md:text-base capitalize"
                        />
                        <Text
                            text={
                                <>
                                    <span>
                                        {formatTime(group[0].startTimeLabel)}
                                    </span>
                                    <span className=" px-1"> {toLabel} </span>
                                    <span>
                                        {formatTime(group[0].endTimeLabel)}
                                    </span>
                                </>
                            }
                            styles="font-primary-regular text-sm md:text-base  flex flex-row text-[#4B4B4B] ltr:pl-1 rtl:pr-1"
                        />
                    </div>
                );
            }
        }
    );

    return <>{formattedGroups}</>;
};

// Function to check if an event is in progress
export const isEventInProgress = (
    openingHoursValue: OpeningHour[],
    sameTimeAcrossWeek: boolean,
    isExpired: boolean
): boolean => {
    if (!openingHoursValue || openingHoursValue.length === 0 || isExpired) {
        return false;
    }

    const saudiCurrentTime = new Date().toLocaleString("en-US", { timeZone: "Asia/Riyadh", });
    //const saudiCurrentTime = '5/14/2024, 02:00:00 AM'
    let saudiCurrentTimeFormatted = dayjs(saudiCurrentTime).add(1, 'milliseconds')
    const currentDate = new Date().toLocaleDateString("en-US");

    let currentOpeningHours;
    let dayIndex = 0;
    if (sameTimeAcrossWeek) {
        currentOpeningHours = openingHoursValue[0];
    } else {
        const dayOfWeek = saudiCurrentTimeFormatted.locale('en').format('dddd');
        const currentDay = dayOfWeek.slice(0, 3).toLowerCase();
        currentOpeningHours = openingHoursValue.find((date, index) => {
            if (date.dayLabel.toLowerCase() === currentDay) {
                dayIndex = index;
                return date.dayLabel.toLowerCase() === currentDay
            }
        });


    }

    if (currentOpeningHours) {
        if (currentOpeningHours.endTimeLabel.startsWith("00:00")) currentOpeningHours.endTimeLabel = "23:59:59"
        let startTime = dayjs(`${currentDate} ${currentOpeningHours.startTimeLabel}`)
        let endTime = dayjs(`${currentDate} ${currentOpeningHours.endTimeLabel}`)

        //if the event extends for 2 days span

        if (dayjs(endTime, 'HH:mm').hour() < 12 && endTime.isBefore(startTime) && saudiCurrentTimeFormatted.isBefore(startTime)) {
            return saudiCurrentTimeFormatted.isBefore(endTime);
        }




        if (!saudiCurrentTimeFormatted.isAfter(startTime) || !saudiCurrentTimeFormatted.isBefore(endTime)) {
            // if current time is exceeded midnihgt then check if it accuring in the last day

            if (dayjs(saudiCurrentTimeFormatted, 'HH:mm').hour() < 12 && saudiCurrentTimeFormatted.isBefore(endTime)) {
                const currentDate = new Date();
                currentDate.setDate(currentDate.getDate() - 1);
                const previousDate = currentDate.toLocaleDateString("en-US");

                if (openingHoursValue[dayIndex - 1]) {
                    startTime = dayjs(`${previousDate} ${openingHoursValue[dayIndex - 1].startTimeLabel}`);
                    endTime = dayjs(`${previousDate} ${openingHoursValue[dayIndex - 1].endTimeLabel}`);

                    if (dayjs(endTime, 'HH:mm').hour() < 12) {
                        startTime = startTime.subtract(1, 'day');
                    }
                    saudiCurrentTimeFormatted = dayjs(saudiCurrentTime).subtract(1, 'day').add(1, 'milliseconds')
                }
            }
        }

        if (startTime.isAfter(endTime)) {
            endTime = endTime.add(1, 'day');
        }
        return saudiCurrentTimeFormatted.isAfter(startTime) && saudiCurrentTimeFormatted.isBefore(endTime);
    }


    return false;
};