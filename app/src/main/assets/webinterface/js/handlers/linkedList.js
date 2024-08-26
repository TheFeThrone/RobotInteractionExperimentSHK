class Node {
  constructor(data) {
    this.data = data;
    this.next = null;
  }
}

export default class LinkedList {
  constructor() {
    this.head = null;
    this.tail = null;
  }

  append(data) {
    const newNode = new Node(data);
    if (this.tail) {
      this.tail.next = newNode;
    } else {
      this.head = newNode;
    }
    this.tail = newNode;
  }

  findById(id) {
    let current = this.head;
    while (current) {
      if (current.data.id === id) {
        return current.data;
      }
      current = current.next;
    }
    return null;
  }

  toArray() {
    const array = [];
    let current = this.head;
    while (current) {
      array.push(current.data);
      current = current.next;
    }
    return array;
  }
}
