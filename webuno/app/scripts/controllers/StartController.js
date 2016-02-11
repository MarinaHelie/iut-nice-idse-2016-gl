'use strict';

angular.module('unoApp')
    .controller('StartController', ['$rootScope', '$scope', '$state', '$http', function ($rootScope, $scope, $state, $http) {
        $scope.nbPlayers = 2;

        $scope.goGame = function () {
            if ($scope.game && $scope.game.length > 3 && $scope.user.name) {

                //TODO remplacer par Game.createGame($scope.game,$scope.user.name,$scope.nbPlayers)
                $http.post('/rest/game', {
                        game: $scope.game,
                        player: $scope.user.name,
                        numberplayers: $scope.nbPlayers
                    }, {
                        headers: {
                            token: $scope.user.token
                        }
                    })
                    .then(function (data) {
                        switch (data.status) {
                            case 200 :
                                $state.go('app.room', {name: $scope.game});
                                break;
                            default:
                                $scope.error = data.error;
                        }
                    }, function (error) {
                        $scope.error = "Une erreur est survenue : " + error;
                    });
            } else {
                $scope.error = "3 caractères minimum est requis pour le nom de la partie";
            }
        };
    }]);
