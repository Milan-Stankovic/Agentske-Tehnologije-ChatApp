(function () {
    'use strict';

    angular
		.module('app',[])
		.controller('allChatsController', allChatsController);

    allChatsController.$inject = ['$scope', '$rootScope','$http',  '$window'];
    function allChatsController($scope, $rootScope,$http, $window) {
  
    	$scope.sviPrijatelji =[];
    	$scope.sveGrupe = [];
    	$scope.otvoriChat = function(username){
    		
    	}
    	
    	$scope.notifications=[];
    	$scope.notification.text="";
    	
    	
    	var ws = new WebSocket("ws://localhost:8096/ChatApp/notification/"+$window.localStorage.getItem("user"));
        
        ws.onopen = function(){  
            console.log("Socket has been opened!");  
        };
        
        ws.onmessage = function(message) {
            listener(JSON.parse(message.data));
        };

        function sendRequest(request) {
          var defer = $q.defer();
          var callbackId = getCallbackId();
          callbacks[callbackId] = {
            time: new Date(),
            cb:defer
          };
          request.callback_id = callbackId;
          console.log('Sending request', request);
          ws.send(JSON.stringify(request));
          return defer.promise;
        }

        function listener(data) {
          var messageObj = data;
          console.log("Received data from websocket: ", messageObj);
          
          var notification ={};
          notification.tekst = messageObj;// OVDE MI ISPARSIRAJ DA NOTIFICATION TEXT LICI NA NESTO :D
          $scope.notifications.push(messageObj);
         
        }
    	
    	
    	
    	
    	
    	$scope.otvoriChatGrupa = function(username){
    		
    	}
    	
    	$scope.getAllFriends= function(){
    		
    		
    		var username = $window.localStorage.getItem("user");
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
    		
    		var username = $window.localStorage.getItem("user");
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