(function () {
    'use strict';

    angular
        .module('app')
        .factory('LoggerService', function () {
            return {
                request: function (config) {
                    //weed out loading of views - we just want service requests.
                    if (config.url.indexOf('html') == -1) {
                        console.log("HTTP " + config.method + " request: " + config.url);
                    }
                    return config;
                }
            };
        })
})();


