(function () {
    'use strict';

    angular
		.module('app',[])
		.controller('allChatsController', allChatsController);

    allChatsController.$inject = ['$scope', '$rootScope','$http',  '$window'];
    function allChatsController($scope, $rootScope,$http, $window, service) {
  
    	$scope.sviPrijatelji =[];
    	$scope.sveGrupe = [];
    	$scope.isGrupa=false;
    	$scope.chat={};
    	
    	$scope.showNotifications=true;
    	$scope.showAllChats=true;
    	
    	$scope.notifications=[];
    	//$scope.notification.text="";
    	
    	$scope.skloniNotifikacije = function(){
    		$scope.showNotifications= !	$scope.showNotifications;
    	}
    	
    	var ws = new WebSocket("ws://localhost:8096/ChatApp/notification/"+$window.localStorage.getItem("user"));
        
        ws.onopen = function(){  
            console.log("Socket has been opened!");  
        };
        
        ws.onmessage = function(message) {
            listener(JSON.parse(message.data));
        };
        
        
        var wsChat = new WebSocket("ws://localhost:8096/ChatApp/chat/"+$window.localStorage.getItem("user"));
        
        wsChat.onopen = function(){  
            console.log("Chat socket has been opened!");  
        };
        
        wsChat.onmessage = function(message) {
            parseChat(JSON.parse(message.data));
        };

        
      

        function listener(data) {
          var messageObj = data;
          console.log("Received data from websocket: ", messageObj);
          var date = "";
          if(messageObj.type=="GROUPADD"){
        	  data+="Group ";
        	  service.getGroup(messageObj.groupId,
        			  function(info){
        		  	  		data+=info.data.name;
        		  	  		data+=" has been created.\n You have been added to that group."
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
        		  	  		data+=info.data.name;
        		  	  		data+=" has been removed.\n You have been removed from that group."
        	  		  },
        	  		  function(){
        	  			  alert("Error loading group name");
        	  		  }
        	  );
          }
          else if(messageObj.type=="LOGIN"){
        	  date+="Your friend "+messageObj.userId + " has just logged in!";
          }
          else if(messageObj.type=="LOGOUT"){
        	  date+="Your friend "+messageObj.userId + " has just logged out!";
          }
          else if(messageObj.type=="ACCEPTED"){
        	  date+="Your friend "+messageObj.userId + " has just accepted your friendship request!";
          }
          else if(messageObj.type=="REMOVED"){
        	  date+="Your friend "+messageObj.userId + " has just removed you from friends!";
          }
          else if(messageObj.type=="PENDING"){
        	  date+=""+messageObj.userId + " has just sent you a friendship request!";
          }
          else if(messageObj.type=="GROUPNEWUSER"){
        	  data+="User " +messageObj.userId+ " has been added to group ";
        	  service.getGroup(messageObj.groupId,
        			  function(info){
        		  	  		data+=info.data.name;
        	  		  },
        	  		  function(){
        	  			  alert("Error loading group name");
        	  		  }
        	  );
          }
          else if(messageObj.type=="GROUPREMOVEUSER"){
        	  data+="User " +messageObj.userId+ " from group ";
        	  service.getGroup(messageObj.groupId,
        			  function(info){
        		  	  		data+=info.data.name;
        		  	  		data+=" has been removed."
        	  		  },
        	  		  function(){
        	  			  alert("Error loading group name");
        	  		  }
        	  );
          }
          data =date; 
          $scope.notifications.push(data);  
          $scope.$apply($scope.notifications);
         
        }
        
        
    	
        $scope.otvoriChat = function(username){
       
        	
        	$scope.isGrupa = false;
        	
        	$scope.chat.id = username;
        	
        	
        	var user = $window.localStorage.getItem("user");
    		console.log(username);
    		 $http({
                 method: 'GET',
                 url: 'http://localhost:8096/ChatApp/rest/users/getMessages/user/'+user+'/from/N/'+ username 
               }).then(function successCallback(response) {
                   var poruke = response.data;
                   console.log(poruke);
                   $scope.chat.messages=poruke;
                   $scope.$apply($scope.chat);
                   
                   }, function errorCallback(response) {
                    $scope.message="Error.";

                   });

    		$scope.showAllChats=false;
    		
    		
    		
    	}
    	
    	
        $scope.seeAllChats = function(){
        	$scope.showAllChats=true;
        }
    	
    	$scope.otvoriChatGrupa = function(id){
    		

    		$scope.isGrupa = true;
    		
    		$scope.chat.id = id;
    		
    		
    	  	var user = $window.localStorage.getItem("user");
    		console.log(username);
    		 $http({
                 method: 'GET',
                 url: 'http://localhost:8096/ChatApp/rest/users/getMessages/user/'+user+'/from/Y/'+ id 
               }).then(function successCallback(response) {
            	   var poruke = response.data;
                   console.log(poruke);
                   $scope.chat.messages=poruke;
                   $scope.$apply($scope.chat);
                   
                   }, function errorCallback(response) {
                    $scope.message="Error.";

                   });
    		
    		
    		
    		
    		$scope.showAllChats=false;
    		
    	}
    	
    	
    	  function parseChat(data) {
              var messageObj = data;
              console.log("Received data from websocket: ", messageObj);
              
              $scope.chat.messages.push(data);
              $scope.$apply($scope.chat);
              
              
            }
          
          

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
    	
    	
    	$scope.sendMessage = function(komeId, newMessage, isGroup){
 
    		var user = $window.localStorage.getItem("user");
    		
    		
    		var data={};
    		
    		if(isGroup){
    			data=  {
            			"id" : 	"",
            			"sender" : user, 
            			"reciver" : "",
            			"content" : newMessage,
            			"groupId" : komeId
            		}
    		}else{
    			data=  {
            			"id" : 	"",
            			"sender" : user, 
            			"reciver" : komeId,
            			"content" : newMessage,
            			"groupId" : ""
            			
            		}
    		}
    		
    		
    		 var defer = $q.defer();
             var callbackId = getCallbackId();
             callbacks[callbackId] = {
               time: new Date(),
               cb:defer
             };
             request.callback_id = callbackId;
             console.log('Sending message', data);
             wsChat.send(JSON.stringify(data));
             
             $scope.chat.messages.push(data);
             $scope.$apply($scope.chat);
             
             
             
             return defer.promise;
    		
    		
    		
    		
    		
    	/*	var user = $window.localStorage.getItem("user");
    		
    		
    		var data={};
    		
    		if(isGroup){
    			data=  {
            			"id" : 	"",
            			"sender" : user, 
            			"reciver" : "",
            			"content" : newMessage,
            			"groupId" : komeId
            		}
    			
    			 $http({
                     method: 'POST',
                     url: 'http://localhost:8096/ChatApp/rest/users/forwardGroupMessage', 
                     data: data
                   }).then(function successCallback(response) {
                	   
                       $scope.chat.messages.push(data);
                       $scope.$apply($scope.chat);
                       
                       }, function errorCallback(response) {
                        $scope.message="Error.";

                       });
    			
    			
    			
    			
    			
    			
    		}else{
    			data=  {
            			"id" : 	"",
            			"sender" : user, 
            			"reciver" : komeId,
            			"content" : newMessage,
            			"groupId" : ""
            			
            		}
    			
    			 $http({
                     method: 'POST',
                     url: 'http://localhost:8096/ChatApp/rest/users/forwardMessage', 
                     data: data
                   }).then(function successCallback(response) {
                	   
                       $scope.chat.messages.push(data);
                       $scope.$apply($scope.chat);
                       
                       }, function errorCallback(response) {
                        $scope.message="Error.";

                       });
    			
    		}
    		*/

    	
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