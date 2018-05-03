(function () {
    'use strict';

    angular
		.module('app')
		.controller('registerController', registerController);

    registerController.$inject = ['$scope', '$rootScope','$http', '$cookies', '$window'];
    function registerController($scope, $rootScope,$http, $cookies, $window) {
  
    	
    	$scope.register = function(username, pass, firstName, lastName){
        	if(username===undefined || pass===undefined || username==="" || pass==="" || firstName==="" || lastName ===""){
        		$scope.message="Enter both username and password.";
        		return;
        	}
        	var data = {
        	    "username" : username,
        	    "password" :pass, 
        	    "firstName" : firstName, 
        	    "lastName" : lastName, 
        	    "hostIp" : ""
        	}
            $http({
              method: 'POST',
              url: 'http://localhost:8096/ChatApp/rest/register/',
              data: data
            }).then(function successCallback(response) {
                var user = response.data;

                
                }, function errorCallback(response) {
                 $scope.message="Error.";

                });

        }
    	

    }

})();