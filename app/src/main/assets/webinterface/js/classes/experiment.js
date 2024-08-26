import LinkedList from '../handlers/linkedList.js';

export default class Experiment {
  constructor(name) {
    this.name = name;
    this.sequence = new LinkedList(); // Linked list to manage blocks
  }

  addBlock(block) {
    this.sequence.append(block);
  }

  findBlockById(id) {
    return this.sequence.findById(id);
  }

  getSequenceArray() {
    return this.sequence.toArray();
  }
}