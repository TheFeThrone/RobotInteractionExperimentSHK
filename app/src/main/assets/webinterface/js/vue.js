var experimentElement = {
    props: ['step', 'currentState'],
    template: `<button v-if="step.index == currentState.index && !currentState.stopped" class="grid-item active" @click="buttonClick(step.index)">{{step.item}}</button><button v-else-if="step.index == currentState.index" class="grid-item stopped" @click="buttonClick(step.index)">{{step.item}}</button><button v-else class="grid-item" @click="buttonClick(step.index)">{{step.item}}</button>`,
    methods: {
        buttonClick: (index) => {
            vm.buttonClick(index);
        }
    },
    components: {
        "vm": vm
    }
}

var noConnection = {
    template: '<button class="noconnection" @click="reconnect">Unable to establish connection. Click to reconnect.</button>',
    methods: {
        reconnect: () => {
            vm.reconnect();
        }
    },
    components: {
        "vm": vm
    }
}

var parent = {
    props: ['connection', 'steps', 'currentState'],
    template: '<div v-if="connection" class="grid-container"><experiment-element v-for="step in steps" v-bind:step="step" v-bind:current-state="currentState"></experiment-element></div><div v-else class="general-container"><no-connection></no-connection></div>',
    components: {
        "experiment-element": experimentElement,
        "no-connection": noConnection
    }
}

var vm = new Vue({
  el: '#app',
  data: {
    currentState: {
        index: null,
        stopped: false
    },
    experiment: null,
    response: null,
    connection: null,
    steps: null
  },
  methods: {
    loadExperiment: async function() {
        var response = await fetch("/data");
        this.response = await response.json();
        this.experiment = this.response.steps;
        this.steps = this.experiment;
        console.log(this.steps);
    },
    init: function() {
        this.connection = new WebSocket("ws://" + location.host + "/websocket");

        this.connection.onmessage = (event) => {
            if (event.data != "Alive") {
                this.currentState = JSON.parse(event.data);
            }
        };
        this.connection.onopen = (event) => {
            console.log("Connected.");
        };
        this.connection.onclose = (event) => {
            console.log(event);
            this.connection = null;
        };
        this.connection.onerror = (event) => {
            console.log(event);
            this.connection = null;
        };
    },
    reconnect: () => {
        vm.loadExperiment();
        vm.init();
    },
    buttonClick(index) {
        this.connection.send("execute " + index);
    }
  },
  mounted: function() {
    this.loadExperiment();
    this.init();
  },
  components: {
    "experiment-element": experimentElement,
    "parent": parent,
    "no-connection": noConnection
  }
});
