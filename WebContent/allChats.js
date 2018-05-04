(function () {
    'use strict';

    angular
		.module('app',[])
		.controller('allChatsController', allChatsController);

    allChatsController.$inject = ['$scope', '$rootScope','$http',  '$window', 'service'];
    function allChatsController($scope, $rootScope,$http, $window, service) {
  
    	$scope.sviPrijatelji =[];
    	$scope.sveGrupe = [];
    	$scope.otvoriChat = function(username){
    		
    	}
    	
    	$scope.notifications=[];
    	//$scope.notification.text="";
    	
    	
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
          var date = "";
          if(messageObj.type=="GROUPADD"){
        	  date+="Group ";
        	  service.getGroup(messageObj.groupId,
        			  function(info){
		        		  date+=info.data.name;
		        		  date+=" has been created.\n You have been added to that group.";
		        		  data = date; 
		                  $scope.notifications.push(data);  
		                  $scope.$apply($scope.notifications);
        	  		  },
        	  		  function(){
        	  			  alert("Error loading group name");
        	  		  }
        	  );
          }
          else if(messageObj.type=="GROUPREMOVE"){
        	  data+="Group ";
        	  service.getGroup(messageObj.groupId,
        			  function(info){
		        		  date+=info.data.name;
		        		  date+=" has been removed.\n You have been removed from that group.";
		        		  data = date; 
		                  $scope.notifications.push(data);  
		                  $scope.$apply($scope.notifications);
        	  		  },
        	  		  function(){
        	  			  alert("Error loading group name");
        	  		  }
        	  );
          }
          else if(messageObj.type=="LOGIN"){
        	  date+="Your friend "+messageObj.userId + " has just logged in!";
        	  data = date; 
              $scope.notifications.push(data);  
              $scope.$apply($scope.notifications);
          }
          else if(messageObj.type=="LOGOUT"){
        	  date+="Your friend "+messageObj.userId + " has just logged out!";
        	  data = date; 
              $scope.notifications.push(data);  
              $scope.$apply($scope.notifications);
          }
          else if(messageObj.type=="ACCEPTED"){
        	  date+="Your friend "+messageObj.userId + " has just accepted your friendship request!";
        	  data = date; 
              $scope.notifications.push(data);  
              $scope.$apply($scope.notifications);
          }
          else if(messageObj.type=="REMOVED"){
        	  date+="Your friend "+messageObj.userId + " has just removed you from friends!";
        	  data = date; 
              $scope.notifications.push(data);  
              $scope.$apply($scope.notifications);
          }
          else if(messageObj.type=="PENDING"){
        	  date+=""+messageObj.userId + " has just sent you a friendship request!";
        	  data = date; 
              $scope.notifications.push(data);  
              $scope.$apply($scope.notifications);
          }
          else if(messageObj.type=="GROUPNEWUSER"){
        	  date+="User " +messageObj.userId+ " has been added to group ";
        	  service.getGroup(messageObj.groupId,
        			  function(info){
        		  		date+=info.data.name;
        		  		data = date; 
        		          $scope.notifications.push(data);  
        		          $scope.$apply($scope.notifications);
        	  		  },
        	  		  function(){
        	  			  alert("Error loading group name");
        	  		  }
        	  );
          }
          else if(messageObj.type=="GROUPREMOVEUSER"){
        	  date+="User " +messageObj.userId+ " from group ";
        	  service.getGroup(messageObj.groupId,
        			  function(info){
		        		  date+=info.data.name;
		        		  date+=" has been removed."
		        			  data = date; 
		                  $scope.notifications.push(data);  
		                  $scope.$apply($scope.notifications);
        	  		  },
        	  		  function(){
        	  			  alert("Error loading group name");
        	  		  }
        	  );
          }
          
         
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