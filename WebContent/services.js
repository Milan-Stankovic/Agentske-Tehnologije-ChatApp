angular.module('app')
.service('service', function ($http) {
    return {
    	getGroup: function (groupId, onSuccess, onError) {
            var req = {
                method: 'GET',
                url: 'http://localhost:8096/ChatApp/rest/users/group/'+groupId
            };
            $http(req).then(onSuccess, onError);
        },
    	
		newFriend: function (dto, onSuccess, onError) {
            var req = {
                method: 'POST',
                url: 'http://localhost:8096/ChatApp/rest/proxy/newFriendship',
                data: dto
            };
            $http(req).then(onSuccess, onError);
        }	
    }
});