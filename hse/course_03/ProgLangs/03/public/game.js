function createStatusString() {
    console.log("creating status string");
    statusString = document.createTextNode('waiting for opponent');
    document.getElementsByTagName('body')[0].appendChild(statusString);
}

function createTable() {
    var body = document.getElementsByTagName('body')[0];
    var table = document.createElement('table');
    table.style.width = '500px';
    table.style.height = '500px';
    table.style.tableLayout = 'fixed';
    table.setAttribute('border', '1');

    var tableBody = document.createElement('tbody');
    for (var line = 0; line < 10; line++) {
        var tr = document.createElement('tr');
        for (var column = 0; column < 10; column++) {
            var cellId = line.toString() + ',' + column.toString();
            var td = document.createElement('td');
            td.setAttribute('id', cellId);
            td.style.textAlign = 'center';
            td.addEventListener('click', function (event) {
                var id = event.target.id;
                var coordinates = id.split(',');
                var _line = parseInt(coordinates[0]);
                var _column = parseInt(coordinates[1]);
                onCellClicked(_line, _column);
            });

            tr.appendChild(td)
        }
        tableBody.appendChild(tr);
    }
    table.appendChild(tableBody);
    body.appendChild(table)
}

var gameOver = false;
var socket = new WebSocket('ws://' + window.location.hostname + ':' + window.location.port + '/connect');
socket.onmessage = function (event) { onMessageReceived(JSON.parse(event.data)); };

function onMessageReceived(message) {
    console.log('received: ' + JSON.stringify(message));
    if (message.type === 'start') {
        console.log('game started');
        myTurn = message.turnOrder === 2; // потому что onTurnToggled его сменит
        onTurnToggled();
    } else if (message.type === 'win') {
        onGameOver(true);
    } else if (message.type === 'loss') {
        onGameOver(false);
    } else if (message.type === 'set_cell') {
        onTurnToggled();
        onSetCell(message.line, message.column, message.imOwner);
    }
}

function onTurnToggled() {
    myTurn = !myTurn;
    statusString.textContent = myTurn ? "Your turn" : "It's your opponent's turn";
}

function onGameOver(won) {
    gameOver = true;
    statusString.textContent = won ? "You won!" : "You lost!";
}

function onSetCell(line, column, isMy) {
    document.getElementById(line + ',' + column).innerHTML = isMy ? 'X' : 'O';
}

function onCellClicked(line, column) {
    if (gameOver) { return; }

    var json = JSON.stringify({
        "type": 'click',
        "line": line,
        "column": column
    });
    console.log('send: ' + json);
    socket.send(json);
}


