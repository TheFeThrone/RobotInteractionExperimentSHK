var experiments = {
    props: ['experiment'],
    template: '<button class="grid-item" @click="buttonClick(experiment)">{{experiment}}</button>',
    methods: {
        buttonClick: (experiment) => {
            vm.experimentButtonClick(experiment);
        }
    },
    components: {
        "vm": vm
    }
}

var experimentEditor = {
    props: ['block', 'currentExperiment'],
    template: `<button v-if="block !== '+' && block !== 'Exit'" class="grid-item" @click="buttonClick(block)" :title="currentExperiment.experiment.sequence[block].value">{{currentExperiment.experiment.sequence[block].friendly_name}}</button><button v-else class="grid-item" @click="buttonClick(block)">{{block}}</button>`,
    methods: {
        buttonClick: (block) => {
            vm.editButtonClick(block);
        }
    },
    components: {
        "vm": vm
    }
}

var noInput = {
    template: '<div></div>'
}

var textInput = {
    props: ['currentBlock'],
    template: '<input type="text" class="text" v-model="currentBlock.value">'
}

var numberInput = {
    props: ['currentBlock'],
    template: '<input type="number" class="number" v-model="currentBlock.value">'
}

var coordinateInput = {
    props: ['currentBlock'],
    template: '<div><input type="number" class="number" v-model="currentBlock.value.x"><input type="number" class="number" v-model="currentBlock.value.y"><input type="number" class="number" v-model="currentBlock.value.z"></div>'
}

var fileInput = {
    props: ['currentBlock'],
    template: '<div></div>'
}

var newExperiment = {
    data: function() {
        return {
            newName: ""
        }
    },
    template: '<div class="two-element-container"><div><label for="new-experiment-name">Experiment name</label><br><input type="text" id="new-experiment-name" v-model="newName"></div><div></div><button class="grid-item" @click="createButtonClick(newName)">Create Experiment</button><button class="grid-item" @click="exitButtonClick">Cancel</button></div>',
    methods: {
        createButtonClick: (name) => {
            vm.createExperiment(name);
        },
        exitButtonClick: () => {
            vm.state = 0;
        }
    },
    components: {
        "vm": vm
    }
}

var blockEditor = {
    props: ['currentBlock'],
    template: `<div class="two-element-container"><div><label for="block-type-select">Block type</label><br><select class="select" id="block-type-select" v-model="currentBlock.friendly_name"><option value="Animation">Animation</option><option value="Display">Display</option><option value="Reset Display">Reset Display</option><option value="Look At Target">Look At Target</option><option value="Reset Look At">Reset Look At</option><option value="Move To">Move To</option><option value="Say">Say</option><option value="Sound">Sound</option><option value="Time">Time</option><option value="Empty">Empty</option></select></div><div><label for="input">Value</label><br><no-input v-if="currentBlock.friendly_name == 'Reset Display' || currentBlock.friendly_name == 'Reset Look At'" id="input"></no-input><text-input v-else-if="currentBlock.friendly_name == 'Say' || currentBlock.friendly_name == 'Empty'" id="input" v-bind:current-block="currentBlock"></text-input><number-input v-else-if="currentBlock.friendly_name == 'Time'" id="input" v-bind:current-block="currentBlock"></number-input><coordinate-input v-else-if="currentBlock.friendly_name == 'Look At Target' || currentBlock.friendly_name == 'Move To'" id="input" v-bind:current-block="currentBlock"></coordinate-input><file-input v-else-if="currentBlock.friendly_name == 'Animation' || currentBlock.friendly_name == 'Display' || currentBlock.friendly_name == 'Sound'" id="input" v-bind:current-block="currentBlock"></file-input></div><div><label for="stopping-checkbox">Stopping</label><br><input type="checkbox" id="stopping-checkbox" class="checkbox" v-model="currentBlock.stopping"></div><div><label for="interaction-checkbox">Requires user interaction</label><br><input type="checkbox" class="checkbox" v-model="currentBlock.requires_user_interaction"></div><button v-if="currentBlock.requires_user_interaction" v-for="possibility in currentBlock.possibilities" class="grid-item">{{possibility.friendly_name}}</button><button v-if="currentBlock.requires_user_interaction" class="grid-item">+</button><button class="grid-item" @click="buttonClick">Exit</button></div>`,
    methods: {
        buttonClick: () => {
            vm.returnFromBlockEditor();
        }
    },
    components: {
        "no-input": noInput,
        "text-input": textInput,
        "number-input": numberInput,
        "coordinate-input": coordinateInput,
        "file-input": fileInput,
        "vm": vm
    }
}

var state = {
    props: ['state', 'experiments', 'currentExperiment', 'currentBlock'],
    template: '<div v-if="state == 0" class="grid-container"><experiments v-for="experiment in experiments" v-bind:experiment="experiment"></experiments></div><div v-else-if="state == 1" class="grid-container"><experiment-editor v-for="block in currentExperiment.order" v-bind:current-experiment="currentExperiment" v-bind:block="block"></experiment-editor></div><new-experiment v-else-if="state == 2"></new-experiment><block-editor v-else-if="state == 3" v-bind:current-block="currentBlock"></block-editor>',
    components: {
        "experiments": experiments,
        "new-experiment": newExperiment,
        "experiment-editor": experimentEditor,
        "block-editor": blockEditor
    }
}

var vm = new Vue({
  el: '#app',
  data: {
    experiments: [],
    currentExperiment: {
        name: null,
        order: null,
        experiment: null
    },
    currentBlock: {
        name: null,
        friendly_name: null,
        value: null,
        stopping: true,
        requires_user_interaction: false,
        possibilities: null
    },
    state: 0
  },
  methods: {
    loadExperiments: async function() {
        var dataResponse = await fetch("/experiments");
        dataResponse = await dataResponse.text();
        this.experiments = dataResponse.split(",");
        this.experiments.push("+");
    },
    experimentButtonClick: async function(experiment) {
        if (experiment != "+") {
            var dataResponse = await fetch("/experiment?name=" + experiment);
            dataResponse = await dataResponse.json();
            this.currentExperiment.name = experiment;
            this.currentExperiment.experiment = dataResponse;
            this.currentExperiment.order = dataResponse.sequence.order.split(",");
            this.currentExperiment.order.push("+");
            this.currentExperiment.order.push("Exit");
            this.state = 1;
        } else {
            this.state = 2;
        }
    },
    returnFromBlockEditor: function() {
        if (this.currentBlock.name == null) {
            var name = "";
            switch(this.currentBlock.friendly_name) {
                case "Animation":
                name = "animation_";
                break;
                case "Display":
                name = "display_";
                break;
                case "Reset Display":
                name = "reset_display_";
                break;
                case "Look At Target":
                name = "look_at_";
                break;
                case "Reset Look At":
                name = "reset_look_";
                break;
                case "Move To":
                name = "move_to_";
                break;
                case "Say":
                name = "say_";
                break;
                case "Sound":
                name = "sound_";
                break;
                case "Time":
                name = "time_";
                break;
                case "Empty":
                name = "empty_";
                break;
                default:
                name = "empty_";
            };
            var counter = 0;
            for (block of this.currentExperiment.order) {
                if (block.startsWith(name)) {
                    counter++;
                }
            }
            this.currentExperiment.experiment.sequence[name + counter] = this.currentBlock;
            if (this.currentExperiment.experiment.sequence.order == "") {
                this.currentExperiment.experiment.sequence.order = name + counter;
            } else {
                this.currentExperiment.experiment.sequence.order = this.currentExperiment.experiment.sequence.order + "," + name + counter;
            }
            this.currentExperiment.order.splice(this.currentExperiment.order.length - 2, 0, name + counter);
        } else {
            this.currentExperiment.experiment.sequence[this.currentBlock.name] = this.currentBlock;
        }
        this.state = 1;
    },
    editButtonClick: async function(block) {
        if (block != "+" && block != "Exit") {
            this.currentBlock = this.currentExperiment.experiment.sequence[block];
            this.currentBlock.name = block;
            this.state = 3;
        } else if (block == "+") {
            this.currentBlock = {
                name: null,
                friendly_name: null,
                value: null,
                stopping: true,
                requires_user_interaction: false,
                possibilities: null
            };
            this.state = 3;
        } else if (block == "Exit") {
            var dataResponse = await fetch("/experiment?name=" + this.currentExperiment.name, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(this.currentExperiment.experiment)
            });
            this.state = 0;
        }
    },
    createExperiment: async function(name) {
        var dataResponse = await fetch("/experiment?name=" + name + ".json", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify("")
        });
        dataResponse = await dataResponse.text();
        this.experiments = dataResponse.split(",");
        this.experiments.push("+");
        this.state = 0;
    }
  },
  mounted: function() {
    this.loadExperiments();
  },
  components: {
    "state": state
  }
});
