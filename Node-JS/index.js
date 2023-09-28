import { randomInt } from 'crypto';
import express from 'express'
const app = express();
import http from 'http';
let server = http.Server(app);
import { Server } from 'socket.io';
var io = new Server(server);
var players = [];
var temproom = "room0";

server.listen(8080, function(){
    console.log("Server is now running...");
});

io.on("connection", (socket)=>{
    temproom = "room0";
    socket.join(temproom);

    console.log("Player Connected!");

    io.to(temproom).emit('socketID', { id: socket.id });

    players.push(new player(socket.id, 0, 0, 0));

    if(players.length %2 ==0){
        io.to(temproom).emit("getPlayers", players);
        io.to(temproom).emit('startGame', {seed: Math.floor(Math.random() * 10000)});
    } else if(players.length %2 !=0){
        //temproom = "room"+((Math.floor(players.length/2)))
    }

    socket.on("disconnect", ()=>{
        console.log("Player Disconnected");
        for(var i = 0; i < players.length; i++){
            io.to(temproom).emit('playerDisconnected', { id: socket.id});
            if(players[i].id == socket.id){
                players.splice(i, 1);
                socket.leave();
            }
        }
    });
    socket.on('playermove', function({x, y,rotation,xadd2,yadd2,moveVectx,moveVecty,kbaddx,kbaddy,dashvelx,dashvely,spawnprot}){
        for(var i = 0; i < players.length; i++){
            if(players[i].id == socket.id){
                players[i].x = x;
                players[i].y = y;
                players[i].rotation = rotation;
                players[i].xadd2 = xadd2;
                players[i].yadd2 = yadd2;
                players[i].moveVectx = moveVectx;
                players[i].moveVecty = moveVecty;
                players[i].kbaddx = kbaddx;
                players[i].kbaddy = kbaddy;
                players[i].dashvelx = dashvelx;
                players[i].dashvely = dashvely;
                players[i].spawnprot = spawnprot;

                io.to(temproom).emit('movement', {
                    id:players[i].id,
                    x:players[i].x,
                    y:players[i].y,
                    spawnprot:players[i].spawnprot,
                    rotation:players[i].rotation,
                    xadd2:players[i].xadd2,
                    yadd2:players[i].yadd2,
                    moveVectx:players[i].moveVectx,
                    moveVecty:players[i].moveVecty,
                    kbaddx:players[i].kbaddx,
                    kbaddy:players[i].kbaddy,
                    dashvelx:players[i].dashvelx,
                    dashvely:players[i].dashvely
                });
            }
        }
    });
    socket.on('shootmyball', function({ballcount,ballsize,color}){
        for(var i = 0; i < players.length; i++){
            if(players[i].id == socket.id){
                io.to(temproom).emit('shootBullet', {
                    id: socket.id,
                    ballcount:ballcount,
                    ballsize:ballsize,
                    color:color
                });
            }
        }
    });
    socket.on('updateserverpoints', function({id,ran,rantile}){
        io.to(temproom).emit('updatePoints', {
            id: id,
            ran: ran,
            rantile: rantile
        });
    });
});

function player(id, x, y,rot){
    this.id = id;
    this.x = x;
    this.y = y;
    this.rotation = rot
    this.xadd2 = 0;
    this.yadd2 = 0;
    this.moveVectx = 0;
    this.moveVecty = 0;
    this.kbaddx = 0;
    this.kbaddy = 0;
    this.dashvelx = 0;
    this.dashvely = 0;
    this.spawnprot = 0;
}