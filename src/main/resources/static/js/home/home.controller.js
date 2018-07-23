(function () {
    'use strict';

    angular
        .module('app')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['UserService', '$rootScope', '$location', 'FlashService'];
    function HomeController(UserService, $rootScope, $location, FlashService) {
        var vm = this;

        vm.user = null;
        vm.allUsers = [];
        vm.deleteUser = deleteUser;

        initController();

        function initController() {
            loadCurrentUser();
            loadAllUsers();
        }

        function loadCurrentUser() {
        	/*
            UserService.GetByUsername($rootScope.globals.currentUser.username)
                .then(function (user) {
                    vm.user = user;
                }, handleError('Error loadCurrentUser in HomeController'));
            */
        	UserService.GetByUsername($rootScope.globals.currentUser.username)
        		.then(function (response) {
		            	// console.log( '### After login');
		                if (response.success) {
		                    console.log('HomeController, UserService.GetByUsername response='+ response);
		                    vm.user = response.message;
		                } else {
		                    FlashService.Error(response.message);
		                	// console.log( '### response.meesage=' + response.message );
		                    $location.path('/login');
		                }
		            }        			
        	);
        }

        function loadAllUsers() {
        	vm.dataLoading = true;
        	/*
            UserService.GetAll()
                .then(function (users) {
                    vm.allUsers = users;
                }, handleError('Error loadAllUsers in HomeController'));
            */
            UserService.GetAll()
            	.then(	function (response) {
		            	// console.log( '### After login');
		                if (response.success) {
		                    console.log('HomeController, UserService.GetAll response='+ response);
		                    vm.allUsers = response.message;
		                } else {
		                    FlashService.Error(response.message);
		                	// console.log( '### response.meesage=' + response.message );
		                    vm.dataLoading = false;
		                    $location.path('/login');
		                }
		            }        			
        	);
        }

        function deleteUser(id) {
        	/*
            UserService.Delete(id)
            .then(function () {
                loadAllUsers();
            });
            */
            UserService.Delete(id)
            	.then( function (response) {
		            	// console.log( '### After login');
		                if (response.success) {
		                    console.log('HomeController, UserService.Delete response='+ response);
		                    vm.user = response.message;
		                } else {
		                    FlashService.Error(response.message);
		                	// console.log( '### response.meesage=' + response.message );
		                    $location.path('/login');
		                }
		            }        			
        	);
        }
    }

})();