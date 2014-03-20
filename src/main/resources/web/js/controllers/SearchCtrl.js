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

//      $scope.searchResults = [];
//      $scope.searchResults.push ({
//        id:    '#43',
//        bot:   '[MG]-HDTV|EU|S|0006',
//        file:  'Justified.S05E08.720p.mkv',
//        size:  1390411776,
//        speed: '768 Kb/s',
//        date:  new Date ()
//      });
//
//      $scope.searchResults.push ({
//        id:    '#268',
//        bot:   '[MG]-HDTV|EU|Q|0003',
//        file:  'Justified.S05E08.720p.mkv',
//        size:  1390411776,
//        speed: '1293 Kb/s',
//        date:  new Date ()
//      });
//
//      $scope.searchResults.push ({
//        id:    '#103',
//        bot:   '[MG]-HDTV|EU|Q|0004',
//        file:  'Justified.S05E08.720p.mkv',
//        size:  1390411776,
//        speed: '438 Kb/s',
//        date:  new Date ()
//      });
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
      //TODO start download
      msg.show ("Added '" + item.file + "' to Download-Queue.");
    };

  }]);