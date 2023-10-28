import { WebSocketServer } from 'ws';
import WebSocket from 'ws';

//const wss = new WebSocketServer({ port: 8080 });
const wss = new WebSocket('ws://localhost:8080', {
    perMessageDeflate: false
});
wss.on('connection', function connection(ws) {
    ws.on('error', console.error);

    ws.on('message', function message(data) {
        console.log('received: %s', data);
    });

    ws.send('something');
});

/*
import { randomInt } from 'crypto';

import express from 'express'
const app = express();
import http from 'http';
let server = http.Server(app);
import { Server } from 'socket.io';
var io = new Server(server);
var players = [];
var rooms = []
var curroom = ""

server.listen(8080, function(){
    console.log("Server is now running...");
});

io.on("connection", (socket)=>{
    var openroom = checkRoomsFor(rooms,1);
    if(openroom==-1){
        curroom = "rm"+(rooms.length)
        rooms.push(new roomstruct(curroom))
        players.push(new player(socket.id, 0, 0, 0, curroom));
        openroom = rooms.length-1
        socket.join(curroom);
        io.to(rooms[openroom].room).emit('socketID', { id: socket.id,room: curroom});
        rooms[openroom].num++;
    } else {
        curroom = "rm"+(rooms.length-1)
        socket.join(curroom);
        players.push(new player(socket.id, 0, 0, 0, curroom));
        io.to(curroom).emit('socketID', { id: socket.id,room: curroom});
        rooms[openroom].num++;
        io.to(curroom).emit("getPlayers", players);
        io.to(curroom).emit('startGame', {seed: Math.floor(Math.random() * 10000)});
    }
    console.log("Player Connected!");
    for(var i  = 0; i <25;i++){
        rooms[openroom].tilerects[i] = {
            width:196,
            height:196,
            x:(((i % 5) * 196) + 64),
            y:(Math.floor(i / 5) * 196 + 64),
        }
    }

    socket.on("disconnect", ()=>{
        console.log("Player Disconnected");
        for(var i = 0; i < players.length; i++){
            if(players[i].id == socket.id){
                var roomlocation = locateRoom(rooms,players[i].myroom)
                io.to(rooms[roomlocation].room).emit('playerDisconnected', { id: socket.id});
                rooms[roomlocation].num--;
                if(rooms[roomlocation].num<=0){
                    rooms.splice(roomlocation, 1);
                }
                players.splice(i, 1);
                socket.leave();
            }
        }
    });
    socket.on('playermove', function({x, y,rotation,xadd2,yadd2,moveVectx,moveVecty,kbaddx,kbaddy,dashvelx,dashvely,spawnprot,ballsize,mytime,room}){
        var roomloc = locateRoom(rooms,room);
        rooms[roomloc].time = 0;
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

                io.to(rooms[roomloc].room).emit('movement', {
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
            rooms[roomloc].time+=players[i].mytime;
        }
        rooms[roomloc].time/=players.length;
        if (Math.floor(Math.random() *(1500 * (1 + rooms[roomloc]))) == 0){
            io.to(rooms[roomloc].room).emit('makePower', {
                randpos:Math.random()*3
            });
        }
    });
    socket.on('shootmyball', function({ballcount,ballsize,color,room}){
        for(var i = 0; i < players.length; i++){
            if(players[i].id == socket.id){
                io.to(room).emit('shootBullet', {
                    id: socket.id,
                    ballcount:ballcount,
                    ballsize:ballsize,
                    color:color
                });
            }
        }
    });
    socket.on('updateTiles', function({room}){
        var roomloc = locateRoom(rooms,room);
        if (rooms[roomloc].tilerects[rooms[roomloc].rantile].width <= 0) {
            rooms[roomloc].rantile = Math.floor(Math.random()*25);
        } else if (rooms[roomloc].rantile != -1) {
            if (rooms[roomloc].tilerects[rooms[roomloc].rantile].width > 0) {
                var changefactor = rooms[roomloc].time;
                rooms[roomloc].tilerects[rooms[roomloc].rantile].width -= (changefactor);
                rooms[roomloc].tilerects[rooms[roomloc].rantile].height -= (changefactor);
                rooms[roomloc].tilerects[rooms[roomloc].rantile].x += changefactor / 2;
                rooms[roomloc].tilerects[rooms[roomloc].rantile].y += changefactor / 2;
                io.to(rooms[roomloc].room).emit('setTiles', {
                    jstilerects:rooms[roomloc].tilerects
                });
            }
        }
    });
    socket.on('updateserverpoints', function({id,ran,rantile,room}){
        var roomloc = locateRoom(rooms,room);
        for(var i  = 0; i <25;i++){
            rooms[roomloc].tilerects[i] = {
                width:196,
                height:196,
                x:(((i % 5) * 196) + 64),
                y:(Math.floor(i / 5) * 196 + 64),
            }
        }
        io.to(rooms[roomloc].room).emit('updatePoints', {
            id: id,
            ran: ran,
            rantile: rantile
        });
    });
});

function player(id, x, y,rot,room){
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
    this.myroom = room;
}

function roomstruct(room){
    this.time = 0;
    this.tilerects = [25];
    this.rantile = Math.floor(Math.random()*25);
    this.room = room;
    this.num=0
}

function checkRoomsFor(rooms,playernumber){
    for(var i = 0; i < rooms.length;i++){
        if(rooms[i].num==playernumber){
            return i;
        }
    }
    return -1;
}
function locateRoom(rooms,name){
    for(var i = 0; i < rooms.length;i++){
        if(rooms[i].room==name){
            return i;
        }
    }
    return -1;
}

function locatePlayerByID(players,id){
    for(var i = 0; i < players.length;i++){
        if(players[i].id==id){
            return i;
        }
    }
    return -1;
}
*/
