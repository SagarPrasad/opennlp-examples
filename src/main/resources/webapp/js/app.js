'use strict';

/* App Module */

var listingApp = angular.module('listingApp', [
  'ngRoute',
  'productAnimations',

  'productControllers'
  
]);

listingApp.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
      when('/items', {
        templateUrl: 'partials/product-list.html',
        controller: 'ProductListCtrl'
      }).
      otherwise({
        redirectTo: '/items'
      });
  }]);
