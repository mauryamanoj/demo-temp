"use strict";

use(function () {
    var prefetchLinks;
    if(resource.getChild("prefetchLinks")!=null){
    	prefetchLinks = resource.getChild("prefetchLinks").listChildren();
    }
    return {
        prefetchLinks: prefetchLinks,
    };
});