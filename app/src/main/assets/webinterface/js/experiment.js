var experiments = {
    props: ['experiment'],
    template: experimentTemplates.experiments,
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
    template: experimentTemplates.editor.experiment,
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
    template: experimentTemplates.input.none,
}

var textInput = {
    props: ['currentBlock'],
    template: experimentTemplates.input.text,
}

var numberInput = {
    props: ['currentBlock'],
    template: experimentTemplates.input.number,
}

var coordinateInput = {
    props: ['currentBlock'],
    template: experimentTemplates.input.coordinate
}

var fileInput = {
    props: ['currentBlock'],
    template: experimentTemplates.input.file,
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
    template: experimentTemplates.input.select
}

var newExperiment = {
    data: function() {
        return {
            newName: ""
        }
    },
    template: experimentTemplates.newExperiment,
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
    template: experimentTemplates.editor.block,
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
            vm.setPossibilityName();

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
        "vm": vm
    }
}

var possibilityEditor = {
    props: ['currentPossibility', 'currentExperiment'],
    template: experimentTemplates.editor.possibility,
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
        "vm": vm
    }
}

var state = {
    props: ['state', 'experiments', 'currentExperiment', 'currentBlock', 'currentPossibility'],
    template: experimentTemplates.state,
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
            order: ""
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
        if (this.currentBlock.name == null) {
            this.currentBlock.name = name;
            this.currentExperiment.experiment.sequence[name] = this.currentBlock;
            if (this.currentExperiment.experiment.sequence.order == "") {
                this.currentExperiment.experiment.sequence.order = name;
            } else {
                this.currentExperiment.experiment.sequence.order = this.currentExperiment.experiment.sequence.order + "," + name;
            }
            this.currentExperiment.order.splice(this.currentExperiment.order.length - 2, 0, name);
        } else {
            if (this.currentBlock.name.substring(0, this.currentBlock.name.lastIndexOf('_')) != name.substring(0, name.lastIndexOf('_'))) {
                delete this.currentExperiment.experiment.sequence[this.currentBlock.name];
                this.currentExperiment.experiment.sequence.order = this.currentExperiment.experiment.sequence.order.replace(this.currentBlock.name, name);
                this.currentExperiment.order[this.currentExperiment.order.indexOf(this.currentBlock.name)] = name;
                this.currentBlock.name = name;
                this.currentExperiment.experiment.sequence[name] = this.currentBlock;
            } else {
                this.currentExperiment.experiment.sequence[this.currentBlock.name] = this.currentBlock;
            }
        }
    },
    returnFromPossibilityEditor: function() {
        this.setPossibilityName();
        this.state = 3;
    },
    setPossibilityName: function() {
        var name = this.getBlockName(this.currentPossibility.friendly_name, this.currentBlock.possibilities.order);
        if (this.currentPossibility.name == null) {
            this.currentPossibility.name = name;
            this.currentBlock.possibilities[name] = this.currentPossibility;
            if (this.currentBlock.possibilities.order == "" ) {
                this.currentBlock.possibilities.order = name;
            } else {
                this.currentBlock.possibilities.order = this.currentBlock.possibilities.order + "," + name;
            }
            //this.currentBlock.possibilities.order.splice(this.currentBlock.possibilities.order.length - 2, 0, name);
        } else {
            if (this.currentBlock.name.substring(0, this.currentBlock.name.lastIndexOf('_')) != name.substring(0, name.lastIndexOf('_'))) {
                delete this.currentBlock.possibilities[this.currentPossibility.name];
                this.currentBlock.possibilities.order = this.currentBlock.possibilities.order.replace(this.currentPossibility.name, name);
                this.currentPossibility.name = name;
                this.currentBlock.possibilities[name] = this.currentPossibility;
            } else {
                this.currentBlock.possibilities[this.currentPossibility.name] = this.currentPossibility;
            }
        }
    },
    editButtonClick: async function(block) {
        if (block != "+" && block != "Save & Exit") {
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
                possibilities: {
                    order: ""
                }
            };
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
            alert(`Experiment "${experimentName}" has been deleted.`);
          } catch (error) {
            console.error('Error deleting experiment:', error);
            alert(`Error deleting experiment "${experimentName}".`);
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
            "Empty": "empty_"
        };
        var name = nameMapping[friendly_name] || "empty_";
        var counter = 0;

        if (typeof order === 'string' && order.trim() !== "") {
            orderArray = order.split(",").map(item => item.trim()); // Split and trim to handle extra spaces
        } else if (Array.isArray(order)) {
            orderArray = order; // Direct assignment if already an array
        }

        orderArray.forEach(block => {
            if (block.startsWith(name)) {
                var suffixNumber = parseInt(block.substring(name.length), 10); // Correctly parse the number suffix
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