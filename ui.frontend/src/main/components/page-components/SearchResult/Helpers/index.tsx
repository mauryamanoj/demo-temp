import { getLanguage } from 'src/main/util/getLanguage';

//نتيجتان , نتيجة , نتائج
export function handelText(eventsText: string, count: number, resultCountLabel: string) {
    let eventsTextFix = eventsText;
    if (getLanguage() == 'ar') {

        if (count == 2) {
            eventsTextFix = "نتيجتان";
            return eventsTextFix;
        }

        if (count == 1) {
            eventsTextFix = 'نتيجة';
            return eventsTextFix;
        }

        if ((count > 1 && count < 10) || count == 10) {
            eventsTextFix = englishToArabic(count) + ' نتائج';
            return eventsTextFix;
        }

        if (count > 10) {
            eventsTextFix = englishToArabic(count) + ' نتيجة';
            return eventsTextFix;
        }
    }
    

    if (getLanguage() == 'en') {
        eventsTextFix = count == 1 ? count + " Result" : count + " Results"; // we need to add the keys here
        return eventsTextFix;
    }

    if (getLanguage() != 'en' && getLanguage() != 'ar') {
        eventsTextFix = count == 1 ? count + " " + resultCountLabel : count + " " + resultCountLabel;
        // we need to add the keys here
        return eventsTextFix;
    }

    return eventsTextFix;
}

export function getParameterByName(name: string, url = window.location.href) {
    name = name.replace(/[\[\]]/g, '\\$&');
    var regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)'),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, ' '));
}

export function stripHTML(myString: string) {
    var el: any = document.createElement("div");
    el.innerHTML = myString;
    return el.textContent || el.innerText || "";
}

// hide section under the no result section
export function visibleComponent(selecter: string, displayVal:string) {
    const ele: any = document.querySelector(selecter);
    ele ? ele.style.display = displayVal : null;
}

export function englishToArabic(number:number) {
    const arabicNumbers = ['٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩'];
    return String(number).replace(/\d/g, (digit:any) => arabicNumbers[digit]);
}
