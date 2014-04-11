'use strict';

/* Controllers */

angular.module('xtv.controllers').
  controller('SearchCtrl', ['$scope', 'msg', 'xtvService', function($scope, msg, xtvService) {

    $scope.query = undefined;
    $scope.packetCount = '';
    $scope.searchResults = undefined;

    $scope.search = function () {
      xtvService.send('xtv.findPackets', {query: $scope.query}).then(function(response){
        if (response.status == 'ok') {
          $scope.searchResults = response.results;
        } else {
          msg.error (response.message);
        }
      });
    };

    $scope.countPackets = function () {
      xtvService.send('xtv.countPackets').then(function(response){
        if (response.status == 'ok') {
          $scope.packetCount = response.count;
        } else {
          msg.error (response.message);
        }
      });
    };
    $scope.countPackets();

    $scope.startDownload = function (item) {
      xtvService.send('xtv.downloadPacket', {data: item}).then(function(response){
        if (response.status == 'ok') {
          msg.show ("Added '" + item.name + "' to Download-Queue.");
        } else {
          msg.error (response.message);
        }
      });

    };

  }]);