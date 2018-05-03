(function () {
    'use strict';

    angular
		.module('app',[])
		.controller('loginController', loginController);

    loginController.$inject = ['$scope', '$rootScope','$http', '$window'];
    function loginController($scope, $rootScope,$http, $window) {
  
    	$scope.login = function(username, pass){
        	if(username===undefined || pass===undefined || username==="" || pass===""){
        		$scope.message="Enter both username and password.";
        		return;
        	}
        	var data ={
        			"username" : "", 
        			"password":pass
        			
        	}
            $http({
              method: 'POST',
              url: 'http://localhost:8096/ChatApp/rest/users/login/'+username,
              data: data
            }).then(function successCallback(response) {
                var user = response.data;

                		
                	$window.localStorage.setItem("user",username);
	     			console.log("Uspesno logovanje: " + $window.localStorage.getItem("user"))
	     			console.log(user);
	            	$window.location.href = 'http://localhost:8096/ChatApp/allChats.html';
                
                
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

                
                $window.localStorage.removeItem("user",username);
                
               
	            	$window.location.href = 'http://localhost:8096/';
                
                
                }, function errorCallback(response) {
                 $scope.message="Error.";

                });

        }
    	
    	
    	
    	
    	

    }

})();