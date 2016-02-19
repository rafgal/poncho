//This file register into WebSocket for votation

var ip_server = "10.10.1.4";
var port = 8080;
var poncho = angular.module('poncho',[]);
var ws = new WebSocket('ws://'+ip_server+':'+port+'/PocheWebSocket/ponchito');

ws.onmessage = function(data) {
	console.log(data);
	$("#register").hide();
	$("#votation").show();
}

poncho.controller("loginController", function($scope, $http) {
	$scope.register = function(person) {
		if(person != "") {
			ws.send(person);
		}
    };
});