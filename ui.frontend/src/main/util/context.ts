import React from "react";

export const AnalyticsContext = React.createContext({
    header: ''
});

export const AnalyticsFavContext = React.createContext<any>({
    section: '',
    title: '',
    subTitle: '',
    destination: []
});
