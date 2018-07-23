(function () {
    'use strict';

    angular
        .module('app')
        .factory('UserService', UserService);

    UserService.$inject = ['$http'];
    function UserService($http) {
        var service = {};

        service.GetAll = GetAll;
        service.GetById = GetById;
        service.GetByUsername = GetByUsername;
        service.Create = Create;
        service.Update = Update;
        service.Delete = Delete;
        
        return service;

        function GetAll() {
        	var responseData = $http.get('api/user/listAll').then(handleSuccess, handleError('Error getting all users'));
        	// console.log( responseData );
            return responseData;
        }

        function GetById(id) {
            return $http.get('api/user/' + id).then(handleSuccess, handleError('Error getting user by id'));
        }

        function GetByUsername(username) {
        	// console.log('GetByUsername=['+ username + ']');
            return $http.get('api/user/get/' + username).then(handleSuccess, handleError('Error getting user by username'));
        }

        function Create(user) {
            return $http.post('api/user', user).then(handleSuccess, handleError('Error creating user'));
        }

        function Update(user) {
            return $http.put('api/user/' + user.id, user).then(handleSuccess, handleError('Error updating user'));
        }

        function Delete(id) {
            return $http.delete('api/user/' + id).then(handleSuccess, handleError('Error deleting user'));
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
    }

})();
