'use strict';

/* Controllers */

var productControllers = angular.module('productControllers', []);

productControllers.controller('ProductListCtrl',  function($scope, $routeParams,$http) {

  var searchTerm=$routeParams.searchTerm;
  var flow=$routeParams.flow;
  if (flow == null) {flow="A"; return;}
  if(searchTerm == null) {searchTerm="bag"; return;}          
			
  var searchUrl="http://localhost:4777/search?searchTerm="+searchTerm;
  
 
  $http({method: 'GET', url: searchUrl}).
  
  success(function(data, status, headers, config) {
  console.log('$scope -- ',$scope,data)
    $scope.items = data;
	$scope.itemCountMsg = data.length;
    $scope.orderProp = 'productId';
	
  }).
  error(function(data, status, headers, config) {
  alert('something went wrong : '+data+status);
 
  });

  });
