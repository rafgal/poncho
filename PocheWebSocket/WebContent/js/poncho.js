//This file register into WebSocket for votation

var ws = new WebSocket('ws://' + window.location.hostname + ':'
		+ window.location.port + window.location.pathname + 'ponchito');
(function() {
	var poncho = angular.module('poncho', [ 'ui.bootstrap' ]);
	poncho.directive('radioWithChangeHandler',
			[ function checkboxWithChangeHandler() {
				return {
					replace : false,
					require : 'ngModel',
					scope : false,
					link : function(scope, element, attr, ngModelCtrl) {
						$(element).change(function() {
							if (element[0].checked) {
								scope.$apply(function() {
									ngModelCtrl.$setViewValue(attr.value);
								});
							}
						});
					}
				};
			} ]);
	poncho.directive('bootstrapSwitch', [ function() {
		return {
			restrict : 'A',
			require : '?ngModel',
			link : function(scope, element, attrs, ngModel) {
				element.bootstrapSwitch();

				element.on('switchChange.bootstrapSwitch', function(event,
						state) {
					if (ngModel) {
						scope.$apply(function() {
							ngModel.$setViewValue(state);
						});
					}
				});

				scope.$watch(attrs.ngModel, function(newValue, oldValue) {
					if (newValue) {
						element.bootstrapSwitch('state', true, true);
					} else {
						element.bootstrapSwitch('state', false, true);
					}
				});
			}
		};
	} ]);
	poncho.filter('ordinal', function() {
		return function(value, type, boardStatus) {
			if (boardStatus == 0) {
				if (value < 0) {
					return "Sin voto";
				} else {
					return "votó"
				}
			} else {
				if (value < 0) {
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
				return value + typeString;
			}
		}
	});

	poncho.controller("loginController", function($scope, $http) {

		ws.onmessage = function(data) {
			$("#register").hide();
			$("#votation").show();
			var datas = angular.fromJson(data.data);
			$scope.updateBoard(JSON.parse(data.data));
		}
		$scope.register = function(person) {
			if (person != "") {
				ws.send('{"comando":0,"nombre":"' + person + '"}');
			}
		};
	});

	poncho.controller("BoardController", function($scope, $http) {

		var boardCtrl = this;
		boardCtrl.fields = {
			type : 0
		};
		boardCtrl.command = {
			comando : 1
		};
		boardCtrl.board = [];
		boardCtrl.status = 0;
		boardCtrl.approved = false;
		boardCtrl.setConformity = function() {
			boardCtrl.command.comando = 2;
			delete boardCtrl.command.vote;
			boardCtrl.command.approved = boardCtrl.approved;
			ws.send(JSON.stringify(boardCtrl.command));
			boardCtrl.fields.vote = null;
			boardCtrl.fields.type = 0;
		};
		$scope.updateBoard = function(data) {
			boardCtrl.board = data.usuarios;
			boardCtrl.status = data.boardStatus;
			if (boardCtrl.status === 0) {
				boardCtrl.approved = false;
			}
			$scope.$apply();
		};
		$scope.vote = function() {
			boardCtrl.command.comando = 1;
			boardCtrl.command.vote = {};
			boardCtrl.command.vote.value = boardCtrl.fields.vote;
			boardCtrl.command.vote.type = boardCtrl.fields.type;
			console.log(JSON.stringify(boardCtrl.command));
			ws.send(JSON.stringify(boardCtrl.command));
		}
	});
})();