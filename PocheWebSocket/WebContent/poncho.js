//This file register into WebSocket for votation

var ip_server = "10.10.1.1";
var port = 8080;
var poncho = angular.module('poncho',[]);
var register = $("#register");
var votation = $("#votation");
var ws = new WebSocket('ws://'+ip_server+':'+port+'/PocheWebSocket/ponchito');

poncho.controller("loginController", function($scope, $http) {
	$scope.register = function(person) {
		console.log(person);
		if(person != "") { 
			$http.get('ws://'+ip_server+':'+port+'/PocheWebSocket/ponchito').
			success(function(data) {
				console.log(data);
				register.hide();
				votation.show();
			})
		}
    };
});