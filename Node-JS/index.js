import { WebSocketServer } from 'ws';

const wss = new WebSocketServer({ port: 8080 });
let clients= []
let rooms = []
rooms.push(new Roomstruct(rooms,'room0'))

console.log("Server is running...")

wss.on('connection', function connection(ws) {
    console.log("Connected Player")
    clients.push(new Player(uuidv4(), 0, 0, 0, rooms[rooms.length-1].room,ws));
    rooms[rooms.length-1].clientlist.push(clients[clients.length-1].id)

    let playerindex = findWS(clients,ws)

    let senddata = {
        event: 'socketID',
        id: clients[clients.length-1].id,
        room: clients[clients.length-1].room
    }
    ws.send(JSON.stringify(senddata));

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

        rooms.push(rooms,new Roomstruct('room'+rooms.length))
    }

    ws.on('error', console.error);

    ws.on('message', (e) => {
        //console.log("Message from client: " + e);
        const data = JSON.parse(e);
        let playerindex = findWS(clients,ws)
        let roomloc = findRoom(rooms,clients[playerindex].room);
        let otherws = clients[findID(clients,clients[playerindex].otherid)].ws
        if(data.event==="debug") {
            console.log("here:")///+data.message)
        }

        switch (data.event) {
            case ('playermove') : {
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
                /*
                    jstilerects1: rooms[roomloc].tilerects[1],
                    jstilerects2: rooms[roomloc].tilerects[2],
                    jstilerects3: rooms[roomloc].tilerects[3],
                    jstilerects4: rooms[roomloc].tilerects[4],
                    jstilerects5: rooms[roomloc].tilerects[5],
                    jstilerects6: rooms[roomloc].tilerects[6],
                    jstilerects7: rooms[roomloc].tilerects[7],
                    jstilerects8: rooms[roomloc].tilerects[8],
                    jstilerects9: rooms[roomloc].tilerects[9],
                    jstilerects10: rooms[roomloc].tilerects[10],
                    jstilerects11: rooms[roomloc].tilerects[11],
                    jstilerects12: rooms[roomloc].tilerects[12],
                    jstilerects13: rooms[roomloc].tilerects[13],
                    jstilerects14: rooms[roomloc].tilerects[14],
                    jstilerects15: rooms[roomloc].tilerects[15],
                    jstilerects16: rooms[roomloc].tilerects[16],
                    jstilerects17: rooms[roomloc].tilerects[17],
                    jstilerects18: rooms[roomloc].tilerects[18],
                    jstilerects19: rooms[roomloc].tilerects[19],
                    jstilerects20: rooms[roomloc].tilerects[20],
                    jstilerects21: rooms[roomloc].tilerects[21],
                    jstilerects22: rooms[roomloc].tilerects[22],
                    jstilerects23: rooms[roomloc].tilerects[23],
                    jstilerects24: rooms[roomloc].tilerects[24],
                 */
                //console.log(rooms[roomloc].tilerects)

                ws.send(JSON.stringify(senddata))
                otherws.send(JSON.stringify(senddata));

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
            rooms.push(new Roomstruct(rooms,'room'+rooms.length))
        }
        clients.splice(disclient,1);
        console.log("Disconnected Player")
    });

});

function uuidv4() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        let r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

function inRoomList(map,room){
    let list = []
    for (let i = 0; i < map.length;i++){
        if(map[i].roomid === room)
            list.push(i)
    }
    return list
}
function findID(map,id){
    for (let i = 0; i < map.length;i++){
        if(map[i].id === id)
            return i
    }
    return -1
}

function findRoom(rooms,room){
    for (let i = 0; i < rooms.length;i++){
        if(rooms[i].room === room)
            return i
    }
    return -1
}

function findWS(map,ws){
    for (let i = 0; i < map.length;i++){
        if(map[i].ws === ws)
            return i
    }
    return -1
}

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

function Roomstruct(rooms,room){
    this.time = 0;
    this.tilerects = [25];
    this.rantile = Math.floor(Math.random()*25);
    this.room = room;
    this.clientlist = [];
    for(let i  = 0; i <25;i++){
        this.tilerects[i] = {
            width:196,
            height:196,
            x:(((i % 5) * 196) + 64),
            y:(Math.floor(i / 5) * 196 + 64),
        }
    }
    //this.num=0
}

/*import { WebSocketServer,WebSocket } from 'ws';

const wss = new WebSocketServer({ port: 8080 });
let clients= []
let rooms = []
rooms.push(new Roomstruct(rooms,'room0'))

console.log("Server is running...")

wss.on('connection', function connection(ws) {
    console.log("Connected Player")
    let thisid = uuidv4()
    clients.push(new Player(thisid, 0, 0, 0, rooms[rooms.length-1].room,ws));
    let playerindex = clients.length-1
    rooms[rooms.length-1].clientlist.push({
        ws:ws,
        id:thisid
    });
    let roomloc = findRoom(rooms, clients[playerindex].room)
    let otherws;

    let senddata = {
        event: 'socketID',
        id: clients[clients.length-1].id,
        room: clients[clients.length-1].room
    }

    ws.send(JSON.stringify(senddata));

    if(clients.length%2===0){
        let client = findID(clients,rooms[rooms.length-1].clientlist[0].id)
        let client2 = findID(clients,rooms[rooms.length-1].clientlist[1].id)
        if (playerindex===client)
            otherws = clients[client2].ws
        if (playerindex===client2)
            otherws = clients[client].ws
        otherws.send(JSON.stringify("hi"))
        clients[client].otherid = clients[client2].id
        clients[client2].otherid = clients[client].id
        let seed = Math.floor(Math.random() * 10000)

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

        //console.log(clients)
        rooms.push(new Roomstruct(rooms,'room'+rooms.length))
    }

    ws.on('error', console.error);

    ws.on('message', (e) => {
        //console.log("Message from client: " + e);
        const data = JSON.parse(e);
        if (data.event === 'playermove') {
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

            let senddata = {
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
                dashvelx: data.dashvelx,
                dashvely: data.dashvely,
                ballsize: data.ballsize
            }
            otherws.send(JSON.stringify(senddata));

            if (Math.floor(Math.random() * (1500)) === 0) {
                senddata = {
                    event: 'makePower',
                    randpos: Math.random() * 3
                }
                ws.send(JSON.stringify(senddata));
                otherws.send(JSON.stringify(senddata));
            }
        }
        if (data.event === 'shootmyball') {
            let senddata = {
                event:'shootBullet',
                id: clients[findWS(clients,ws)].id,
                ballcount: data.ballcount,
                ballsize: data.ballsize,
                color: data.color
            }
            ws.send(JSON.stringify(senddata));
            otherws.send(JSON.stringify(senddata));
        }
        if (data.event === 'playermove'){
            if (rooms[roomloc].tilerects[rooms[roomloc].rantile].width <= 0) {
                rooms[roomloc].rantile = Math.floor(Math.random() * 25);
            } else if (rooms[roomloc].rantile !== -1) {
                if (rooms[roomloc].tilerects[rooms[roomloc].rantile].width > 0) {
                    var changefactor = rooms[roomloc].time;
                    rooms[roomloc].tilerects[rooms[roomloc].rantile].width -= (changefactor);
                    rooms[roomloc].tilerects[rooms[roomloc].rantile].height -= (changefactor);
                    rooms[roomloc].tilerects[rooms[roomloc].rantile].x += changefactor / 2;
                    rooms[roomloc].tilerects[rooms[roomloc].rantile].y += changefactor / 2;
                    let senddata = {
                        event:'setTiles',
                        jstilerects: rooms[roomloc].tilerects
                    }
                    ws.send(JSON.stringify(senddata));
                    otherws.send(JSON.stringify(senddata));
                }
            }
        }
        if (data.event === 'updateserverpoints') {
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
        }
    });

    ws.on("close", () => {
        let disclient = findWS(clients,ws)
        let otherclient = findID(clients,clients[disclient].otherid)
        if(otherclient!=-1){
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
            rooms.push(new Roomstruct(rooms,'room'+rooms.length))
        }
        clients.splice(disclient,1);
        console.log("Disconnected Player")
    });

});

function uuidv4() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        let r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

function inRoomList(map,room){
    let list = []
    for (let i = 0; i < map.length;i++){
        if(map[i].roomid === room)
            list.push(i)
    }
    return list
}
function findID(map,id){
    for (let i = 0; i < map.length;i++){
        if(map[i].id === id)
            return i
    }
    return -1
}

function findRoom(rooms,room){
    for (let i = 0; i < rooms.length;i++){
        if(rooms[i].room === room)
            return i
    }
    return -1
}

function findWS(map,ws){
    for (let i = 0; i < map.length;i++){
        if(map[i].ws === ws)
            return i
    }
    return -1
}

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

function Roomstruct(rooms,room){
    this.time = 0;
    this.tilerects = [25];
    this.rantile = Math.floor(Math.random()*25);
    this.room = room;
    this.clientlist = [];
    for(let i  = 0; i <25;i++){
        this.tilerects[i] = {
            width:196,
            height:196,
            x:(((i % 5) * 196) + 64),
            y:(Math.floor(i / 5) * 196 + 64),
        }
    }
    //this.num=0
}
*/
