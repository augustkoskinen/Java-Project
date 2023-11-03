import { WebSocketServer } from 'ws';

//vars/initialization
const wss = new WebSocketServer({ port: 8080 });
let clients= []
let rooms = []
rooms.push(new Roomstruct(rooms))

console.log("Server is running...")

//setting events
wss.on('connection', function connection(ws) {
    //connecting a new player
    console.log("Connected Player")
    clients.push(new Player(uuidv4(), 0, 0, 0, rooms[rooms.length-1].room,ws));
    rooms[rooms.length-1].clientlist.push(clients[clients.length-1].id)
    //let playerindex = findWS(clients,ws)

    //sending details ot client
    let senddata = {
        event: 'socketID',
        id: clients[clients.length-1].id,
        room: clients[clients.length-1].room
    }
    ws.send(JSON.stringify(senddata));

    //start game if there are two plaeyrs
    if(clients.length%2===0){
        let client = findID(clients,rooms[rooms.length-1].clientlist[0])
        let client2 = findID(clients,rooms[rooms.length-1].clientlist[1])
        clients[client].otherid = clients[client2].id
        clients[client2].otherid = clients[client].id
        let seed = Math.floor(Math.random() * 10000)
        let otherws = clients[client].ws

        //p1
        senddata = {
            event: 'getPlayers',
            otherid: clients[client2].id,
            color: "blue"
        }
        otherws.send(JSON.stringify(senddata));
        senddata = {
            event: 'startGame',
            seed: seed
        }
        otherws.send(JSON.stringify(senddata));

        //p2
        senddata = {
            event: 'getPlayers',
            otherid: clients[client].id,
            color: "red"
        }
        ws.send(JSON.stringify(senddata));
        senddata = {
            event: 'startGame',
            seed: seed
        }
        ws.send(JSON.stringify(senddata));

        rooms.push(rooms,new Roomstruct(rooms))
    }

    ws.on('error', console.error);

    //events
    ws.on('message', (e) => {
        //console.log("Message from client: " + e);

        //getting data
        const data = JSON.parse(e);
        let playerindex = findWS(clients,ws)
        let roomloc = findRoom(rooms,clients[playerindex].room);
        let otherws = clients[findID(clients,clients[playerindex].otherid)].ws

        switch (data.event) {
            case ('playermove') : {
                //getting player data
                
                if (clients[playerindex].x == data.x && clients[playerindex].y == data.y && clients[playerindex].rotation == data.rotation && clients[playerindex].xadd2 == data.xadd2 && clients[playerindex].yadd2 == data.yadd2 && clients[playerindex].moveVectx == data.moveVectx && clients[playerindex].moveVecty == data.moveVecty && clients[playerindex].kbaddx == data.kbaddx && clients[playerindex].kbaddy == data.kbaddy && clients[playerindex].dashvelx == data.dashvelx && clients[playerindex].dashvely == data.dashvely && clients[playerindex].spawnprot == data.spawnprot && clients[playerindex].ballsize == data.ballsize && clients[playerindex].mytime == data.mytime) {
                    return;
                }

                clients[playerindex].x = data.x;
                clients[playerindex].y = data.y;
                clients[playerindex].rotation = data.rotation;
                clients[playerindex].xadd2 = data.xadd2;
                clients[playerindex].yadd2 = data.yadd2;
                clients[playerindex].moveVectx = data.moveVectx;
                clients[playerindex].moveVecty = data.moveVecty;
                clients[playerindex].kbaddx = data.kbaddx;
                clients[playerindex].kbaddy = data.kbaddy;
                clients[playerindex].dashvelx = data.dashvelx;
                clients[playerindex].dashvely = data.dashvely;
                clients[playerindex].spawnprot = data.spawnprot;
                clients[playerindex].ballsize = data.ballsize;
                clients[playerindex].mytime = data.mytime;

                let senddata = {}

                //changing tile sizes every now and then
                if (rooms[roomloc].tilerects[rooms[roomloc].rantile].width <= 0) {
                    rooms[roomloc].rantile = Math.floor(Math.random() * 25);
                } else if (rooms[roomloc].rantile !== -1) {
                    if (rooms[roomloc].tilerects[rooms[roomloc].rantile].width > 0) {
                        var changefactor = 0.05;//rooms[roomloc].time;
                        rooms[roomloc].tilerects[rooms[roomloc].rantile].width -= (changefactor);
                        rooms[roomloc].tilerects[rooms[roomloc].rantile].height -= (changefactor);
                        rooms[roomloc].tilerects[rooms[roomloc].rantile].x += changefactor / 2;
                        rooms[roomloc].tilerects[rooms[roomloc].rantile].y += changefactor / 2;
                    }
                }

                //preparing sending struct
                senddata = {
                    event: 'movement',
                    id: data.otherid,
                    x: data.x,
                    y: data.y,
                    rotation: data.rotation,
                    xadd2: data.xadd2,
                    yadd2: data.yadd2,
                    spawnprot2: data.spawnprot,
                    moveVectx: data.moveVectx,
                    moveVecty: data.moveVecty,
                    kbaddx: data.kbaddx,
                    kbaddy: data.kbaddy,
                    dashvelx:data.dashvelx,
                    dashvely: data.dashvely,
                    ballsize: data.ballsize,
                    jstilerects: rooms[roomloc].tilerects,
                };

                //sending data to clients
                ws.send(JSON.stringify(senddata))
                otherws.send(JSON.stringify(senddata));

                //making power every now and then
                if (Math.floor(Math.random() * (1500)) === 0) {
                    let rand = Math.random() * 3
                    ws.send(JSON.stringify({
                        event:'makePower',
                        randpos: rand
                    }));
                    otherws.send(JSON.stringify({
                        event:'makePower',
                        randpos: rand
                    }));
                }

                break;
            }
            case ('shootmyball'): {
                //sending data for shoot
                let senddata = {
                    event:'shootBullet',
                    id: clients[findWS(clients,ws)].id,
                    ballcount: data.ballcount,
                    ballsize: data.ballsize,
                    color: data.color
                }
                ws.send(JSON.stringify(senddata));
                otherws.send(JSON.stringify(senddata));
                break;
            }
            case ('updateserverpoints') : {
                //updating points on the server, reseting tiles, and sending the event to the other client
                for (let i = 0; i < 25; i++) {
                    rooms[roomloc].tilerects[i] = {
                        width: 196,
                        height: 196,
                        x: (((i % 5) * 196) + 64),
                        y: (Math.floor(i / 5) * 196 + 64),
                    }
                }
                let senddata = {
                    event: 'updatePoints',
                    ran: data.ran,
                    rantile: data.rantile
                }
                otherws.send(JSON.stringify(senddata));
                break;
            }
        }
    });

    ws.on("close", () => {
        //disconnecting both players and cleaning up their websockets
        let disclient = findWS(clients,ws)
        let otherclient = findID(clients,clients[disclient].otherid)
        if(otherclient!==-1){
            let otherws = clients[otherclient].ws
            let senddata = {
                event: 'disconnecting',
            }
            ws.send(JSON.stringify(senddata));
            otherws.send(JSON.stringify(senddata))
            ws.close();
            otherws.close();
        } else {
            rooms.splice(findRoom(rooms,clients[disclient].room),1)
        }
        clients.splice(disclient,1);
        console.log("Disconnected Player")
    });

});

//makes a unique id
function uuidv4() {
    return 'xxxxxxxx'.replace(/[xy]/g, function(c) {
        let r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

//finds player index based on id
function findID(map,id){
    for (let i = 0; i < map.length;i++){
        if(map[i].id === id)
            return i
    }
    return -1
}

//finds room index based on name
function findRoom(rooms,room){
    for (let i = 0; i < rooms.length;i++){
        if(rooms[i].room === room)
            return i
    }
    return -1
}

//finds player index based on its websocket
function findWS(map,ws){
    for (let i = 0; i < map.length;i++){
        if(map[i].ws === ws)
            return i
    }
    return -1
}

//player object
function Player(id, x, y,rot,room,ws){
    this.ws = ws;
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
    this.otherid = "";
    this.room = room;
}

//room object
function Roomstruct(rooms){
    this.time = 0;
    this.tilerects = [25];
    this.rantile = Math.floor(Math.random()*25);
    this.clientlist = [];
    for(let i  = 0; i <25;i++){
        this.tilerects[i] = {
            width:196,
            height:196,
            x:(((i % 5) * 196) + 64),
            y:(Math.floor(i / 5) * 196 + 64),
        }
    }

    this.room ="";
    for(let i  = 0; i<rooms.length;i++) {
        if (!findRoom(rooms, "room" + i))
            this.room = "room" + i;
    }
    if(this.room===""){
        this.room = "room"+rooms.length;
    }
}
