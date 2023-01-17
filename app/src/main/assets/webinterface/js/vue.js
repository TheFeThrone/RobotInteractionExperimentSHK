var experimentElement = {
    props: ['step', 'experimentState'],
    template: `<button v-if="step.index == experimentState.index && !experimentState.stopped" class="grid-item active" @click="buttonClick(step.index)">{{step.item}}</button><button v-else-if="step.index == experimentState.index" class="grid-item stopped" @click="buttonClick(step.index)">{{step.item}}</button><button v-else class="grid-item" @click="buttonClick(step.index)">{{step.item}}</button>`,
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

var state = {
    props: ['steps', 'experimentState', 'currentState'],
    template: '<div v-if="currentState == 0" class="one-element-container"></div><div v-else-if="currentState == 1" class="grid-container"><experiment-element v-for="step in steps" v-bind:step="step" v-bind:experiment-state="experimentState"></experiment-element></div>',
    components: {
        "experiment-element": experimentElement
    }
}

var parent = {
    props: ['connection', 'steps', 'experimentState', 'currentState'],
    template: '<div v-if="connection" class="test"><state v-bind:steps="steps" v-bind:experiment-state="experimentState" v-bind:current-state="currentState"></state></div><div v-else class="one-element-container"><no-connection></no-connection></div>',
    components: {
        "state": state,
        "no-connection": noConnection
    }
}

var vm = new Vue({
  el: '#app',
  data: {
    experimentState: {
        index: null,
        stopped: false
    },
    currentState: 1,
    experiment: null,
    response: null,
    connection: null,
    steps: []
  },
  methods: {
    loadExperiment: async function() {
        var response = await fetch("/data");
        this.response = await response.json();
        this.experiment = this.response.sequence.order.split(',');
        var i = 0;
        this.steps = [];
        for (step of this.experiment) {
            newStep = {index: i, item: step};
            this.steps.push(newStep);
            i++;
        }
    },
    init: function() {
        this.connection = new WebSocket("ws://" + location.host + "/websocket");

        this.connection.onmessage = (event) => {
            this.experimentState = JSON.parse(event.data);
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
    "no-connection": noConnection,
    "state": state
  }
});
