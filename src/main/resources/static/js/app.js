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

            .when('/interface-gpvnet-node', {
                controller: 'InterfaceGpvNetNodeController',
                //templateUrl: 'register',
                templateUrl: 'js/interface-gpvnet-node/interface-gpvnet-node.view.html',
                controllerAs: 'vm'
            })

            .when('/interface-gpvnet-node/list', {
                controller: 'InterfaceGpvNetNodeController',
                //templateUrl: 'register',
                templateUrl: 'js/interface-gpvnet-node/list/gpvnettreenode.html',
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

            .when('/interface-passupgpvnet-node', {
                controller: 'InterfacePassUpGpvNetNodeController',
                //templateUrl: 'register',
                templateUrl: 'js/interface-passupgpvnet-node/interface-passupgpvnet-node.view.html',
                controllerAs: 'vm'
            })

            .when('/interface-passupgpvnet-node/list', {
                controller: 'InterfacePassUpGpvNetNodeController',
                //templateUrl: 'register',
                templateUrl: 'js/interface-passupgpvnet-node/list/passupgpvnettreenode.html',
                controllerAs: 'vm'
            })

            .when('/interface-qualifiedfivestarnet-node', {
                controller: 'InterfaceQualifiedFiveStarNetTreeNode',
                //templateUrl: 'register',
                templateUrl: 'js/interface-qualifiedfivestarnet-node/interface-qualifiedfivestarnet-node.view.html',
                controllerAs: 'vm'
            })

            .when('/interface-qualifiedfivestarnet-node/list', {
                controller: 'InterfaceQualifiedFiveStarNetTreeNode',
                //templateUrl: 'register',
                templateUrl: 'js/interface-qualifiedfivestarnet-node/list/qualifiedfivestarnettreenode.html',
                controllerAs: 'vm'
            })

            .when('/interface-golddiamondnet-node', {
                controller: 'InterfaceGoldDiamondNetTreeNode',
                //templateUrl: 'register',
                templateUrl: 'js/interface-golddiamondnet-node/interface-golddiamondnet-node.view.html',
                controllerAs: 'vm'
            })

            .when('/interface-golddiamondnet-node/list', {
                controller: 'InterfaceGoldDiamondNetTreeNode',
                //templateUrl: 'register',
                templateUrl: 'js/interface-golddiamondnet-node/list/golddiamondnettreenode.html',
                controllerAs: 'vm'
            })
            
            .when('/interface-activenet-node', {
	            controller: 'InterfaceActiveNetNodeController',
	            //templateUrl: 'register',
	            templateUrl: 'js/interface-activenet-node/interface-activenet-node.view.html',
	            controllerAs: 'vm'
            })
            
            .when('/interface-activenet-node/list', {
                controller: 'InterfaceActiveNetNodeController',
                //templateUrl: 'register',
                templateUrl: 'js/interface-activenet-node/list/activenettreenode.html',
                controllerAs: 'vm'
            })

            .when('/interface-customernet-node', {
                controller: 'InterfaceCustomerNetNodeController',
                //templateUrl: 'register',
                templateUrl: 'js/interface-customernet-node/interface-customernet-node.view.html',
                controllerAs: 'vm'
            })

            .when('/interface-customernet-node/list', {
                controller: 'InterfaceCustomerNetNodeController',
                //templateUrl: 'register',
                templateUrl: 'js/interface-customernet-node/list/customernettreenode.html',
                controllerAs: 'vm'
            })

            .when('/interface-distributorbonus-node', {
                controller: 'InterfaceDistributorBonusNetNodeController',
                //templateUrl: 'register',
                templateUrl: 'js/interface-distributorbonus-node/interface-distributorbonus-node.view.html',
                controllerAs: 'vm'
            })

            .when('/interface-distributorbonus-node/list', {
                controller: 'InterfaceDistributorBonusNetNodeController',
                //templateUrl: 'register',
                templateUrl: 'js/interface-distributorbonus-node/list/distributorbonus.html',
                controllerAs: 'vm'
            })

            .when('/interface-distributordifferential-node', {
                controller: 'InterfaceDistributorDifferentialBonusNetController',
                //templateUrl: 'register',
                templateUrl: 'js/interface-distributordifferential-node/interface-distributordifferential-node.view.html',
                controllerAs: 'vm'
            })

            .when('/interface-distributordifferential-node/list', {
                controller: 'InterfaceDistributorDifferentialBonusNetController',
                //templateUrl: 'register',
                templateUrl: 'js/interface-distributordifferential-node/list/distributordifferential.html',
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