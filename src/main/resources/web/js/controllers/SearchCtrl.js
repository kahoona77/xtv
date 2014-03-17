'use strict';

/* Controllers */

angular.module('xtv.controllers').
  controller('SearchCtrl', ['$scope', 'msg', 'vertxEventBusService', function($scope, msg, vertxEventBusService) {

    $scope.query = undefined;
    $scope.searchResults = undefined;

    $scope.search = function () {
      // TODO search

      $scope.searchResults = [];
      $scope.searchResults.push ({
        id:    '#43',
        bot:   '[MG]-HDTV|EU|S|0006',
        file:  'Justified.S05E08.720p.mkv',
        size:  1390411776,
        speed: '768 Kb/s',
        date:  new Date ()
      });

      $scope.searchResults.push ({
        id:    '#268',
        bot:   '[MG]-HDTV|EU|Q|0003',
        file:  'Justified.S05E08.720p.mkv',
        size:  1390411776,
        speed: '1293 Kb/s',
        date:  new Date ()
      });

      $scope.searchResults.push ({
        id:    '#103',
        bot:   '[MG]-HDTV|EU|Q|0004',
        file:  'Justified.S05E08.720p.mkv',
        size:  1390411776,
        speed: '438 Kb/s',
        date:  new Date ()
      });
    };

    $scope.startDownload = function (item) {
      //TODO start download
      msg.show ("Added '" + item.file + "' to Download-Queue.");
    };

  }]);