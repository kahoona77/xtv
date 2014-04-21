angular.module ('xtv.services').factory('xtvService', [ '$rootScope', '$q', '$timeout', function($rootScope, $q, $timeout) {

  var connected = false;
  var queue = [];

  var addToQueue = function  (deferred, address, data) {
    queue.push({
      deferred: deferred,
      address:  address,
      data:     data
    });
  };

  var resolveQueue = function () {
    angular.forEach (queue, function (item) {
       var deferred = item.deferred;
//       deferred.resolve(vertxEventBusService.send(item.address, item.data, true));
    });
    queue = [];
  };

  $rootScope.$on('vertx-eventbus.system.connected', function() {
    console.log('eventbus connected');
//    connected = true;
//    resolveQueue();
  });

  $rootScope.$on('vertx-eventbus.system.disconnected', function() {
    console.log('eventbus disconnected');
    connected = false;
  });


  return {
        send: function (address, data) {
          if (connected) {
//            return vertxEventBusService.send(address, data, true);
          } else {
            var deferred = $q.defer ();
            addToQueue(deferred, address, data);
            return deferred.promise;
          }
        },

        on: function (address, callback) {
//          vertxEventBusService.on(address, callback);
        }
    }
}]);