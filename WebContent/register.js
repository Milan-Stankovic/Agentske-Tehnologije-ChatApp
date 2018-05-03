(function () {
    'use strict';

    angular
    	.module('app',[])
		.controller('registerController', registerController);

    registerController.$inject = ['$scope', '$rootScope','$http', '$window'];
    function registerController($scope, $rootScope,$http, $window) {
  
    	
    	$scope.register = function(username, pass, firstName, lastName){
        	if(username===undefined || pass===undefined || username==="" || pass==="" || firstName==="" || lastName ===""){
        		$scope.message="Enter both username and password.";
        		return;
        	}
        	var data = {
        	    "username" : username,
        	    "password" :pass, 
        	    "name" : firstName, 
        	    "lastName" : lastName, 
        	    "hostIp" : ""
        	}
        	console.log(data);
            $http({
              method: 'POST',
              url: 'http://localhost:8096/ChatApp/rest/users/register',
              data: data
            }).then(function successCallback(response) {
                var user = response.data;
                console.log(user);
                console.log("HELOOO ! ");
                
                }, function errorCallback(response) {
                 $scope.message="Error.";

                });

        }
    	

    }

})();