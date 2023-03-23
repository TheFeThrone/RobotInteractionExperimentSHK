var vm = new Vue({
  el: '#app',
  data: {
    config: null
  },
  methods: {
    loadConfig: async function() {
        var dataResponse = await fetch("/config");
        this.config = await dataResponse.json();
    }
  },
  mounted: function() {
    this.loadConfig();
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
