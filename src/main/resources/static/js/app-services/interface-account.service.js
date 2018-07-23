(function () {
    'use strict';

    angular
        .module('app')
        .factory('InterfaceAccountService', InterfaceAccountService);

    InterfaceAccountService.$inject = ['$http'];
    function InterfaceAccountService($http) {
        var service = {};

        // service.GetAll = GetAll;
        service.GetAll = GetInterfaceAccounts(); //get the data from server side
        service.GetById = GetById;
        service.Create = Create;
        service.Update = Update;
        service.Delete = Delete;
        service.CreateBatchInterfaceAccounts = CreateBatchInterfaceAccounts;
        service.ConfirmBatchInterfaceAccounts = ConfirmBatchInterfaceAccounts;
        
        return service;

        function GetAll() {
        	var responseData = $http.get('api/interfaceAccount/listPendingAccounts').then(handleSuccess, handleError('Error getting all pending interface accounts'));
        	// console.log( responseData );
            return responseData;
        }

        function GetById(id) {
            return $http.get('api/interfaceAccount/' + id).then(handleSuccess, handleError('Error getting user by id'));
        }

        function Create(interfaceAccount) {
            return $http.post('api/interfaceAccount', interfaceAccount).then(handleSuccess, handleError('Error creating interfaceAccount'));
        }

        function Update(interfaceAccount) {
            return $http.put('api/interfaceAccount/' + user.id, user).then(handleSuccess, handleError('Error updating interfaceAccount'));
        }

        function Delete(id) {
            return $http.delete('api/interfaceAccount/' + id).then(handleSuccess, handleError('Error deleting interfaceAccount'));
        }

        function CreateBatchInterfaceAccounts( interfaceAccounts ) {        	
            return $http.post('api/interfaceAccount/createBatch', interfaceAccounts).then(handleSuccess, handleError('Error creating batch interface accounts'));
        }

        function ConfirmBatchInterfaceAccounts() {        	
            return $http.post('api/interfaceAccount/confirmBatch').then(handleSuccess, handleError('Error confirming interface accounts'));
        }

        
        // private functions

        function handleSuccess(response ) {
            return { success: true, message: response.data } ;
            // return res.data;
        }

        function handleError(error) {
            return function () {
               return { success: false, message: error };
            };
        }
        
        // hardcode json string for testing
        // private functions

        function GetInterfaceAccounts() {
        	/*
            if(!localStorage.interfaceAccounts){
                localStorage.interfaceAccounts = JSON.stringify([]);
            }

            return JSON.parse(localStorage.interfaceAccounts);
            */
            return this.http.request('./data/interface-accounts.json')
                             .map(res => res.json());
        }

        function setInterfaceAccounts(interfaceAccounts) {
            localStorage.interfaceAccounts = JSON.stringify(interfaceAccounts);
        }
        
        
        
    }

})();
