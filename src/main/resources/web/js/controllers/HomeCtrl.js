'use strict';

/* Controllers */

angular.module('xtv.controllers').
  controller('HomeCtrl', ['$scope', 'xtvService', function($scope, xtvService) {

    xtvService.send('testdata.servers', {data: 123}, true).then(function(reply){
      $scope.servers = reply.data;
    });

    $scope.selectServer = function (server) {
      $scope.selectedServer = server;
    };

    $scope.showAddServerDialog = function () {
      $('#addServerDialog').modal('show');
    };

    $scope.addServer = function () {
      //TODO add Server
      $('#addServerDialog').modal('hide');
      $scope.servers.push ({
        name: $scope.newServer.uri,
        port: $scope.newServer.port,
        channels: []
      });
      $scope.newServer = undefined;
    };

    $scope.showAddChannelDialog = function () {
      $('#addChannelDialog').modal('show');
    };

    $scope.addChannel = function () {
      //TODO add Server
      $('#addChannelDialog').modal('hide');
      $scope.selectedServer.channels.push ({
        name: $scope.newChannel.name
      });
      $scope.newChannel = undefined;
    };

    $scope.showDeleteServerConfirm = function (server) {
      $scope.serverToDelete = server;
      $('#deleteServerConfirmDialog').modal ('show');
    };

    $scope.deleteServer = function () {
      // TODO delete server
      var index = $scope.servers.indexOf($scope.serverToDelete);
      $scope.servers.splice(index, 1);
      $scope.selectedServer = undefined;
      $scope.serverToDelete = undefined;
    };

    $scope.showDeleteChannelConfirm = function (channel) {
      $scope.channelToDelete = channel;
      $('#deleteChannelConfirmDialog').modal ('show');
    };

    $scope.deleteChannel = function () {
      // TODO delete channel
      var index = $scope.selectedServer.channels.indexOf($scope.channelToDelete);
      $scope.selectedServer.channels.splice(index, 1);
      $scope.channelToDelete = undefined;
    };

    $scope.getStatusClass = function (server) {
      if (server.status == 'Connected') {
         return 'glyphicon-globe';
      }
      return 'glyphicon-ban-circle';
    };

    $scope.toggleConnection = function (server) {
      //TODO toggle connection
      if (server.status == 'Connected'){
        server.status = 'Not Connected';
      } else {
        server.status = 'Connected';
      }
    };

    //TODO remove test-stuff
    xtvService.on('test.new', function(message){
      console.log('got test new (xtv): ', message);
    });

    xtvService.send('test.msg', {data: 123}).then(function(reply){
      console.log('A reply received: ', reply);
    });

  }]);