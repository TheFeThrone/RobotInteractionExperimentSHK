var vm = new Vue({
  el: '#app',
  data: {
    response: null,
    currentState: null
  },
  methods: {
    loadData: async function () {
        var response = await fetch("/data");
        this.response = await response.json();
        this.currentState = this.response.data;
        setTimeout(this.loadData, 1000);
    }
  },
  mounted: function() {
    //Initial Load
    this.loadData();
    //Run every 30 seconds
    /*var loadData = function(){
        loadData();
        // Do stuff
        setTimeout(this.loadData, 1000);
   };
     setTimeout(loadData(this), 1000);*/
  }
});
