(function () {
    'use strict';

    angular
        .module('app', ['ngRoute', 'ngCookies'])
        .config(config)
        .run(run);

    config.$inject = ['$routeProvider', '$locationProvider'];
    function config($routeProvider, $locationProvider) {
        $routeProvider
            .when('/', {
                controller: 'HomeController',
                templateUrl: 'js/home/home.view.html',
                //templateUrl: 'home',
                controllerAs: 'vm'
            })

            .when('/home', {
                controller: 'HomeController',
                templateUrl: 'js/home/home.view.html',
                //templateUrl: 'home',
                controllerAs: 'vm'
            })
            
            .when('/login', {
                controller: 'LoginController',
                //templateUrl: 'login',
                templateUrl: 'js/login/login.view.html',
                controllerAs: 'vm'
            })

            .when('/register', {
                controller: 'RegisterController',
                //templateUrl: 'register',
                templateUrl: 'js/register/register.view.html',
                controllerAs: 'vm'
            })

            .when('/interface-sales-record', {
                controller: 'InterfaceSalesRecordController',
                //templateUrl: 'register',
                templateUrl: 'js/interface-sales-record/interface-sales-record.view.html',
                controllerAs: 'vm'
            })
            
             .when('/interface-account', {
                controller: 'InterfaceAccountController',
                //templateUrl: 'register',
                templateUrl: 'js/interface-account/interface-account.view.html',
                controllerAs: 'vm'
            })
            
            .when('/interface-simplenet-node', {
                controller: 'InterfaceSimpleNetNodeController',
                //templateUrl: 'register',
                templateUrl: 'js/interface-simplenet-node/interface-simplenet-node.view.html',
                controllerAs: 'vm'
            })
            
             .when('/interface-simplenet-node/list', {
                controller: 'InterfaceSimpleNetNodeController',
                //templateUrl: 'register',
                templateUrl: 'js/interface-simplenet-node/list/simplenettreenode.html',
                controllerAs: 'vm'
            })
            
            .when('/interface-opvnet-node', {
                controller: 'InterfaceOpvNetNodeController',
                //templateUrl: 'register',
                templateUrl: 'js/interface-opvnet-node/interface-opvnet-node.view.html',
                controllerAs: 'vm'
            })
            
            .when('/interface-opvnet-node/list', {
                controller: 'InterfaceOpvNetNodeController',
                //templateUrl: 'register',
                templateUrl: 'js/interface-opvnet-node/list/opvnettreenode.html',
                controllerAs: 'vm'
            })
            
            .when('/interface-fivestarnet-node', {
                controller: 'InterfaceFiveStarNetNodeController',
                //templateUrl: 'register',
                templateUrl: 'js/interface-fivestarnet-node/interface-fivestarnet-node.view.html',
                controllerAs: 'vm'
            })
            
            .when('/interface-fivestarnet-node/list', {
                controller: 'InterfaceFiveStarNetNodeController',
                //templateUrl: 'register',
                templateUrl: 'js/interface-fivestarnet-node/list/fivestarnettreenode.html',
                controllerAs: 'vm'
            })
            
            .otherwise({ redirectTo: '/login' });
                
    }

    run.$inject = ['$rootScope', '$location', '$cookies', '$http'];
    function run($rootScope, $location, $cookies, $http) {
        // keep user logged in after page refresh
        $rootScope.globals = $cookies.getObject('globals') || {};
        if ($rootScope.globals.currentUser) {
            $http.defaults.headers.common['Authorization'] = 'Basic ' + $rootScope.globals.currentUser.authdata;
        }

        $rootScope.$on('$locationChangeStart', function (event, next, current) {
            // redirect to login page if not logged in and trying to access a restricted page
            var restrictedPage = $.inArray($location.path(), ['/login', '/register']) === -1;
            var loggedIn = $rootScope.globals.currentUser;
            if (restrictedPage && !loggedIn) {
                $location.path('/login');
            }
        });
    }

})();