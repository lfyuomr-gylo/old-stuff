const REQUEST_TYPE = 'REQUEST';
const RESPONSE_TYPE = 'RESPONSE';

const generateId = () => Math.random().toString(36).substring(2, 10);

const vueApp = new Vue({
    el: "#vue-app",
    data: {
        // front-end data
        id: 0, // id of the client
        number: Math.floor(Math.random() * 100) - 50, // local number
        connections: [],
        result: 0, // result of minimum number computation
        newNeighborInput: "", // value of 'add neighbor' input field
        //
        _peer: null,
        operations: {} // opId: {waitingFor: [], parent: "id"|null, value: 0}
    },
    created: function() {
        this._peer = new Peer({key: 'qz7o3npt18e8w7b9', debug: 3});
        this._peer.on('open', id => this.id = id);
        this._peer.on('connection', this._onConnected);
        window.onunload = window.onbeforeunload = function() {
            if (!!this._peer && !this._peer.destroyed) {
                this._peer.destroy();
            }
        };
    },
    methods: {
        onAddNeighbor: function() {
            const id = this.newNeighborInput;
            if (this.connections.some(it => it.peer === id)) { // already connected
                return;
            }
            const conn = this._peer.connect(id);
            this._onConnected(conn);
        },
        onFindMin: function() {
            const opId = generateId();
            this._requestAll(opId);
            this._checkIfComputed(opId)
        },
        _onConnected: function(conn) {
            console.log("Connected with ", conn.peer);
            this.connections.push(conn);
            conn.on('data', data => {
                console.log(`Received data from ${conn.peer}: `, data);
                this._onData(conn, data)
            });
        },
        _onData: function(conn, data) {
            const { opId, type, value } = data;
            if (type === REQUEST_TYPE) {
                this._onRequest(conn, opId)
            } else if (type === RESPONSE_TYPE) {
                this._onResponse(conn, opId, value)
            } else {
                console.warn("Unexpected request type: ", type);
            }
        },
        _onRequest: function(conn, opId) {
            this.result = 0; // hide outdated result
            if (opId in this.operations) { // we already know about it
                this._respond(conn, opId, this.number);
            } else {
                this._requestAll(opId, conn.peer);
                this._checkIfComputed(opId)
            }
        },
        _onResponse: function(conn, opId, receivedValue) {
            const { value, waitingFor } = this.operations.opId;
            this.operations.opId.value = Math.min(receivedValue, value); // update value
            this.operations.opId.waitingFor = waitingFor.filter(it => it.peer !== conn.peer); // remove from waitingFor
            this._checkIfComputed(opId);
        },
        _requestAll: function(opId, parent = null) {
            const waitingFor = this.connections.filter(it => it.peer !== parent);
            this.operations.opId = { waitingFor, parent, value: this.number };
            waitingFor.forEach(conn => this._request(conn, opId));
        },
        _request: function(conn, opId) {
            conn.send({
                opId,
                type: REQUEST_TYPE
            });
        },
        _respond: function(conn, opId, value) {
            conn.send({
                opId,
                type: RESPONSE_TYPE,
                value
            });
        },
        _checkIfComputed: function(opId) {
            const op = this.operations.opId;
            if (op.waitingFor.length !== 0) {
                return;
            }
            if (op.parent === null) {
                this.result = op.value;
            } else {
                const parentConnection = this.connections.find(conn => conn.peer === op.parent);
                this._respond(parentConnection, opId, op.value);
            }
        }
    }
});