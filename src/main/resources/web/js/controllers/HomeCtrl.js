'use strict';

/* Controllers */

angular.module('xtv.controllers').
  controller('HomeCtrl', ['$scope', 'xtvService', 'msg', function($scope, xtvService, msg) {

    $scope.loadServers = function () {
      xtvService.send('xtv.loadServers', {data: 123}, true).then(function(response){
        if (response.status == 'ok') {
          $scope.servers = response.results;

          //reselect server
          if ($scope.selectedServer) {
            angular.forEach ($scope.servers, function (server) {
               if (server._id == $scope.selectedServer._id) {
                 $scope.selectedServer = server;
               }
            });
          }
        } else {
          msg.error (response.message);
        }
      });
    };
    $scope.loadServers();

    $scope.selectServer = function (server) {
      $scope.selectedServer = server;
    };

    $scope.showAddServerDialog = function () {
      $('#addServerDialog').modal('show');
    };

    $scope.addServer = function () {
      var newServer = {
          name: $scope.newServer.uri,
          port: $scope.newServer.port,
          status: 'Not Connected',
          channels: []
      };

      xtvService.send ('xtv.saveServer', {data: newServer}).then (function (response) {
        if (response.status = 'ok') {
          $('#addServerDialog').modal('hide');
          $scope.newServer = undefined;
          $scope.loadServers();
        } else {
          msg.error (response.message);
        }
      });
    };

    $scope.showAddChannelDialog = function () {
      $('#addChannelDialog').modal('show');
    };

    $scope.addChannel = function () {
      var data = {
        serverId: $scope.selectedServer._id,
        channel: {
          name: $scope.newChannel.name,
          botsCount: 0,
          packetsCount: 0
        }
      };
      xtvService.send ('xtv.addChannel', {data: data}).then (function (response) {
        if (response.status = 'ok') {
          $('#addChannelDialog').modal('hide');
          $scope.newChannel = undefined;
          $scope.loadServers();
        } else {
          msg.error (response.message);
        }
      });
    };

    $scope.showDeleteServerConfirm = function (server) {
      $scope.serverToDelete = server;
      $('#deleteServerConfirmDialog').modal ('show');
    };

    $scope.deleteServer = function () {
      xtvService.send ('xtv.deleteServer', {data: $scope.serverToDelete}).then (function (response) {
        if (response.status = 'ok') {
          $scope.selectedServer = undefined;
          $scope.serverToDelete = undefined;
          $scope.loadServers();
        } else {
          msg.error (response.message);
        }
      });
    };

    $scope.showDeleteChannelConfirm = function (channel) {
      $scope.channelToDelete = channel;
      $('#deleteChannelConfirmDialog').modal ('show');
    };

    $scope.deleteChannel = function () {
      var data = {
        serverId: $scope.selectedServer._id,
        channelId: $scope.channelToDelete._id
      };

      xtvService.send ('xtv.deleteChannel', {data: data}).then (function (response) {
        if (response.status = 'ok') {
          $scope.channelToDelete = undefined;
          $scope.loadServers();
        } else {
          msg.error (response.message);
        }
      });
    };

    $scope.getStatusClass = function (server) {
      if (server.status == 'Connected') {
         return 'glyphicon-globe';
      }
      return 'glyphicon-ban-circle';
    };

    $scope.toggleConnection = function (server) {
      xtvService.send ('xtv.toggleConnection', {data: angular.copy (server)}).then (function (response) {
        if (response.status = 'ok') {
          server.status = response.result.status;
        } else {
          msg.error (response.message);
        }
      });
    };
  }]);