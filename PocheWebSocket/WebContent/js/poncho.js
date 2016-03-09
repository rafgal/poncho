//This file register into WebSocket for votation
var hoursPerDay = 7;
var ws = new WebSocket('ws://' + window.location.hostname + ':'
		+ window.location.port + window.location.pathname + 'ponchito');
var usersSortFunction = function(a, b) {
	return a.voto - b.voto;
};
(function() {
	angular.module('d3', []).factory(
			'd3Service',
			[ '$document', '$window', '$q', '$rootScope',
					function($document, $window, $q, $rootScope) {
						var d = $q.defer(), d3service = {
							d3 : function() {
								return d.promise;
							}
						};
						function onScriptLoad() {
							// Load client in the browser
							$rootScope.$apply(function() {
								d.resolve($window.d3);
							});
						}
						var scriptTag = $document[0].createElement('script');
						scriptTag.type = 'text/javascript';
						scriptTag.async = true;
						scriptTag.src = 'http://d3js.org/d3.v3.min.js';
						scriptTag.onreadystatechange = function() {
							if (this.readyState == 'complete')
								onScriptLoad();
						}
						scriptTag.onload = onScriptLoad;

						var s = $document[0].getElementsByTagName('body')[0];
						s.appendChild(scriptTag);

						return d3service;
					} ]);
	var poncho = angular.module('poncho', [ 'd3', 'ui.bootstrap' ]);
	poncho
			.directive(
					'd3Bars',
					[
							'$window',
							'$timeout',
							'd3Service',
							function($window, $timeout, d3Service) {
								return {
									restrict : 'A',
									scope : {
										data : '=',
										label : '@',
										onClick : '&'
									},
									link : function(scope, ele, attrs) {
										d3Service
												.d3()
												.then(
														function(d3) {
															var renderTimeout;
															var margin = parseInt(attrs.margin) || 20, barHeight = parseInt(attrs.barHeight) || 20, barPadding = parseInt(attrs.barPadding) || 5;
															var svg = d3
																	.select(
																			ele[0])
																	.append(
																			'svg')
																	.style(
																			'width',
																			'100%');
															$window.onresize = function() {
																scope.$apply();
															};
															scope
																	.$watch(
																			function() {
																				return angular
																						.element($window)[0].innerWidth;
																			},
																			function() {
																				scope
																						.render(scope.data);
																			});

															scope
																	.$watch(
																			'data',
																			function(
																					newData) {
																				scope
																						.render(newData);
																			},
																			true);
															scope.render = function(
																	data) {
																svg
																		.selectAll(
																				'*')
																		.remove();
																if (!data)
																	return;
																if (renderTimeout)
																	clearTimeout(renderTimeout);
																renderTimeout = $timeout(
																		function() {
																			var width = d3
																					.select(ele[0])[0][0].offsetWidth
																					- margin, height = scope.data.length
																					* (barHeight + barPadding), xScale = d3.scale
																					.linear()
																					.domain(
																							[
																									0,
																									d3
																											.max(
																													data,
																													function(
																															d) {
																														if (d.tipoVoto === 1) {
																															console
																																	.log('tipo dia');
																															return d.voto
																																	* hoursPerDay;
																														} else {
																															return d.voto;
																														}
																													}) ])
																					.range(
																							[
																									0,
																									width ]);
																			svg
																					.attr(
																							'height',
																							height);
																			svg
																					.selectAll(
																							'rect')
																					.data(
																							data)
																					.enter()
																					.append(
																							'rect')
																					.on(
																							'click',
																							function(
																									d,
																									i) {
																								return scope
																										.onClick({
																											item : d
																										});
																							})
																					.attr(
																							'height',
																							barHeight)
																					.attr(
																							'width',
																							140)
																					.attr(
																							'x',
																							Math
																									.round(margin / 2))
																					.attr(
																							'y',
																							function(
																									d,
																									i) {
																								return i
																										* (barHeight + barPadding);
																							})
																					.attr(
																							'fill',
																							"#1F77B4")
																					.transition()
																					.duration(
																							1000)
																					.attr(
																							'width',
																							function(
																									d) {
																								if (d.tipoVoto === 1) {
																									console
																											.log('tipo dia');
																									return xScale(d.voto
																											* hoursPerDay);
																								} else {
																									return xScale(d.voto);
																								}
																							});
																			svg
																					.selectAll(
																							'text')
																					.data(
																							data)
																					.enter()
																					.append(
																							'text')
																					.attr(
																							'fill',
																							'#fff')
																					.attr(
																							'y',
																							function(
																									d,
																									i) {
																								return i
																										* (barHeight + barPadding)
																										+ 15;
																							})
																					.attr(
																							'x',
																							15)
																					.text(
																							function(
																									d) {
																								var unit;
																								if (d.tipoVoto === 1) {
																									unit = ' días)';
																								} else {
																									unit = ' horas)';
																								}
																								return d.nombre
																										+ " ("
																										+ d.voto
																										+ unit;
																							});
																		}, 200);
															};
														});
									}
								}
							} ]);
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

	poncho.filter('avg', function() {
		return function(value) {
			if (value < hoursPerDay) {
				return value + ' horas';
			} else {
				return value + ' horas (' + (value / hoursPerDay).toFixed(1) + ' días)';
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
			console.log(boardCtrl.board);
			boardCtrl.status = data.boardStatus;
			if (boardCtrl.status === 0) {
				boardCtrl.approved = false;
			} else {
				var sum = 0;
				for (var i = 0; i < boardCtrl.board.length; i++) {
					var factor = 1;
					if (boardCtrl.board[i].tipoVoto === 1) {
						factor = hoursPerDay;
					}
					sum += boardCtrl.board[i].voto*factor;
				}
				boardCtrl.avg = sum / boardCtrl.board.length;
				boardCtrl.board.sort(usersSortFunction);
				// hard-code data
				$scope.data = boardCtrl.board;
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