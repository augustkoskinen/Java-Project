import { randomInt } from 'crypto';
import express from 'express'
const app = express();
import http from 'http';
let server = http.Server(app);
import { Server } from 'socket.io';
var io = new Server(server);
var players = [];

server.listen(8080, function(){
    console.log("Server is now running...");
});

io.on('connection', async function(socket){
    var temproom = ("room"+Math.floor(players.length/2))
    socket.join(temproom);
    players.push(new player(socket.id, 0, 0, 0));

    if(players.length %2 ==0){
        socket.broadcast.to(temproom).emit('startGame', {seed: Math.random()});
    }
    console.log("Player Connected!");
    socket.to(temproom).emit('socketID', { id: socket.id });
    socket.to(temproom).emit('getPlayers', players);
    socket.to(temproom).emit('newPlayer', { id: socket.id});
    socket.on('disconnect', function(){
        console.log("Player Disconnected");
        for(var i = 0; i < players.length; i++){
            socket.to(temproom).emit('playerDisconnected', { id: socket.id});
            if(players[i].id == socket.id){
                players.splice(i, 1);
            }
        }
        if(players.length%2==0){
            temproom = ("room"+Math.floor(players.length/2))
        }
        socket.leave("room1");
    });
    socket.on('playermove', function({x, y,rotation}){
        for(var i = 0; i < players.length; i++){
            socket.broadcast.to(temproom).emit('movement', { id: socket.id,x,y,rotation});
            if(players[i].id == socket.id){
                players[i].x = x;
                players[i].y = y;
                players[i].rotation = rotation;
            }
        }
    });
});

function player(id, x, y,rot,curroom){
    this.id = id;
    this.x = x;
    this.y = y;
    this.rotation = rot
}