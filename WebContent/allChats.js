(function () {
    'use strict';

    angular
		.module('app',['ngCookies'])
		.controller('allChatsController', allChatsController);

    allChatsController.$inject = ['$scope', '$rootScope','$http', '$cookies', '$window'];
    function allChatsController($scope, $rootScope,$http, $cookies, $window) {
  
    	$scope.sviPrijatelji =[];
    	$scope.sveGrupe = [];
    	$scope.otvoriChat = function(username){
    		
    	}
    	
    	$scope.otvoriChatGrupa = function(username){
    		
    	}
    	
    	$scope.getAllFriends= function(){
    		
    		
    		var username = $cookies.get("user");
    		console.log(username);
    		 $http({
                 method: 'GET',
                 url: 'http://localhost:8096/ChatApp/rest/users/getFriends/'+username
               }).then(function successCallback(response) {
                   var fren = response.data;
                   console.log(fren);
                   $scope.sviPrijatelji=fren;
                   
                   }, function errorCallback(response) {
                    $scope.message="Error.";

                   });
    		
    	}
    	$scope.getAllFriends();
    	
    	$scope.getAllGroups= function(){
    		
    		var username = $cookies.get("user");
    		console.log(username);
    		 $http({
                 method: 'GET',
                 url: 'http://localhost:8096/ChatApp/rest/users/getGroups/'+username
               }).then(function successCallback(response) {
                   var fren = response.data;
                   console.log(fren);
                   $scope.sveGrupe=fren;
                   
                   }, function errorCallback(response) {
                    $scope.message="Error.";

                   });
    		
    	}
    	
    	$scope.getAllGroups();
    
    	

    }

})();