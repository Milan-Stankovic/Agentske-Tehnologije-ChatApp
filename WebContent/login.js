(function () {
    'use strict';

    angular
		.module('app')
		.controller('loginController', loginController);

    loginController.$inject = ['$scope', '$rootScope','$http', '$cookies', '$window'];
    function loginController($scope, $rootScope,$http, $cookies, $window) {
  
    	$scope.login = function(username, pass){
        	if(username===undefined || pass===undefined || username==="" || pass===""){
        		$scope.message="Enter both username and password.";
        		return;
        	}
        	var data =pass;
            $http({
              method: 'POST',
              url: 'http://localhost:8096/ChatApp/rest/login/'+username,
              data: data
            }).then(function successCallback(response) {
                var user = response.data;

                		
	     			$cookies.put("user", user.username, {
	     			   path: 'core' //E ovo nemam pojma sta je
	     			});
	     			console.log("Uspesno logovanje: " + $cookies.get('user'))
	            	$window.location.href = 'http://localhost:8096/';
                
                
                }, function errorCallback(response) {
                 $scope.message="Error.";

                });

        }
    	
    	$scope.logout = function(username){
        	if(username===undefined || username===""){
        		$scope.message="No user";
        		return;
        	}
            $http({
              method: 'POST',
              url: 'http://localhost:8096/ChatApp/rest/logout/'+username
            }).then(function successCallback(response) {
                var user = response.data;

                
                $cookies.remove("user");
                
               
	            	$window.location.href = 'http://localhost:8096/';
                
                
                }, function errorCallback(response) {
                 $scope.message="Error.";

                });

        }
    	
    	
    	
    	
    	

    }

})();