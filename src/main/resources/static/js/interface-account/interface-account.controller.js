(function () {
    'use strict';

    angular
        .module('app')
        .controller('InterfaceAccountController', InterfaceAccountController);

    InterfaceAccountController.$inject = ['InterfaceAccountService', '$rootScope', '$location', 'FlashService'];
    function InterfaceAccountController(InterfaceAccountService, $rootScope, $location, FlashService) {
        var vm = this;

        vm.interfaceAccount = null;
        vm.allInterfaceAccounts = [];
        vm.deleteInterfaceAccount = deleteInterfaceAccount;

        initController();

        function initController() {
            loadAllInterfaceAccounts();
        }

        function loadAllInterfaceAccounts() {
        	vm.dataLoading = true;
        	InterfaceAccountService.GetAll()
            	.then(	function (response) {
		            	// console.log( '### After login');
		                if (response.success) {
		                    console.log('InterfaceAccountController, InterfaceAccountService.GetAll response='+ response);
		                    vm.allInterfaceAccounts = response.message;
		                } else {
		                    FlashService.Error(response.message);
		                	// console.log( '### response.meesage=' + response.message );
		                    vm.dataLoading = false;
		                    $location.path('/login');
		                }
		            }        			
        	);
        }

        function createInterfaceAccount() {
            vm.dataLoading = true;
            InterfaceAccountService.Create(vm.interfaceAccount)
                .then(function (response) {
                    if (response.success) {
                        FlashService.Success('Create InterfaceAccount successful', true);
                        $location.path('/login');
                    } else {
                        FlashService.Error(response.message);
                        vm.dataLoading = false;
                    }
                });
        }
        
        
        function deleteInterfaceAccount(id) {
        	InterfaceAccountService.Delete(id)
            	.then( function (response) {
		            	// console.log( '### After login');
		                if (response.success) {
		                    console.log('InterfaceAccountController, InterfaceAccountService.Delete response='+ response);
		                    vm.interfaceAccount = response.message;
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