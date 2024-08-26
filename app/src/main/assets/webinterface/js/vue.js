var experimentElement = {
    props: ['step', 'experimentState'],
    template: vueTemplates.experimentElement,
    methods: {
        buttonClick: (index) => {
            vm.jumpButtonClick(index);
        }
    },
    components: {
        "vm": vm
    }
}

var decisionElement = {
    props: ['element', 'decisionState'],
    template: vueTemplates.decisionElement,
    methods: {
        buttonClick: (index) => {
            vm.decisionButtonClick(index);
        }
    },
    components: {
        "vm": vm
    }
}

var noConnection = {
    template: vueTemplates.noConnection,
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
    props: ['steps', 'experimentState', 'decisionState', 'currentState'],
    template: vueTemplates.state,
    methods: {
        startExperiment: () => {
            vm.startExperiment();
        }
    },
    components: {
        "experiment-element": experimentElement,
        "decision-element": decisionElement,
        "vm": vm
    }
}

var parent = {
    props: ['connection', 'steps', 'experimentState', 'decisionState', 'currentState'],
    template: vueTemplates.parent,
    components: {
        "state": state,
        "no-connection": noConnection
    }
}

var vm = new Vue({
  el: '#app',
  data: {
    experimentState: {
        index: null
    },
    decisionState: {
        index: null
    },
    currentState: 0,
    experiment: null,
    response: null,
    connection: null,
    steps: []
  },
  methods: {
    loadExperiment: async function() {
        var dataResponse = await fetch("/data");
        var stateResponse = await fetch("/currentstate");
        this.response = await dataResponse.json();
        this.currentState = await stateResponse.json();
        var experimentSteps = this.response.sequence.order.split(',');
        var i = 0;
        this.steps = [];
        for (step of experimentSteps) {
            var value = this.response.sequence[step].value
            var shortValue = ""
            if (typeof value == "object") {
                var newValue = ""
                for (key in value) {
                    newValue += (key + ": " + value[key] + " ");
                }
                value = newValue
            }
            if (value.length >= 30) {
                shortValue = value.substring(0, 27) + "...";
            } else {
                shortValue = value;
            }
            var possibilities = [];
            var possibilitiesJson = this.response.sequence[step].possibilities;
            if (possibilitiesJson) {
                if (possibilitiesJson.order != "") {
                possibleSteps = possibilitiesJson.order.split(",");
                var j = 0;
                for (possibleStep of possibleSteps) {
                    var possibilityValue = possibilitiesJson[possibleStep].value
                    var shortPossibilityValue = ""
                    if (typeof possibilityValue == "object") {
                        var newPossibilityValue = ""
                        for (key in possibilityValue) {
                            newPossibilityValue += (key + ": " + possibilityValue[key] + " ");
                        }
                        possibilityValue = newPossibilityValue
                    }
                    if (possibilityValue.length >= 30) {
                        shortPossibilityValue = possibilityValue.substring(0, 27) + "...";
                    } else {
                        shortPossibilityValue = possibilityValue;
                    }
                    shortPossibilityValue = possibilityValue;
                    newPossibility = {index: j, item: possibleStep, friendlyName: possibilitiesJson[possibleStep].friendly_name, value: possibilityValue, shortValue: shortPossibilityValue}
                    possibilities.push(newPossibility)
                    j++;
                }
                possibilities.push({index: j, item: "exit", friendlyName: "Return without choice.", value: "", shortValue: ""})
                }
            }
            newStep = {index: i, item: step, friendlyName: this.response.sequence[step].friendly_name, value: value, shortValue: shortValue, possibilities: possibilities};
            this.steps.push(newStep);
            i++;
        }
    },
    init: function() {
        this.connection = new WebSocket("ws://" + location.host + "/websocket");
        this.connection.onmessage = (event) => {
            console.log(event.data);
            if (event.data.startsWith("new-state")) {
                this.currentState = event.data.split(" ")[1];
                if (this.currentState == 1) {
                    this.experimentState = {index: null};
                } else if (this.currentState == 2) {
                    this.decisionState = {index: null};
                } else if (this.currentState == 3) {
                    var logName = "";
                    fetch("/logname").then(res => res.text()).then(res => {
                        logName = res;
                    });
                    fetch("/log", {method: 'get', mode: 'no-cors', referrerPolicy: 'no-referrer'}).then(res => res.blob()).then(res => {
                          const aElement = document.createElement('a');
                          aElement.setAttribute('download', logName);
                          const href = URL.createObjectURL(res);
                          aElement.href = href;
                          aElement.setAttribute('target', '_blank');
                          aElement.click();
                          URL.revokeObjectURL(href);
                        });
                    fetch("/restart");
                }
            } else {
                if (this.currentState == 1) {
                    this.experimentState = JSON.parse(event.data);
                } else if (this.currentState == 2) {
                    this.decisionState = JSON.parse(event.data);
                }
            }
        };
        this.connection.onopen = (event) => {
        };
        this.connection.onclose = (event) => {
            this.connection = null;
        };
        this.connection.onerror = (event) => {
            this.connection = null;
        };
    },
    reconnect: () => {
        vm.loadExperiment();
        vm.init();
    },
    jumpButtonClick(index) {
        this.connection.send("{\"type\": jumpto, \"value\": " + index + "}");
    },
    decisionButtonClick(index) {
        this.connection.send("{\"type\": decision, \"value\": " + index + "}");
    },
    startExperiment() {
        this.connection.send("{\"type\": start}");
    }
  },
  mounted: function() {
    this.loadExperiment();
    this.init();
  },
  components: {
    "experiment-element": experimentElement,
    "decision-element": decisionElement,
    "parent": parent,
    "no-connection": noConnection,
    "state": state
  }
});
