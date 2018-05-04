(function () {
    'use strict';

    angular
		.module('app',[])
		.controller('newFriendCtrl', newFriendCtrl);

    newFriendCtrl.$inject = ['$scope', '$rootScope','$http',  '$window', 'service'];
    function newFriendCtrl($scope, $rootScope,$http, $window, service) {
    	service.getAccepted($window.localStorage.getItem("user"),
    			function(info){
    				$scope.acc=info.data;
    			},
    			function(){
    				
    			}
    	)
    	
    	service.getPccepted($window.localStorage.getItem("user"),
    			function(info){
    				$scope.acc1=info.data;
    			},
    			function(){
    				
    			}
    	)
    	$scope.addNew = function(){
    		service.newFriend(getDTO($scope.newUserName),
    				function(info){
    					alert("Friendship succesfuly sent.");
    				},
    				function(){
    					alert("Error sending. Probably wrong username.");
    				}
    		)
    	}
    	
    	$scope.accept = function(acc){
    		acc.status = "ACCEPTED";
    		service.accFriend(acc,
    				function(info){
    					alert("Friendship succesfuly sent.");
    					$window.location.reload();
    				},
    				function(){
    					alert("Error sending. Probably wrong username.");
    					$window.location.reload();
    				}
    		)
    	}
    	
    	$scope.addNewGroup = function(){
    		var partsOfStr = $scope.newGroupStr.split(',');
    		var usersDtoArr = [];
    		var temp = $scope.newGroupname;
    		for(var i=0;i<partsOfStr.length;i++){
    			usersDtoArr.push({"username":partsOfStr[i]});
    		}
    		usersDtoArr.push({"username":$window.localStorage.getItem("user")});
    		var dto = {
    				"name":temp,
    				"admin":usersDtoArr[0].username,
    				"users":usersDtoArr
    		}
    		console.log(JSON.stringify(dto));
    		service.newGroup(dto,
    				function(info){
    					alert("Uspelo i slanje i notifikacija")
    				},
    				function(){
    					alert("Neka notifikacija nije prosla.")
    				}
    		)
    		
    	}
    	
    	var getDTO = function(targetId){
    		return {
    	        "id":"0",

    	        "sender":$window.localStorage.getItem("user"),

    	    	"reciever":targetId,

    	    	"status":"PENDING"
    	    }
    	}
    	
    	$scope.getFriendName = function(acc){
    		if(acc.sender==$window.localStorage.getItem("user")){
    			return acc.reciever;
    		}else{
    			return acc.sender;
    		}
    	}
    }

})();