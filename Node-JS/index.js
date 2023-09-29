import { randomInt } from 'crypto';
import express from 'express'
const app = express();
import http from 'http';
let server = http.Server(app);
import { Server } from 'socket.io';
var io = new Server(server);
var players = [];
var roomstructlist = []

server.listen(8080, function(){
    console.log("Server is now running...");
});

io.on("connection", (socket)=>{
    var temproom = "room" + Math.floor(players.length / 2);
    socket.join(temproom);

    console.log("Player Connected!");

    io.to(temproom).emit('socketID', { id: socket.id });

    players.push(new player(socket.id, 0, 0, 0));

    if(players.length %2 ==0){
        roomstructlist.push(new roomstruct());
        io.to(temproom).emit("getPlayers", players);
        io.to(temproom).emit('startGame', {seed: Math.floor(Math.random() * 10000)});
    } 

    for(var i  = 0; i <25;i++){
        roomstructlist[(Math.floor(players.length/2))].tilerects[i] = {
            width:196,
            height:196,
            x:(((i % 5) * 196) + 64),
            y:(Math.floor(i / 5) * 196 + 64),
        }
    }

    socket.on("disconnect", ()=>{
        console.log("Player Disconnected");
        for(var i = 0; i < players.length; i++){
            io.to(temproom).emit('playerDisconnected', { id: socket.id});
            if(players[i].id == socket.id){
                players.splice(i, 1);
                roomstructlist.splice((Math.floor(players.length/2)), 1);
                socket.leave();
            }
        }
    });
    socket.on('playermove', function({x, y,rotation,xadd2,yadd2,moveVectx,moveVecty,kbaddx,kbaddy,dashvelx,dashvely,spawnprot,ballsize,mytime}){
        roomstructlist[(Math.floor(players.length/2))].time = 0;
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
                players[i].ballsize = ballsize;
                players[i].mytime = mytime;

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
                    dashvely:players[i].dashvely,
                    ballsize:players[i].ballsize,
                });
            }
            roomstructlist[(Math.floor(players.length/2))].time+=players[i].mytime;
        }
        roomstructlist[(Math.floor(players.length/2))].time/=players.length;
        if (roomstructlist[(Math.floor(players.length/2))].tilerects[roomstructlist[(Math.floor(players.length/2))].rantile].width <= 0) {
            roomstructlist[(Math.floor(players.length/2))].rantile = Math.floor(Math.random()*25);
        } else if (roomstructlist[(Math.floor(players.length/2))].rantile != -1) {
            if (roomstructlist[(Math.floor(players.length/2))].tilerects[roomstructlist[(Math.floor(players.length/2))].rantile].width > 0) {
                var changefactor = roomstructlist[(Math.floor(players.length/2))].time * 5;
                roomstructlist[(Math.floor(players.length/2))].tilerects[roomstructlist[(Math.floor(players.length/2))].rantile].width -= (changefactor);
                roomstructlist[(Math.floor(players.length/2))].tilerects[roomstructlist[(Math.floor(players.length/2))].rantile].height -= (changefactor);
                roomstructlist[(Math.floor(players.length/2))].tilerects[roomstructlist[(Math.floor(players.length/2))].rantile].x += changefactor / 2;
                roomstructlist[(Math.floor(players.length/2))].tilerects[roomstructlist[(Math.floor(players.length/2))].rantile].y += changefactor / 2;
                io.to(temproom).emit('setTiles', {
                    jstilerects:roomstructlist[(Math.floor(players.length/2))].tilerects
                });
            }
        }
        if (Math.floor(Math.random() *(1500 * (1 + roomstructlist[(Math.floor(players.length/2))].time))) == 0){
            io.to(temproom).emit('makePower', {
                randpos:Math.random()*3
            });
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
        for(var i  = 0; i <25;i++){
            roomstructlist[(Math.floor(players.length/2))].tilerects[i] = {
                width:196,
                height:196,
                x:(((i % 5) * 196) + 64),
                y:(Math.floor(i / 5) * 196 + 64),
            }
        }
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
    this.ballsize = -1;
    this.mytime = 0;
}

function roomstruct(){
    this.time = 0;
    this.tilerects = [25];
    this.rantile = Math.floor(Math.random()*25);
}