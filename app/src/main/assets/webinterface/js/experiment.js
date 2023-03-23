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
    template: `<button v-if="block != '+'" class="grid-item" @click="buttonClick(block)" :title="currentExperiment.experiment.sequence[block].value">{{currentExperiment.experiment.sequence[block].friendly_name}}</button><button v-else class="grid-item" @click="buttonClick(block)" title="Add block">{{block}}</button>`,
    methods: {
        buttonClick: (block) => {
            vm.editButtonClick(block);
        }
    },
    components: {
        "vm": vm
    }
}

var blockEditor = {
    props: ['currentBlock'],
    template: '<div class="two-element-container"><div><label for="block-type-select">Block type</label><br><select class="select" id="block-type-select" v-model="currentBlock.friendly_name"><option value="Animation">Animation</option><option value="Display">Display</option><option value="Reset Display">Reset Display</option><option value="Look At Target">Look At Target</option><option value="Reset Look At">Reset Look At</option><option value="Move To">Move To</option><option value="Say">Say</option><option value="Sound">Sound</option><option value="Time">Time</option><option value="Empty">Empty</option></select></div><div></div><div><label for="stopping-checkbox">Stopping</label><br><input type="checkbox" id="stopping-checkbox" class="checkbox" v-model="currentBlock.stopping"></div><div><label for="interaction-checkbox">Requires user interaction</label><br><input type="checkbox" class="checkbox" v-model="currentBlock.requires_user_interaction"></div></div>'
}

var state = {
    props: ['state', 'experiments', 'currentExperiment', 'currentBlock'],
    template: '<div v-if="state == 0" class="grid-container"><experiments v-for="experiment in experiments" v-bind:experiment="experiment"></experiments></div><div v-else-if="state == 1" class="grid-container"><experiment-editor v-for="block in currentExperiment.order" v-bind:current-experiment="currentExperiment" v-bind:block="block"></experiment-editor></div><block-editor v-else-if="state == 3" v-bind:current-block="currentBlock"></block-editor>',
    components: {
        "experiments": experiments,
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
        stopping: null,
        requires_user_interaction: null,
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
            this.state = 1;
        } else {
            this.state = 2;
        }
    },
    editButtonClick(block) {
        this.currentBlock = this.currentExperiment.experiment.sequence[block];
        this.currentBlock.name = block;
        console.log(this.currentBlock);
        this.state = 3;
    }
  },
  mounted: function() {
    this.loadExperiments();
  },
  components: {
    "state": state
  }
});
