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

io.on('connection', function(socket){
    console.log("Player Connected!");
    socket.emit('socketID', { id: socket.id });
    socket.emit('getPlayers', players);
    socket.broadcast.emit('newPlayer', { id: socket.id });
    socket.on('disconnect', function(){
        console.log("Player Disconnected");
        socket.broadcast.emit('playerDisconnected', { id: socket.id });
        for(var i = 0; i < players.length; i++){
            if(players[i].id == socket.id){
                players.splice(i, 1);
            }
        }
    }); 
    socket.on('playermove', function({x, y}){
        //console.log(x,y)
        socket.broadcast.emit('movement', { id: socket.id,x,y });
        for(var i = 0; i < players.length; i++){
            if(players[i].id == socket.id){
                players[i].x = x;
                players[i].y = y;
            }
        }
    });
    players.push(new player(socket.id, 0, 0));
});

function player(id, x, y){
    this.id = id;
    this.x = x;
    this.y = y;
}