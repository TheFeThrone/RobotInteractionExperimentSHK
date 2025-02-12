var experiments = {
    props: ['experiment'],
    template: '<button class="grid-item" @click="buttonClick(experiment)" @contextmenu.prevent="handler(experiment)">{{experiment}}</button>',
    methods: {
        buttonClick: (experiment) => {
            vm.experimentButtonClick(experiment);
        },
        handler: function(experiment) {
            vm.deleteExperiment(experiment);
        }
    },
    components: {
        "vm": vm
    }
}

var experimentEditor = {
    props: ['block', 'currentExperiment'],
    template: `<button v-if="block !== '+' && block !== 'Save & Exit'" class="grid-item" @click="buttonClick(block)" :title="currentExperiment.experiment.sequence[block].value">{{currentExperiment.experiment.sequence[block].friendly_name}}</button><br><button v-else class="grid-item" @click="buttonClick(block)">{{block}}</button>`,
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
    template: '<div>X:<input type="number" class="number" v-model="currentBlock.value.x">Y:<input type="number" class="number" v-model="currentBlock.value.y">Z:<input type="number" class="number" v-model="currentBlock.value.z"></div>'
}

var fileInput = {
    props: ['currentBlock'],
    template: '<input type="file" class="file" @change="uploadFile($event)">',
    methods: {
        uploadFile: (event) => {
            vm.file.data = event.target.files[0];
            if (vm.state == 3) {
                vm.file.type = vm.getFileType(vm.currentBlock.friendly_name);
                vm.currentBlock.value = vm.file.data.name;
            } else if (vm.state == 4) {
                vm.file.type = vm.getFileType(vm.currentPossibility.friendly_name);
                vm.currentPossibility.value = vm.file.data.name;
            }
        }
    },
    components: {
        "vm": vm
    }
}

var selectInput = {
    props: ['currentBlock'],
    data: function() {
        return {
            pointAtTypes: ['CLOSE_FRONT_LEFT', 'CLOSE_FRONT_RIGHT', 'CLOSE_MEDIUM_LEFT', 'CLOSE_MEDIUM_RIGHT', 'CLOSE_HALF_LEFT', 'CLOSE_HALF_RIGHT', 'FRONT_LEFT', 'FRONT_RIGHT', 'MEDIUM_LEFT', 'MEDIUM_RIGHT', 'HALF_LEFT', 'HALF_RIGHT']
        };
    },
    template: '<select class="select" id="block-type-select" v-model="currentBlock.value"><option disabled value="null">Select a Pointing-Animation</option><option v-for="anim in pointAtTypes" :value="anim">{{anim}}</option></select>'
}

var dynamicMultiTextInput = {
    props: ['currentBlock'],
    template: `
        <div>
            <div v-for="(input, index) in inputs" :key="index">
                <input type="text" class="text" v-model="inputs[index]">
                <button v-if="index === inputs.length - 1" @click="addInput">+</button>
            </div>
        </div>
    `,
    data() {
        return {
            inputs: [""]
        };
    },
    methods: {
        addInput() {
            this.inputs.push("");
        }
    },
    computed: {
        combinedString() {
            return this.inputs.filter(input => input.trim() !== "").join("¶");
        }
    },
    watch: {
        combinedString(newValue) {
            this.currentBlock.value = newValue;
        }
    }
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
    template: `<div class="two-element-container"><div><label for="block-type-select">Block type</label><br><select class="select" id="block-type-select" @change="typeChanged" v-model="currentBlock.friendly_name"><option value="Animation">Animation</option><option value="Display">Display</option><option value="Reset Display">Reset Display</option><option value="Look At Target">Look At Target</option><option value="Reset Look At">Reset Look At</option><option value="Move To">Move To</option><option value="Say">Say</option><option value="Sound">Sound</option><option value="Time">Time in ms</option><option value="Point At">Point At</option><option value="Random Say">Say One Of Many</option><option value="Empty">Empty</option></select></div><div><label for="input">Value</label><br><no-input v-if="currentBlock.friendly_name == 'Reset Display' || currentBlock.friendly_name == 'Reset Look At'" id="input"></no-input><text-input v-else-if="currentBlock.friendly_name == 'Say' || currentBlock.friendly_name == 'Empty'" id="input" v-bind:current-block="currentBlock"></text-input><number-input v-else-if="currentBlock.friendly_name == 'Time'" id="input" v-bind:current-block="currentBlock"></number-input><coordinate-input v-else-if="currentBlock.friendly_name == 'Look At Target' || currentBlock.friendly_name == 'Move To'" id="input" v-bind:current-block="currentBlock"></coordinate-input><file-input v-else-if="currentBlock.friendly_name == 'Animation' || currentBlock.friendly_name == 'Display' || currentBlock.friendly_name == 'Sound'" id="input" v-bind:current-block="currentBlock"></file-input><select-input v-else-if="currentBlock.friendly_name == 'Point At'" id="input" v-bind:current-block="currentBlock"></select-input><dynamic-multi-text-input v-else-if="currentBlock.friendly_name == 'Random Say'" v-bind:current-block="currentBlock"></dynamic-multi-text-input></div><div><label for="stopping-checkbox">Stopping</label><br><input type="checkbox" id="stopping-checkbox" class="checkbox" v-model="currentBlock.stopping"><label for="stopping-checkbox"></label></div><div><label for="interaction-checkbox">Requires user interaction</label><br><input type="checkbox" id="interaction-checkbox" class="checkbox" v-model="currentBlock.requires_user_interaction"><label for="interaction-checkbox"></label></div><button v-if="currentBlock.requires_user_interaction && !(typeof possibility === 'string')" v-for="possibility in currentBlock.possibilities" class="grid-item" @click="editPossibility(possibility)">{{possibility.friendly_name}}</button><button v-if="currentBlock.requires_user_interaction" class="grid-item" @click="addPossibility">+</button><button class="grid-item" @click="exitButtonClick">Save & Exit</button></div>`,
    methods: {
        exitButtonClick: () => {
            if (vm.currentBlock.friendly_name == null) {
                vm.currentBlock.friendly_name = "Empty";
            }
            if (vm.file.data) {
                vm.uploadFile();
            }
            vm.returnFromBlockEditor();
        },
        addPossibility: () => {
            // Create new possibilty object
            vm.currentPossibility = {
                name: null,
                friendly_name: null,
                value: null,
                stopping: true,
                jump: {
                    enabled: false,
                    target: "",
                    again: false
                }
            }
            // Transition to the state for editing the new possibility
            vm.state = 4;
        },
        editPossibility: (possibility) => {
            vm.currentPossibility = possibility;
            var againBool = true;
            if (vm.currentPossibility.jump.split(",")[1] == "0") {
                againBool = false;
            }
            if (vm.currentPossibility.jump != "") {
                vm.currentPossibility.jump = {
                    enabled: true,
                    target: vm.currentPossibility.jump.split(",")[0],
                    again: againBool
                };
            } else {
                vm.currentPossibility.jump = {
                    enabled: false,
                    target: "",
                    again: false
                };
            }
            vm.state = 4;
        },
        typeChanged: () => {
            if (vm.currentBlock.friendly_name == "Look At Target" || vm.currentBlock.friendly_name == "Move To") {
                vm.currentBlock.value = {
                    x: null,
                    y: null,
                    z: null
                };
            } else {
                vm.currentBlock.value = null;
            }
        }
    },
    components: {
        "no-input": noInput,
        "text-input": textInput,
        "number-input": numberInput,
        "coordinate-input": coordinateInput,
        "file-input": fileInput,
        "select-input": selectInput,
        "dynamic-multi-text-input": dynamicMultiTextInput,
        "vm": vm
    }
}

var possibilityEditor = {
    props: ['currentPossibility', 'currentExperiment'],
    template: `<div class="two-element-container"><div><label for="block-type-select">Possibility type</label><br><select class="select" id="block-type-select" @change="typeChanged" v-model="currentPossibility.friendly_name"><option value="Animation">Animation</option><option value="Display">Display</option><option value="Reset Display">Reset Display</option><option value="Look At Target">Look At Target</option><option value="Reset Look At">Reset Look At</option><option value="Move To">Move To</option><option value="Say">Say</option><option value="Sound">Sound</option><option value="Time">Time</option><option value="Point At">Point At</option><option value="Random Say">Say One Of Many</option><option value="Empty">Empty</option></select></div><div><label for="input">Value</label><br><no-input v-if="currentPossibility.friendly_name == 'Reset Display' || currentPossibility.friendly_name == 'Reset Look At'" id="input"></no-input><text-input v-else-if="currentPossibility.friendly_name == 'Say' || currentPossibility.friendly_name == 'Empty'" id="input" v-bind:current-block="currentPossibility"></text-input><number-input v-else-if="currentPossibility.friendly_name == 'Time'" id="input" v-bind:current-block="currentPossibility"></number-input><coordinate-input v-else-if="currentPossibility.friendly_name == 'Look At Target' || currentPossibility.friendly_name == 'Move To'" id="input" v-bind:current-block="currentPossibility"></coordinate-input><file-input v-else-if="currentPossibility.friendly_name == 'Animation' || currentPossibility.friendly_name == 'Display' || currentPossibility.friendly_name == 'Sound'" id="input" v-bind:current-block="currentPossibility"></file-input><select-input v-else-if="currentPossibility.friendly_name == 'Point At'" id="input" v-bind:current-block="currentPossibility"></select-input><dynamic-multi-text-input v-else-if="currentPossibility.friendly_name == 'Random Say'" v-bind:current-block="currentPossibility"></dynamic-multi-text-input></div><div><label for="stopping-checkbox">Stopping</label><br><input type="checkbox" id="stopping-checkbox" class="checkbox" v-model="currentPossibility.stopping"><label for="stopping-checkbox"></label></div><div><label for="jump-checkbox">Jump after decision</label><br><input type="checkbox" class="checkbox" id="jump-checkbox" v-model="currentPossibility.jump.enabled"><label for="jump-checkbox"></label></div><div v-if="currentPossibility.jump.enabled"><label for="target-block">Target block</label><select id="target-block" class="select" v-model="currentPossibility.jump.target"><option v-for="block in currentExperiment.order" v-if="block !== '+' && block !== 'Save & Exit'" :value="block">{{currentExperiment.experiment.sequence[block].friendly_name}}: {{currentExperiment.experiment.sequence[block].value}}</option></select></div><div v-if="currentPossibility.jump.enabled"><label for="again-checkbox">Execute block again</label><br><input id="again-checkbox" type="checkbox" class="checkbox" v-model="currentPossibility.jump.again"><label for="again-checkbox"></label></div><button class="grid-item" @click="exitButtonClick">Save & Exit</button></div>`,
    methods: {
        exitButtonClick: () => {
            if (!vm.currentPossibility.jump.enabled || vm.currentPossibility.jump.target == "") {
                vm.currentPossibility.jump = "";
            } else {
                if (vm.currentPossibility.jump.again) {
                    vm.currentPossibility.jump = vm.currentPossibility.jump.target + ",1";
                } else {
                    vm.currentPossibility.jump = vm.currentPossibility.jump.target + ",0";
                }
            }
            if (vm.currentPossibility.friendly_name == null) {
                vm.currentPossibility.friendly_name = "Empty";
            }
            if (vm.file.data) {
                vm.uploadFile();
            }
            vm.returnFromPossibilityEditor();
        },
        typeChanged: () => {
            if (vm.currentPossibility.friendly_name == "Look At Target" || vm.currentPossibility.friendly_name == "Move To") {
                vm.currentPossibility.value = {
                    x: null,
                    y: null,
                    z: null
                };
            } else {
                vm.currentPossibility.value = null;
            }
        }
    },
    components: {
        "no-input": noInput,
        "text-input": textInput,
        "number-input": numberInput,
        "coordinate-input": coordinateInput,
        "file-input": fileInput,
        "select-input": selectInput,
        "dynamic-multi-text-input": dynamicMultiTextInput,
        "vm": vm
    }
}

var state = {
    props: ['state', 'experiments', 'currentExperiment', 'currentBlock', 'currentPossibility'],
    template: '<div v-if="state == 0" class="grid-container"><experiments v-for="experiment in experiments" v-bind:experiment="experiment"></experiments></div><div v-else-if="state == 1" class="grid-container"><experiment-editor v-for="block in currentExperiment.order" v-bind:current-experiment="currentExperiment" v-bind:block="block"></experiment-editor></div><new-experiment v-else-if="state == 2"></new-experiment><block-editor v-else-if="state == 3" v-bind:current-block="currentBlock"></block-editor><possibility-editor v-else-if="state == 4" v-bind:current-possibility="currentPossibility" v-bind:current-experiment="currentExperiment"></possibility-editor>',
    components: {
        "experiments": experiments,             //0
        "new-experiment": newExperiment,        //2
        "experiment-editor": experimentEditor,  //1
        "block-editor": blockEditor,            //3
        "possibility-editor": possibilityEditor //4
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
        possibilities: {
            order: "",
        }
    },
    currentPossibility: {
        name: null,
        friendly_name: null,
        value: null,
        stopping: true,
        jump: ""
    },
    file: {
        type: null,
        data: null
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
            this.currentExperiment.order.push("Save & Exit");
            this.state = 1;
        } else {
            this.state = 2;
        }
    },
    returnFromBlockEditor: function() {
        this.setBlockName();
        this.state = 1;
    },
    setBlockName: function() {
        var name = this.getBlockName(this.currentBlock.friendly_name, this.currentExperiment.order);
       console.log("Test", "Generated block name: " + name);

       // Check if the block name is null (hasn't been named yet)
        if (this.currentBlock.name == null) {
            // Assign the generated name to the current block
            this.currentBlock.name = name;
            // Add the block to the experiments sequence using the name as key
            this.currentExperiment.experiment.sequence[name] = this.currentBlock;

            // If the block order is empty, set the order to the new order name
            if (this.currentExperiment.experiment.sequence.order === "") {
                this.currentExperiment.experiment.sequence.order = name;
            } else {
                // Otherwise, append the new possibility name to the possibilities order
                this.currentExperiment.experiment.sequence.order = this.currentExperiment.experiment.sequence.order + "," + name;
            }
            // ignore + and save in UI order
            this.currentExperiment.order.splice(this.currentExperiment.order.length - 2, 0, name);
        } else {
            // If the block already has a name, check if the base name (before the underscore) has changed
            var oldNameBase = this.currentBlock.name.substring(0, this.currentBlock.name.lastIndexOf('_'));
            var newNameBase = name.substring(0, name.lastIndexOf('_'));

            if (oldNameBase !== newNameBase) {
                // Remove the old block name from the block object
                delete this.currentExperiment.experiment.sequence[this.currentBlock.name];
                console.log("Test", "Replacing block: " + this.currentBlock.name + " with: " + name);
                // Replace the old block name with the new name in the block order for ui and normal
                this.currentExperiment.experiment.sequence.order = this.currentExperiment.experiment.sequence.order.replace(this.currentBlock.name, name);
                this.currentExperiment.order[this.currentExperiment.order.indexOf(this.currentBlock.name)] = name;
                // Set the new block name
                this.currentBlock.name = name;
                // Add the block to the sequence object under the new name
                this.currentExperiment.experiment.sequence[name] = this.currentBlock;
            } else {
                // If the name hasn't changed, just update the block in the sequence object with the existing name
                this.currentExperiment.experiment.sequence[this.currentBlock.name] = this.currentBlock;
            }
        }
        console.log("Test", "Final state of current experiment: " + JSON.stringify(this.currentExperiment.experiment, null, 2));
    },
    returnFromPossibilityEditor: function() {
        this.setPossibilityName();
        this.state = 3;
    },
    setPossibilityName: function() {
        // Generate a possibility name using the friendly name and the block's possibilities order
        var name = this.getBlockName(this.currentPossibility.friendly_name, this.currentBlock.possibilities.order);
        console.log("Test", "Generated possibility name: " + name);

        // Check if the possibility name is null (hasn't been named yet)
        if (this.currentPossibility.name == null) {
            // Assign the generated name to the current possibility
            this.currentPossibility.name = name;
            // Add the possibility to the block's possibilities object using the name as key
            this.currentBlock.possibilities[name] = this.currentPossibility;

            // If the possibilities order is empty, set the order to the new possibility name
            if (this.currentBlock.possibilities.order === "") {
                this.currentBlock.possibilities.order = name;
            } else {
                // Otherwise, append the new possibility name to the possibilities order
                this.currentBlock.possibilities.order = this.currentBlock.possibilities.order + "," + name;
            }
        } else {
            // If the possibility already has a name, check if the base name (before the underscore) has changed
            var oldNameBase = this.currentPossibility.name.substring(0, this.currentPossibility.name.lastIndexOf('_'));
            var newNameBase = name.substring(0, name.lastIndexOf('_'));

            if (oldNameBase !== newNameBase) {
                // Remove the old possibility name from the possibilities object
                console.log("Test", "Replacing possibility: " + this.currentPossibility.name + " with: " + name);
                delete this.currentBlock.possibilities[this.currentPossibility.name];
                // Replace the old possibility name with the new name in the possibilities order
                this.currentBlock.possibilities.order = this.currentBlock.possibilities.order.replace(this.currentPossibility.name, name);
                // Set the new possibility name
                this.currentPossibility.name = name;
                // Add the possibility to the possibilities object under the new name
                this.currentBlock.possibilities[name] = this.currentPossibility;
            } else {
                // If the name hasn't changed, just update the possibility in the possibilities object with the existing name
                this.currentBlock.possibilities[this.currentPossibility.name] = this.currentPossibility;
            }
        }
        console.log("Test", "Final state of current block: " + JSON.stringify(this.currentBlock, null, 2));
    },
    editButtonClick: async function(block) {
        if (block != "+" && block != "Save & Exit") {
            this.currentBlock = JSON.parse(JSON.stringify(this.currentExperiment.experiment.sequence[block]));
            this.currentBlock.name = block;
            this.state = 3;
        } else if (block == "+") {
            this.currentBlock = {
                name: null,
                friendly_name: null,
                value: null,
                stopping: true,
                requires_user_interaction: false,
                possibilities: {
                    order: ""
                }
            };
            console.log("Created new block with empty possibilities");
            this.state = 3;
        } else if (block == "Save & Exit") {
            var dataResponse = await fetch("/experiment?name=" + this.currentExperiment.name, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(this.currentExperiment.experiment)
            });
            this.state = 0;
        } else if (block == "Delete") {
            this.deleteExperiment(this.currentExperiment.name);
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
    },
    deleteExperiment: async function(experimentName){
        const confirmed = confirm(`Are you sure you want to delete the experiment "${experimentName}"?`);
        if (confirmed) {
          // Send a DELETE request to your server to delete the experiment
          try {
            await fetch(`/experiment?name=${experimentName}`, {
              method: 'DELETE',
            });
            // Update the UI to remove the experiment from the list
            this.experiments = this.experiments.filter(experiment => experiment !== experimentName);
            console.log("Test",`Experiment "${experimentName}" has been deleted.`);
          } catch (error) {
            console.error('Error deleting experiment:', error);
            console.log("Test",`Error deleting experiment "${experimentName}".`);
          }
        }
    },
    uploadFile: async function() {
        var data = new FormData();
        data.append("file", this.file.data);
        var dataResponse = await fetch("/file?name=" + this.file.data.name + "&type=" + this.file.type, {
            method: "PUT",
            body: data
        });
        this.file = {
            type: null,
            data: null
        }
    },
    getFileType: function(name) {
        const typeMapping = {
            "Animation": "animation",
            "Display": "picture",
            "Sound": "sound"
        };
        var type = typeMapping[name] || "empty";
        return type;
    },
    getBlockName: function(friendly_name, order) {
        const nameMapping = {
            "Animation": "animation_",
            "Display": "display_",
            "Reset Display": "reset_display_",
            "Look At Target": "look_at_",
            "Reset Look At": "reset_look_",
            "Move To": "move_to_",
            "Say": "say_",
            "Sound": "sound_",
            "Time": "time_",
            "Point At": "point_at_",
            "Random Say": "random_say_",
            "Empty": "empty_"
        };

        // If friendly_name is invalid, log and return early
        if (!friendly_name || !nameMapping[friendly_name]) {
            console.log("Test: Invalid friendly_name", friendly_name);
            friendly_name = "Empty"; // Default to "Empty" to avoid errors, but you can handle this differently
        }

        var name = nameMapping[friendly_name];
        var counter = 0;
        var orderArray = [];

        if (typeof order === "string") {
            if (order.trim() !== "") {
                orderArray = order.split(",").map(item => item.trim()); // Split and trim to handle extra spaces
            }
        } else if (Array.isArray(order)) {
            orderArray = order; // Direct assignment if already an array
        } else {
            console.log("Test: Invalid order type:", (typeof order));
        }

        // Loop through existing blocks and update counter for naming
        orderArray.forEach(block => {
            if (block.startsWith(name)) {
                var suffixNumber = parseInt(block.substring(name.length), 10); // Parse the number suffix
                if (!isNaN(suffixNumber) && suffixNumber >= counter) {
                    counter = suffixNumber + 1;
                }
            }
        });

        var countedName = name + counter;
        return countedName;
    }
  },
  mounted: function() {
    this.loadExperiments();
  },
  components: {
    "state": state
  }
});