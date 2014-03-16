'use strict';


// Declare app level module which depends on filters, and services
angular.module('xtv', [
  'ngRoute',
  'xtv.filters',
  'xtv.services',
  'xtv.directives',
  'xtv.controllers'
]).
config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/view1', {templateUrl: 'web/partials/partial1.html', controller: 'MyCtrl1'});
  $routeProvider.when('/view2', {templateUrl: 'web/partials/partial2.html', controller: 'MyCtrl2'});
  $routeProvider.otherwise({redirectTo: '/view1'});
}]);
