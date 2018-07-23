(function () {
    'use strict';

    angular
        .module('app')
        .controller('LoginController', LoginController);

    LoginController.$inject = ['$location', 'AuthenticationService', 'FlashService'];
    function LoginController($location, AuthenticationService, FlashService) {
        var vm = this;

        vm.login = login;

        (function initController() {
            // reset login status
            AuthenticationService.ClearCredentials();
        })();

        function login() {
            vm.dataLoading = true;
        	// console.log( '### Before login');
            AuthenticationService.Login(vm.username, vm.password, function (response) {
            	// console.log( '### After login');
                if (response.success) {
                	console.log( '### Login successful');
                    AuthenticationService.SetCredentials(vm.username, vm.password);
                    $location.path('/home');
                } else {
                	console.log( '### Login fail');
                    FlashService.Error(response.message);
                	// console.log( '### response.meesage=' + response.message );
                    vm.dataLoading = false;
                    $location.path('/login');
                }
            });
        };
    }

})();
