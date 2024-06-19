var vm = new Vue({
  el: '#app',
  data: {
    config: {
        experiment: "experiment.json",
        language: "de_DE",
        speech_speed: 75,
        speech_pitch: 80,
        autonomous_activity: true
    },
    experiments: null
  },
  methods: {
    loadConfig: async function() {
        var dataResponse = await fetch("/config");
        this.config = await dataResponse.json();
    },
    loadExperiments: async function() {
        var dataResponse = await fetch("/experiments");
        dataResponse = await dataResponse.text();
        this.experiments = dataResponse.split(",");
    }
  },
  mounted: function() {
    this.loadConfig();
    this.loadExperiments();
  },
  components: {

  },
  watch: {
    config: {
        deep: true,
        handler: async function(value) {
            var dataResponse = await fetch("/config", {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(this.config)
            });
        }
    }
  }
});
