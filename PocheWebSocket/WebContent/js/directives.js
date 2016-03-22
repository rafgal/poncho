/**
 * 
 */

var poncho = angular.module('poncho_directives', []);

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

