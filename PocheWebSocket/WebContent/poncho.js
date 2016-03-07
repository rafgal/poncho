//This file register into WebSocket for votation

var ip_server = window.location.hostname;
var port = 9171;
var poncho = angular.module('poncho',[]);

poncho.factory('ws', function(){
	
    return { FirstName: '' };
});


poncho.controller("loginController", function($scope, $http) {
	var ws = new WebSocket('ws://'+ip_server+':'+port+'/PonchoWebSocket/ponchito');

	ws.onmessage = function(data) {
		$("#register").hide();
		$("#votation").show();
		$scope.updateBoard(angular.fromJson(data.data).usuarios);
	}
	$scope.register = function(person) {
		if(person != "") {
			ws.send('{"comando":0,"nombre":"'+person+'"}');
		}
    };
});

poncho.controller("boardCntrl",function($scope, $http) {
	$scope.board=[{nombre:'getsdajk'}];
	$scope.updateBoard = function(data) {
		console.log(data);
		this.board=data;
		console.log(this.board);
		console.log(this.board[0].nombre);
    };
});