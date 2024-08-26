export default class Block {
  constructor(id, content) {
    this.id = id;
    this.content = content;
    this.possibilities = []; // Array of Possibility instances
  }

  addPossibility(possibility) {
    this.possibilities.push(possibility);
  }
}

