//This file register into WebSocket for votation

var ip_server = window.location.hostname;
var port = 9171;
(function() {
	var poncho = angular.module('poncho', []);
	poncho.filter('ordinal', function() {
		return function(value, type) {
			if(value<0){
				return "Sin voto";
			}
			var typeString = '';
			switch (type) {
			case 0:
				typeString = ' horas';
				break;
			case 1:
				typeString = ' días';
				break;
			}
			return value +  typeString;
		}
	});

	poncho.controller("loginController", function($scope, $http) {
		var ws = new WebSocket('ws://' + ip_server + ':' + port
				+ '/PonchoWebSocket/ponchito');

		ws.onmessage = function(data) {
			$("#register").hide();
			$("#votation").show();
			var datas = angular.fromJson(data.data);
			$scope.updateBoard(JSON.parse(data.data).usuarios);
		}
		$scope.register = function(person) {
			if (person != "") {
				ws.send('{"comando":0,"nombre":"' + person + '"}');
			}
		};
	});

	poncho.controller("BoardController", function($scope, $http) {
		var boardCtrl = this;
		boardCtrl.board = [ ];
		$scope.updateBoard = function(data) {
			boardCtrl.board = data;
			$scope.$apply();
		};

	});
})();