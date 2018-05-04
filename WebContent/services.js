angular.module('app')
.service('service', function ($http) {
    return {
    	getActors: function (groupId, onSuccess, onError) {
            var req = {
                method: 'GET',
                url: 'http://localhost:8096/ChatApp/rest/users/group/'+groupId
            };
            $http(req).then(onSuccess, onError);
        }
    }
});