//This file register into WebSocket for votation

var ip_server = "localhost";//"10.10.1.90";
var port = 9091;
var poncho = angular.module('poncho',[]);
var ws = new WebSocket('ws://'+ip_server+':'+port+'/PonchoWebSocket/ponchito');

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